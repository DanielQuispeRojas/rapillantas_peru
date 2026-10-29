<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api, imgSrc } from '../../lib/api'
import { useSubmit } from '../../composables/useSubmit'
const g = useSubmit()

// Ofertas = promociones con descripción larga (ej. "4+ llantas Bridgestone → gorro gratis").
// Reglas: inicio >= hoy, fin > inicio, máximo 3 imágenes (del dispositivo).
const items = ref<any[]>([])
const showForm = ref(false)
const editId = ref('')
const hoy = new Date().toISOString().slice(0, 10)
const form = ref({ titulo: '', descripcion: '', precioOferta: null as any, vigenciaDesde: hoy, vigenciaHasta: '', activa: true })
const imgs = ref<(string | null)[]>([null, null, null])
const pendientes = ref<(File | null)[]>([null, null, null])
const previews = ref<(string | null)[]>([null, null, null])

async function load() {
  const { data } = await api.get('/admin/ofertas').catch(() => ({ data: [] }))
  items.value = data
}
function fmtFecha(f: any) {
  if (!f) return '—'
  const s = Array.isArray(f) ? f.join('-') : String(f)
  const p = s.slice(0, 10).split('-')
  return p.length === 3 ? `${p[2]}/${p[1]}/${p[0]}` : s
}
function nuevo() {
  editId.value = ''
  form.value = { titulo: '', descripcion: '', precioOferta: null, vigenciaDesde: hoy, vigenciaHasta: '', activa: true }
  imgs.value = [null, null, null]
  pendientes.value = [null, null, null]
  previews.value = [null, null, null]
  showForm.value = true
}
function editar(o: any) {
  editId.value = o.id
  form.value = {
    titulo: o.titulo, descripcion: o.descripcion ?? '',
    precioOferta: o.precioOferta ?? null,
    vigenciaDesde: String(o.vigenciaDesde ?? '').slice(0, 10) || hoy,
    vigenciaHasta: o.vigenciaHasta ? String(o.vigenciaHasta).slice(0, 10) : '',
    activa: !!o.activa,
  }
  imgs.value = [o.imagenUrl ?? null, o.imagenUrl2 ?? null, o.imagenUrl3 ?? null]
  pendientes.value = [null, null, null]
  previews.value = [null, null, null]
  showForm.value = true
}
function imgActual(i: number) {
  return previews.value[i] ?? (imgs.value[i] ? imgSrc(imgs.value[i]!) : null)
}
function elegirArchivo(i: number, e: Event) {
  const f = ((e.target as HTMLInputElement).files ?? [])[0]
  if (!f) return
  pendientes.value[i] = f
  previews.value[i] = URL.createObjectURL(f)
  ;(e.target as HTMLInputElement).value = ''
}
function quitarImg(i: number) {
  pendientes.value[i] = null
  previews.value[i] = null
  imgs.value[i] = null
}
async function guardar() {
  if (!form.value.titulo.trim()) { alert('Título obligatorio'); return }
  const payload = {
    titulo: form.value.titulo.trim(),
    descripcion: form.value.descripcion.trim(),
    precioOferta: form.value.precioOferta !== null && form.value.precioOferta !== '' ? Number(form.value.precioOferta) : null,
    vigenciaDesde: form.value.vigenciaDesde,
    vigenciaHasta: form.value.vigenciaHasta || null,
    imagenUrl: imgs.value[0] ?? '',
    imagenUrl2: imgs.value[1] ?? '',
    imagenUrl3: imgs.value[2] ?? '',
    activa: form.value.activa,
  }
  let id = editId.value
  try {
    if (id) await api.put(`/admin/ofertas/${id}`, payload)
    else {
      const { data } = await api.post('/admin/ofertas', payload)
      id = data.id
    }
  } catch (e: any) {
    alert(e?.response?.data?.error ?? e?.response?.data?.message ?? 'No se pudo guardar (revisa las fechas)')
    return
  }
  for (let i = 0; i < 3; i++) {
    const f = pendientes.value[i]
    if (!f) continue
    const fd = new FormData()
    fd.append('archivo', f)
    try {
      await api.post(`/admin/ofertas/${id}/imagen`, fd)
    } catch (e: any) {
      alert(e?.response?.data?.error ?? 'No se pudo subir una imagen')
    }
  }
  showForm.value = false
  await load()
}
async function toggle(o: any) {
  await api.put(`/admin/ofertas/${o.id}`, {
    titulo: o.titulo, descripcion: o.descripcion ?? '', precioOferta: o.precioOferta ?? null,
    vigenciaDesde: String(o.vigenciaDesde ?? '').slice(0, 10),
    vigenciaHasta: o.vigenciaHasta ? String(o.vigenciaHasta).slice(0, 10) : null,
    imagenUrl: o.imagenUrl ?? '', imagenUrl2: o.imagenUrl2 ?? '', imagenUrl3: o.imagenUrl3 ?? '',
    activa: !o.activa,
  }).catch((e: any) => alert(e?.response?.data?.error ?? 'No se pudo cambiar el estado'))
  await load()
}
async function eliminar(id: string) {
  if (!confirm('¿Eliminar oferta?')) return
  await api.delete(`/admin/ofertas/${id}`)
  await load()
}
onMounted(load)
</script>
<template>
  <div>
    <div class="title-row">
      <h1>Ofertas / Promociones</h1>
      <button class="primary" style="margin-left:auto" @click="nuevo">+ Nueva oferta</button>
    </div>
    <table style="margin-top:.5rem">
      <thead><tr><th>Título</th><th>Vigencia</th><th>Precio oferta</th><th>Imgs</th><th>Estado</th><th>Acciones</th></tr></thead>
      <tbody>
        <tr v-for="o in items" :key="o.id">
          <td><strong>{{ o.titulo }}</strong><br /><small>{{ (o.descripcion ?? '').slice(0, 80) }}</small></td>
          <td style="white-space:nowrap">{{ fmtFecha(o.vigenciaDesde) }} → {{ fmtFecha(o.vigenciaHasta) }}</td>
          <td>{{ o.precioOferta != null ? `S/ ${o.precioOferta}` : '—' }}</td>
          <td>{{ [o.imagenUrl, o.imagenUrl2, o.imagenUrl3].filter(Boolean).length }}/3</td>
          <td><span class="pill" :class="o.activa ? 'ok' : 'bad'">{{ o.activa ? 'activa' : 'pausada' }}</span></td>
          <td style="white-space:nowrap">
            <button @click="editar(o)">Editar</button>
            <button @click="toggle(o)">{{ o.activa ? 'Pausar' : 'Activar' }}</button>
            <button @click="eliminar(o.id)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="!items.length"><small>Sin ofertas. Crea la primera con el botón de arriba.</small></p>

    <div v-if="showForm" class="modal" @click.self="showForm = false">
      <div class="modal-card" style="max-width:640px">
        <div class="modal-head">
          <h2>{{ editId ? 'Editar oferta' : 'Nueva oferta' }}</h2>
          <button style="margin-left:auto" @click="showForm = false">Cerrar ✕</button>
        </div>
        <form @submit.prevent="g.ejecutar(guardar)" style="display:flex;flex-direction:column;gap:.6rem">
          <label>Título * <input v-model="form.titulo" required placeholder="Ej. 4 Bridgestone → gorro gratis" /></label>
          <label>Descripción (puede ser larga) *
            <textarea v-model="form.descripcion" rows="4" required placeholder="Condiciones, regalo, restricciones…" style="font-family:inherit;font-size:15px"></textarea>
          </label>
          <div style="display:flex;gap:.5rem;flex-wrap:wrap">
            <label>Desde * <input v-model="form.vigenciaDesde" type="date" required :min="hoy" /></label>
            <label>Hasta (opcional) <input v-model="form.vigenciaHasta" type="date" :min="form.vigenciaDesde" /></label>
            <label>Precio oferta S/ (opcional) <input v-model="form.precioOferta" type="number" min="0" step="0.01" style="width:110px" /></label>
            <label><input v-model="form.activa" type="checkbox" /> Activa</label>
          </div>
          <div>
            <p><small>Imágenes (máx 3, del dispositivo):</small></p>
            <div style="display:flex;gap:.6rem;flex-wrap:wrap">
              <div v-for="i in 3" :key="i" style="text-align:center">
                <img v-if="imgActual(i - 1)" :src="imgActual(i - 1)!" alt="" style="width:110px;height:90px;object-fit:cover;display:block" />
                <div v-else style="width:110px;height:90px;background:#f1f1f1;display:flex;align-items:center;justify-content:center;color:#6b6b6b">sin foto</div>
                <input type="file" accept="image/*" @change="elegirArchivo(i - 1, $event)" style="max-width:110px;margin-top:4px" />
                <div><button v-if="imgActual(i - 1)" type="button" @click="quitarImg(i - 1)">Quitar</button></div>
              </div>
            </div>
          </div>
          <div style="display:flex;gap:.5rem;margin-top:.4rem">
            <button class="primary" :disabled="g.ocupado.value">Guardar oferta</button>
            <button type="button" @click="showForm = false">Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
