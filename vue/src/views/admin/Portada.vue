<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api, imgSrc } from '../../lib/api'
import { useSubmit } from '../../composables/useSubmit'
const g = useSubmit()

// Sliders por clave: se crea el slider (portada, login,…) y luego solo se incrusta
// el bloque donde corresponda: <SliderBlock clave="…" /> (ver composables/useSlider).
const sliders = ref<any[]>([])
const sel = ref<any>(null)
const nuevaClave = ref({ nombre: '' })
const slides = ref<any[]>([])
const form = ref({ imagenUrl: '', titulo: '', subtitulo: '', mensajeWsp: '' })
const editId = ref('')
const msg = ref('')

async function loadSliders() {
  const { data } = await api.get('/admin/sliders').catch(() => ({ data: [] }))
  sliders.value = data
  if (!sel.value && data.length) await seleccionar(data[0])
}
async function crearSlider() {
  if (!nuevaClave.value.nombre.trim()) return
  const { data } = await api.post('/admin/sliders', { nombre: nuevaClave.value.nombre.trim() }).catch((e: any) => {
    alert(e?.response?.data?.message ?? 'No se pudo crear'); return { data: null }
  })
  nuevaClave.value = { nombre: '' }
  await loadSliders()
  if (data) await seleccionar(data)
}
async function eliminarSlider(id: string) {
  if (!confirm('¿Eliminar slider y sus slides?')) return
  await api.delete(`/admin/sliders/${id}`)
  sel.value = null
  await loadSliders()
}
async function seleccionar(s: any) {
  sel.value = s
  editId.value = ''
  form.value = { imagenUrl: '', titulo: '', subtitulo: '', mensajeWsp: '' }
  const { data } = await api.get('/admin/portada/slides', { params: { sliderId: s.id } }).catch(() => ({ data: [] }))
  slides.value = data
}
async function subir(e: Event, target: any) {
  const f = (e.target as HTMLInputElement).files?.[0]
  if (!f) return
  const fd = new FormData()
  fd.append('archivo', f)
  const { data } = await api.post('/admin/portada/imagen', fd).catch((err: any) => {
    alert(err?.response?.data?.message ?? 'No se pudo subir')
    return { data: null }
  })
  if (data) target.imagenUrl = data.imagenUrl
  ;(e.target as HTMLInputElement).value = ''
}
async function guardar() {
  if (!form.value.titulo.trim()) { alert('Título obligatorio'); return }
  if (editId.value) await api.put(`/admin/portada/slides/${editId.value}`, { ...form.value, activo: true })
  else await api.post('/admin/portada/slides', { ...form.value, sliderId: sel.value.id })
  form.value = { imagenUrl: '', titulo: '', subtitulo: '', mensajeWsp: '' }
  editId.value = ''
  msg.value = 'Slide guardado.'
  setTimeout(() => (msg.value = ''), 2500)
  await seleccionar(sel.value)
}
function editar(s: any) {
  editId.value = s.id
  form.value = { imagenUrl: s.imagenUrl, titulo: s.titulo, subtitulo: s.subtitulo, mensajeWsp: s.mensajeWsp }
}
async function eliminar(id: string) {
  if (!confirm('¿Eliminar slide?')) return
  await api.delete(`/admin/portada/slides/${id}`)
  await seleccionar(sel.value)
}
async function toggle(s: any) {
  await api.put(`/admin/portada/slides/${s.id}`, { ...s, activo: !s.activo })
  await seleccionar(sel.value)
}
async function mover(i: number, delta: number) {
  const j = i + delta
  if (j < 0 || j >= slides.value.length) return
  const orden = slides.value.map((s: any) => s.id)
  const [x] = orden.splice(i, 1)
  orden.splice(j, 0, x)
  await api.put('/admin/portada/slides/reordenar', { orden })
  await seleccionar(sel.value)
}
onMounted(loadSliders)
</script>
<template>
  <div>
    <div style="display:flex;gap:1rem;flex-wrap:wrap">
      <div style="min-width:230px">
        <h2>Sliders</h2>
        <ul>
          <li v-for="s in sliders" :key="s.id">
            <button @click="seleccionar(s)" :style="{ fontWeight: sel?.id === s.id ? 'bold' : 'normal' }">{{ s.nombre }} <small>({{ s.clave }})</small></button>
            <button v-if="s.clave !== 'portada' && s.clave !== 'login' && s.clave !== 'recomendaciones'" @click="g.ejecutar(() => eliminarSlider(s.id))" :disabled="g.ocupado.value">✕</button>
          </li>
        </ul>
        <h3>Nuevo slider</h3>
        <input v-model="nuevaClave.nombre" placeholder="Nombre * ej. Promo llantas" />
        <button @click="g.ejecutar(crearSlider)" :disabled="g.ocupado.value">Crear</button>
        <p><small>La clave se genera sola desde el nombre.</small></p>
        <p><small>Para mostrarlo, incrusta el bloque con su clave donde corresponda.</small></p>
      </div>
      <div v-if="sel" style="flex:1;min-width:300px">
        <h2>Slides de “{{ sel.nombre }}” ({{ slides.length }})</h2>
        <p v-if="sel.clave === 'recomendaciones'"><small>Estas tarjetas salen en Inicio y Mi perfil (carrusel de recomendaciones). La imagen es opcional; el mensaje WhatsApp no se usa aquí.</small></p>
        <div style="display:flex;flex-direction:column;gap:.5rem;max-width:560px">
          <img v-if="form.imagenUrl" :src="imgSrc(form.imagenUrl)" alt="Flyer" style="width:100%;max-height:180px;object-fit:cover;border:1px solid #ccc;border-radius:8px" />
      <label>Subir flyer desde tu dispositivo *
        <input type="file" accept="image/*" @change="(e) => subir(e, form)" />
      </label>
          <label>Título * <input v-model="form.titulo" required style="width:100%" /></label>
          <label>Descripción <input v-model="form.subtitulo" style="width:100%" /></label>
          <label>Mensaje WhatsApp * <input v-model="form.mensajeWsp" required style="width:100%" /></label>
          <div style="display:flex;gap:.5rem">
            <button class="primary" @click="g.ejecutar(guardar)" :disabled="g.ocupado.value">{{ editId ? 'Actualizar slide' : 'Agregar slide' }}</button>
            <button v-if="editId" @click="editId = ''; form = { imagenUrl: '', titulo: '', subtitulo: '', mensajeWsp: '' }">Cancelar</button>
          </div>
          <p v-if="msg">{{ msg }}</p>
        </div>
        <table style="margin-top:.6rem">
          <thead><tr><th></th><th>Flyer</th><th>Título</th><th>Estado</th><th>Acciones</th></tr></thead>
          <tbody>
            <tr v-for="(s, i) in slides" :key="s.id">
              <td>#{{ i + 1 }}</td>
              <td><img :src="imgSrc(s.imagenUrl)" alt="" style="width:110px;height:60px;object-fit:cover" /></td>
              <td><strong>{{ s.titulo }}</strong><br /><small>{{ s.subtitulo }}</small></td>
              <td>{{ s.activo ? 'visible' : 'oculto' }}</td>
              <td style="white-space:nowrap">
                <button @click="g.ejecutar(() => mover(i, -1))" :disabled="i === 0 || g.ocupado.value">↑</button>
                <button @click="g.ejecutar(() => mover(i, 1))" :disabled="i === slides.length - 1 || g.ocupado.value">↓</button>
                <button @click="g.ejecutar(() => toggle(s))" :disabled="g.ocupado.value">{{ s.activo ? 'Ocultar' : 'Mostrar' }}</button>
                <button @click="editar(s)">Editar</button>
                <button @click="g.ejecutar(() => eliminar(s.id))" :disabled="g.ocupado.value">Eliminar</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
