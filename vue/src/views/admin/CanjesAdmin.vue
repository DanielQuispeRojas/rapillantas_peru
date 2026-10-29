<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api, imgSrc } from '../../lib/api'
import { useSubmit } from '../../composables/useSubmit'
const g = useSubmit()

// Canjes: catálogo de ítems canjeables (nombre + descripción + tope opcional)
// + despacho de canjes por recoger. No es mercadería: nunca toca stock ni lleva precio.
const tab = ref<'canjeables' | 'despacho' | 'puntos'>('canjeables')
const items = ref<any[]>([])
const canjes = ref<any[]>([])
const filtro = ref('por_recoger')
const showForm = ref(false)
const editId = ref('')
const form = ref({
  nombre: '', descripcion: '',
  puntosCosto: 0, stock: null as number | null, activo: true,
})
const fmsg = ref('')
const archivo = ref<any>(null)

async function load() {
  const { data } = await api.get('/admin/canjeables').catch(() => ({ data: [] }))
  items.value = data
  await loadCanjes()
}
async function loadCanjes() {
  const { data } = await api.get('/admin/canjeables/canjes', { params: { estado: filtro.value || undefined } }).catch(() => ({ data: [] }))
  canjes.value = data
}
function nuevo() {
  editId.value = ''
  form.value = { nombre: '', descripcion: '', puntosCosto: 0, stock: null, activo: true }
  fmsg.value = ''
  showForm.value = true
}
function editar(c: any) {
  editId.value = c.id
  form.value = {
    nombre: c.nombre, descripcion: c.descripcion ?? '',
    puntosCosto: c.puntosCosto ?? 0, stock: c.stock ?? null,
    activo: c.activo,
  }
  fmsg.value = ''
  showForm.value = true
}
async function guardar() {
  fmsg.value = ''
  const stockNum = form.value.stock === null || (form.value.stock as any) === '' ? null : Math.max(0, Number(form.value.stock))
  const payload = {
    ...form.value,
    publicado: form.value.activo, // si está activo se publica, si no, no
    puntosCosto: Math.max(0, Number(form.value.puntosCosto) || 0),
    stock: stockNum,
  }
  try {
    if (editId.value) await api.put(`/admin/canjeables/${editId.value}`, payload)
    else await api.post('/admin/canjeables', payload)
  } catch (e: any) {
    fmsg.value = e?.response?.data?.error ?? e?.response?.data?.message ?? 'No se pudo guardar.'
    throw e
  }
  showForm.value = false
  await load()
}
async function eliminar(id: string) {
  if (!confirm('¿Eliminar canjeable? El historial de canjes se conserva.')) return
  await api.delete(`/admin/canjeables/${id}`).catch((e: any) => alert(e?.response?.data?.error ?? 'No se pudo eliminar'))
  await load()
}
async function subirFoto(id: string) {
  const f = archivo.value
  if (!f) { alert('Elige una foto primero.'); return }
  const fd = new FormData()
  fd.append('archivo', f)
  await api.post(`/admin/canjeables/${id}/imagen`, fd)
    .catch((e: any) => alert(e?.response?.data?.error ?? e?.response?.data?.message ?? 'No se pudo subir'))
  archivo.value = null
  await load()
}
async function despachar(id: string) {
  if (!confirm('¿Confirmar despacho? El cliente recoge en tienda.')) return
  await api.post(`/admin/canjeables/canjes/${id}/despachar`).catch((e: any) => alert(e?.response?.data?.error ?? 'No se pudo despachar'))
  await loadCanjes()
}
async function cancelar(id: string) {
  if (!confirm('¿Cancelar canje? Devuelve puntos y stock.')) return
  await api.post(`/admin/canjeables/canjes/${id}/cancelar`).catch((e: any) => alert(e?.response?.data?.error ?? 'No se pudo cancelar'))
  await loadCanjes()
}
async function rechazar(id: string) {
  const motivo = prompt('¿Rechazar canje? Se devuelven los puntos al cliente. Motivo (ej. sin stock / oferta caída):', 'sin stock')
  if (motivo === null) return
  await api.post(`/admin/canjeables/canjes/${id}/rechazar`).catch((e: any) => alert(e?.response?.data?.error ?? 'No se pudo rechazar'))
  await loadCanjes()
}
const estadoTxt = (e: string) => e === 'por_recoger' ? 'por recoger (3 días)' : e === 'expirado' ? 'expirado · puntos devueltos' : e
const imgsDe = (c: any) => [c.imagenUrl, c.imagenUrl2, c.imagenUrl3].filter(Boolean)
// Puntos por SKU del catalogo de MOODU (los acredita la venta; la tienda los muestra).
const puntosSku = ref<any[]>([])
const pSku = ref('')
const pPts = ref(0)
const pmsg = ref('')
async function cargarPuntos() {
  pmsg.value = ''
  const { data } = await api.get('/admin/puntos-sku').catch(() => ({ data: [] }))
  puntosSku.value = data ?? []
}
async function guardarPuntos() {
  pmsg.value = ''
  const sku = pSku.value.trim().toUpperCase()
  if (!sku) { pmsg.value = 'SKU obligatorio (ej. ALM-00001).'; return }
  try {
    await api.put(`/admin/puntos-sku/${encodeURIComponent(sku)}`, { puntos: Number(pPts.value) || 0 })
    pSku.value = ''; pPts.value = 0
    await cargarPuntos()
  } catch (e: any) {
    pmsg.value = e?.response?.data?.error ?? e?.response?.data?.message ?? 'No se pudo guardar.'
  }
}
async function quitarPuntos(sku: string) {
  if (!confirm(`¿Quitar puntos de ${sku}?`)) return
  await api.delete(`/admin/puntos-sku/${encodeURIComponent(sku)}`).catch((e: any) => alert(e?.response?.data?.error ?? 'No se pudo quitar'))
  await cargarPuntos()
}
onMounted(load)
</script>
<template>
  <div>
    <div class="tabs">
      <button @click="tab = 'canjeables'" :class="{ on: tab === 'canjeables' }">Canjeables ({{ items.length }})</button>
      <button @click="tab = 'despacho'" :class="{ on: tab === 'despacho' }">Por despachar</button>
      <button @click="tab = 'puntos'; cargarPuntos()" :class="{ on: tab === 'puntos' }">Puntos por SKU</button>
    </div>

    <div v-if="tab === 'canjeables'">
      <div class="title-row">
        <h1 style="margin:0">Ítems canjeables</h1>
        <button class="primary" style="margin-left:auto" @click="nuevo">+ Nuevo canjeable</button>
      </div>
      <p><small>Solo se canjean con puntos (sin precio, sin stock de mercadería). El tope limita la oferta; vacío = ilimitado hasta despublicar.</small></p>
      <table style="margin-top:.5rem" class="cnj-tabla">
        <thead><tr><th>Ítem</th><th>Costo pts</th><th>Tope</th><th>Estado</th><th>Acciones</th></tr></thead>
        <tbody>
          <tr v-for="c in items" :key="c.id">
            <td>
              <img v-if="imgsDe(c).length" :src="imgSrc(imgsDe(c)[0])" alt="" style="width:48px;height:48px;object-fit:cover;border-radius:8px;vertical-align:middle;margin-right:.4rem" />
              <strong>{{ c.nombre }}</strong><br /><small>{{ c.descripcion }}</small>
            </td>
            <td><strong>★ {{ c.puntosCosto }}</strong></td>
            <td>{{ c.stock ?? '∞' }}</td>
            <td><span class="pill" :class="c.activo ? 'ok' : 'bad'">{{ c.activo ? 'publicado' : 'inactivo' }}</span></td>
            <td style="white-space:nowrap">
              <button @click="editar(c)">Editar</button>
              <button @click="g.ejecutar(() => eliminar(c.id))" :disabled="g.ocupado.value">Eliminar</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="cnj-list">
        <div v-for="c in items" :key="c.id" class="cnj-card">
          <div class="cnj-card-top">
            <img v-if="imgsDe(c).length" :src="imgSrc(imgsDe(c)[0])" alt="" />
            <div>
              <strong>{{ c.nombre }}</strong><br /><small class="muted">{{ c.descripcion }}</small>
            </div>
            <span class="pill" :class="c.activo ? 'ok' : 'bad'">{{ c.activo ? 'publicado' : 'inactivo' }}</span>
          </div>
          <div class="cnj-card-meta">
            <span>★ {{ c.puntosCosto }} pts · tope {{ c.stock ?? '∞' }}</span>
          </div>
          <div class="cnj-card-actions">
            <button @click="editar(c)">Editar</button>
            <button @click="g.ejecutar(() => eliminar(c.id))" :disabled="g.ocupado.value">Eliminar</button>
          </div>
        </div>
      </div>

      <div v-if="showForm" class="modal" @click.self="showForm = false">
        <div class="modal-card" style="max-width:600px">
          <div class="modal-head">
            <h2>{{ editId ? 'Editar canjeable' : 'Nuevo canjeable' }}</h2>
            <button style="margin-left:auto" @click="showForm = false">Cerrar ✕</button>
          </div>
          <form @submit.prevent="g.ejecutar(guardar)" style="display:flex;gap:.5rem;flex-wrap:wrap;align-items:end">
            <label>Nombre * <input v-model="form.nombre" required placeholder="Lavado premium" style="min-width:200px" /></label>
            <label>Descripción <input v-model="form.descripcion" style="min-width:220px" placeholder="Qué incluye…" /></label>
            <label>Costo (pts) * <input v-model.number="form.puntosCosto" type="number" min="0" step="1" style="width:90px" /></label>
            <label>Tope oferta <input v-model="form.stock" type="number" min="0" step="1" placeholder="∞" style="width:80px" /></label>
            <label><input v-model="form.activo" type="checkbox" /> Activo (visible en Ofertas)</label>
            <button class="primary" :disabled="g.ocupado.value">{{ editId ? 'Actualizar' : 'Crear' }}</button>
          </form>
          <p v-if="fmsg" class="form-error" role="alert">{{ fmsg }}</p>
          <div v-if="editId" style="margin-top:.8rem;border-top:1px solid var(--erp-line);padding-top:.6rem">
            <p><small>Fotos (máx 3):</small></p>
            <div style="display:flex;gap:.4rem;flex-wrap:wrap;margin:.3rem 0">
              <img v-for="(u, i) in imgsDe(items.find((x: any) => x.id === editId) ?? {})" :key="i" :src="imgSrc(u)" alt="" style="width:48px;height:48px;object-fit:cover;border-radius:8px" />
            </div>
            <label>Subir foto <input type="file" accept="image/*" @change="archivo = ($event.target as HTMLInputElement).files?.[0] ?? null" /></label>
            <button type="button" @click="g.ejecutar(() => subirFoto(editId))" :disabled="g.ocupado.value">Subir</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="tab === 'despacho'">
      <div class="title-row">
        <h1 style="margin:0">Canjes</h1>
        <label style="margin-left:auto">Estado
          <select v-model="filtro" @change="loadCanjes()">
            <option value="">Todos</option>
            <option value="por_recoger">Por recoger</option>
            <option value="despachado">Despachados</option>
            <option value="cancelado">Cancelados</option>
            <option value="rechazado">Rechazados</option>
            <option value="expirado">Expirados (3 días)</option>
          </select>
        </label>
      </div>
      <table v-if="canjes.length" border="1" cellpadding="6" cellspacing="0" style="margin-top:.5rem" class="cnj-tabla">
        <thead><tr><th>Fecha</th><th>Ítem</th><th>Pts</th><th>Estado</th><th>Vence</th><th>Acción</th></tr></thead>
        <tbody>
          <tr v-for="c in canjes" :key="c.id">
            <td>{{ String(c.createdAt ?? '').slice(0, 10) }}</td>
            <td><strong>{{ c.canjeableNombre }}</strong><br /><small>{{ String(c.clienteId).slice(0, 8) }}</small><small v-if="c.motivoNota"><br />{{ c.motivoNota }}</small></td>
            <td>★ {{ c.puntosUsados }}</td>
            <td><span class="pill" :class="c.estado === 'despachado' ? 'ok' : c.estado === 'por_recoger' ? 'warn' : 'bad'">{{ estadoTxt(c.estado) }}</span></td>
            <td><small>{{ c.expiraAt ? String(c.expiraAt).slice(0, 10) : '—' }}</small></td>
            <td style="white-space:nowrap">
              <button v-if="c.estado === 'por_recoger'" @click="g.ejecutar(() => despachar(c.id))" :disabled="g.ocupado.value">Confirmar despacho</button>
              <button v-if="c.estado === 'por_recoger'" @click="g.ejecutar(() => rechazar(c.id))" :disabled="g.ocupado.value">Rechazar</button>
              <button v-if="c.estado === 'por_recoger'" @click="g.ejecutar(() => cancelar(c.id))" :disabled="g.ocupado.value">Cancelar</button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="canjes.length" class="cnj-list">
        <div v-for="c in canjes" :key="c.id" class="cnj-card">
          <div class="cnj-card-top">
            <strong>{{ c.canjeableNombre }}</strong>
            <span class="pill" :class="c.estado === 'despachado' ? 'ok' : c.estado === 'por_recoger' ? 'warn' : 'bad'">{{ estadoTxt(c.estado) }}</span>
          </div>
          <div class="cnj-card-meta">
            <span>{{ String(c.createdAt ?? '').slice(0, 10) }} · ★ {{ c.puntosUsados }} pts · vence {{ c.expiraAt ? String(c.expiraAt).slice(0, 10) : '—' }}</span>
            <span><small class="muted">{{ String(c.clienteId).slice(0, 8) }}</small><small v-if="c.motivoNota"> · {{ c.motivoNota }}</small></span>
          </div>
          <div v-if="c.estado === 'por_recoger'" class="cnj-card-actions">
            <button @click="g.ejecutar(() => despachar(c.id))" :disabled="g.ocupado.value">Despachar</button>
            <button @click="g.ejecutar(() => rechazar(c.id))" :disabled="g.ocupado.value">Rechazar</button>
            <button @click="g.ejecutar(() => cancelar(c.id))" :disabled="g.ocupado.value">Cancelar</button>
          </div>
        </div>
      </div>
      <p v-else><small>Sin canjes en este estado.</small></p>
    </div>

    <div v-if="tab === 'puntos'">
      <div class="title-row">
        <h1 style="margin:0">Puntos por SKU</h1>
      </div>
      <p><small>Puntos que otorga cada SKU de MOODU por unidad comprada (los acredita la venta y la tienda los muestra). Sin fila = 0 pts.</small></p>
      <div style="display:flex;gap:.5rem;flex-wrap:wrap;align-items:end;margin:.6rem 0">
        <label>SKU <input v-model="pSku" placeholder="ALM-00001" style="text-transform:uppercase" /></label>
        <label>Puntos <input v-model.number="pPts" type="number" min="0" step="1" /></label>
        <button class="primary" @click="g.ejecutar(guardarPuntos)" :disabled="g.ocupado.value">Guardar</button>
      </div>
      <p v-if="pmsg" class="form-error" role="alert">{{ pmsg }}</p>
      <table v-if="puntosSku.length" class="cnj-tabla">
        <thead><tr><th>SKU</th><th>Puntos</th><th>Acción</th></tr></thead>
        <tbody>
          <tr v-for="p in puntosSku" :key="p.sku">
            <td><strong>{{ p.sku }}</strong></td><td>★ {{ p.puntos }}</td>
            <td><button @click="g.ejecutar(() => quitarPuntos(p.sku))" :disabled="g.ocupado.value">Quitar</button></td>
          </tr>
        </tbody>
      </table>
      <p v-else><small>Sin puntos configurados.</small></p>
    </div>
  </div>
</template>

<style scoped>
.cnj-list { display: none; }
@media (max-width: 640px) {
  .cnj-tabla { display: none; }
  .cnj-list { display: grid; gap: 10px; margin: 10px 0; }
  .cnj-card { background: #fff; border: 1px solid var(--erp-line); border-radius: 12px; padding: 12px 14px; display: flex; flex-direction: column; gap: 8px; }
  .cnj-card-top { display: flex; gap: 8px; align-items: flex-start; justify-content: space-between; }
  .cnj-card-top img { width: 48px; height: 48px; object-fit: cover; border-radius: 8px; flex-shrink: 0; }
  .cnj-card-top div { flex: 1; min-width: 0; }
  .cnj-card-meta { display: flex; flex-direction: column; gap: 2px; font-size: 13px; color: var(--erp-muted); }
  .cnj-card-actions { display: flex; gap: 8px; }
  .cnj-card-actions button { flex: 1; }
  .title-row { flex-wrap: wrap; }
}
</style>
