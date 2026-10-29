<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { api } from '../../lib/api'
import { mServicios, aServicio } from '../../lib/moodu'
import { textoServicio, coincide, tagsDe } from '../../lib/seo'
import { useCotizacion } from '../../stores/cotizacion'
const cotizacion = useCotizacion()
const items = ref<any[]>([])
const q = ref('')
const fqEtiq = ref('')
const fqPrecioMax = ref<any>(null)
const fqPuntosMin = ref<any>(null)
const orden = ref('')
const tieneDscto = (s: any) => s.precioDescuento && Number(s.precioDescuento) < Number(s.precioRef)
const precioEf = (s: any) => Number(tieneDscto(s) ? s.precioDescuento : s.precioRef)
const filtrados = computed(() => {
  const t = q.value.trim()
  const pMax = fqPrecioMax.value !== null && fqPrecioMax.value !== '' ? Number(fqPrecioMax.value) : null
  const ptsMin = fqPuntosMin.value !== null && fqPuntosMin.value !== '' ? Number(fqPuntosMin.value) : null
  const lista = items.value.filter((s: any) => {
    if (t && !coincide(textoServicio(s), t)) return false
    if (fqEtiq.value && !tagsDe(s.etiquetas).includes(fqEtiq.value)) return false
    if (pMax !== null && precioEf(s) > pMax) return false
    if (ptsMin !== null && Number(s.puntos ?? 0) < ptsMin) return false
    return true
  })
  if (orden.value === 'precio-asc') lista.sort((a: any, b: any) => precioEf(a) - precioEf(b))
  else if (orden.value === 'precio-desc') lista.sort((a: any, b: any) => precioEf(b) - precioEf(a))
  else if (orden.value === 'puntos-desc') lista.sort((a: any, b: any) => Number(b.puntos ?? 0) - Number(a.puntos ?? 0))
  return lista
})
function limpiarFiltros() { q.value = ''; fqEtiq.value = ''; fqPrecioMax.value = null; fqPuntosMin.value = null; orden.value = '' }
const filtrosAbiertos = ref(false)
const numFiltros = computed(() =>
  (q.value.trim() ? 1 : 0) + (fqEtiq.value ? 1 : 0)
  + (fqPrecioMax.value !== null && fqPrecioMax.value !== '' ? 1 : 0)
  + (fqPuntosMin.value !== null && fqPuntosMin.value !== '' ? 1 : 0)
  + (orden.value ? 1 : 0),
)
onMounted(async () => {
  // Servicios en MOODU (/pub/core/servicios) + puntos del overlay propio.
  const [ss, pts] = await Promise.all([
    mServicios(undefined, 50),
    api.get('/puntos').then((r: any) => r.data).catch(() => ({})),
  ])
  items.value = ss.map((s) => aServicio(s, Number(pts?.[s.sku] ?? 0)))
})
</script>
<template>
  <div class="wrap servicios">
    <div class="filter-bar">
      <button type="button" class="btn btn-outline btn-sm" @click="filtrosAbiertos = true">Filtros<span v-if="numFiltros"> ({{ numFiltros }})</span></button>
      <span class="muted">{{ filtrados.length }} de {{ items.length }}</span>
    </div>
    <div class="shop-layout" style="margin-top:24px">
      <div v-if="filtrosAbiertos" class="filter-backdrop" @click="filtrosAbiertos = false"></div>
      <aside class="shop-aside" :class="{ open: filtrosAbiertos }">
        <div class="filter-head">
          <strong>Filtros<span v-if="numFiltros"> ({{ numFiltros }})</span></strong>
          <button type="button" class="btn btn-outline btn-sm" @click="filtrosAbiertos = false" aria-label="Cerrar filtros">✕</button>
        </div>
        <h4>Buscar</h4>
        <input v-model="q" type="text" placeholder="Alineamiento, frenos…" />
        <h4 style="margin-top:14px">Etiqueta</h4>
        <select v-model="fqEtiq">
          <option value="">Todas</option>
          <option v-for="t in [...new Set(items.flatMap((s: any) => tagsDe(s.etiquetas)))].sort()" :key="t" :value="t">#{{ t }}</option>
        </select>
        <h4 style="margin-top:14px">Precio</h4>
        <label>Hasta S/ <input v-model.number="fqPrecioMax" type="number" min="0" step="0.01" placeholder="100" /></label>
        <h4 style="margin-top:14px">Puntos</h4>
        <label>Desde (pts) <input v-model.number="fqPuntosMin" type="number" min="0" step="1" placeholder="10" /></label>
        <h4 style="margin-top:14px">Ordenar</h4>
        <select v-model="orden">
          <option value="">Relevancia</option>
          <option value="precio-asc">Menor precio</option>
          <option value="precio-desc">Mayor precio</option>
          <option value="puntos-desc">Más puntos</option>
        </select>
        <div class="filter-foot">
          <button type="button" class="btn btn-outline btn-sm" @click="limpiarFiltros">Limpiar</button>
          <button type="button" class="btn btn-primary btn-sm" @click="filtrosAbiertos = false">Ver {{ filtrados.length }} resultados</button>
        </div>
        <button type="button" class="btn btn-outline btn-sm filter-desktop-only" @click="limpiarFiltros" style="margin-top:10px">Limpiar</button>
      </aside>
      <div>
        <!--<p class="eyebrow" style="text-align:left">Mano de obra</p>
        <h1 class="display display-md" style="text-align:left">Servicios</h1>-->
        <p class="muted count-line" style="text-align:left">{{ filtrados.length }} de {{ items.length }} servicios</p>
        <div class="ml-grid">
          <div v-for="(s, i) in filtrados" :key="s.id" class="ml-card" v-reveal="(i % 8) * 50">
            <div class="body">
              <h3>{{ s.nombre }}</h3>
              <p class="meta">★ {{ s.puntos ?? 0 }} pts</p>
              <p class="price">
                <template v-if="tieneDscto(s)"><s>S/ {{ s.precioRef }}</s> <strong>Desde S/ {{ s.precioDescuento }}</strong></template>
                <template v-else>Desde <strong>S/ {{ s.precioRef }}</strong></template>
              </p>
              <p class="meta ref-note">Precio referencial: el costo final depende del estado real del vehículo (se confirma en taller).</p>
              <div class="cot-row">
                <button v-if="!cotizacion.tiene('servicio', s.id)" type="button" class="btn btn-outline btn-sm" @click="cotizacion.agregar({ tipo: 'servicio', id: s.id, nombre: s.nombre })">+ Cotizar</button>
                <span v-else class="meta">✓ En cotización</span>
              </div>
            </div>
          </div>
        </div>
        <p v-if="!filtrados.length" class="muted" style="text-align:left">Sin servicios por ahora.</p>
      </div>
    </div>
  </div>
</template>
<style scoped>
.btn-sm { padding: 6px 12px; font-size: 14px; }
/* Título del servicio grande y en negrita; la aclaración de precio, pequeña */
.ml-card h3 { font-size: 20px; font-weight: 700; }
.ref-note { font-size: 12px; }
/* El botón queda pegado al borde inferior del card, sin importar el texto */
.cot-row { margin-top: auto; padding-top: 10px; display: flex; gap: .5rem; flex-wrap: wrap; align-items: center; }
.filter-bar { display: none; }
.filter-head { display: none; }
.filter-foot { display: none; }
@media (max-width: 760px) {
  .filter-bar { display: flex; gap: 10px; align-items: center; justify-content: space-between; position: sticky; top: 0; z-index: 5; background: #fff; padding: 10px 0; }
  .count-line { display: none; }
  .filter-desktop-only { display: none; }
  .shop-aside { position: fixed; top: 0; left: 0; bottom: 0; width: min(320px, 85vw); z-index: 70; margin: 0; border-radius: 0; overflow-y: auto; transform: translateX(-105%); transition: transform 0.25s ease; }
  .shop-aside.open { transform: none; }
  .filter-head { display: flex; align-items: center; justify-content: space-between; position: sticky; top: 0; background: #fff; padding-bottom: 8px; }
  .filter-foot { display: flex; gap: 8px; position: sticky; bottom: 0; background: #fff; padding: 12px 0 4px; }
  .filter-foot .btn-primary { flex: 1; }
  .filter-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 60; }
}
</style>
