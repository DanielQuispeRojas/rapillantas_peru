<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../../lib/api'
import { mMercaderias, mServicios, aMercaderia, aServicio } from '../../lib/moodu'
import { textoProducto, textoServicio, coincide, tagsDe } from '../../lib/seo'
import { useCotizacion } from '../../stores/cotizacion'
import ProductCard from '../../components/ProductCard.vue'
const cotizacion = useCotizacion()
const tieneDscto = (s: any) => s.precioDescuento && Number(s.precioDescuento) < Number(s.precioRef)

// Buscador global: mercaderias + servicios de MOODU por texto, incluyendo
// etiquetas. Las etiquetas funcionan como keywords: "frenos", "hilux", etc.
const route = useRoute()
const q = ref((route.query.q as string) ?? '')
const etiqueta = ref((route.query.etiqueta as string) ?? '')
const prods = ref<any[]>([])
const servs = ref<any[]>([])

async function load() {
  const [mm, ss, pts] = await Promise.all([
    mMercaderias(undefined, 50).catch(() => []),
    mServicios(undefined, 50).catch(() => []),
    api.get('/puntos').then((r: any) => r.data).catch(() => ({})),
  ])
  prods.value = mm.map(aMercaderia)
  servs.value = ss.map((s) => aServicio(s, Number(pts?.[s.sku] ?? 0)))
}
const haystackProds = computed(() => new Map(prods.value.map((p: any) => [p.id, textoProducto(p)])))
const haystackServs = computed(() => new Map(servs.value.map((s: any) => [s.id, textoServicio(s)])))
function pasaEtiqueta(x: any): boolean {
  const e = etiqueta.value.trim().toLowerCase()
  if (!e) return true
  return tagsDe(x.etiquetas).includes(e)
}
const fProds = computed(() => {
  const t = q.value.trim()
  return prods.value.filter((p: any) => {
    if (!pasaEtiqueta(p)) return false
    if (!t) return Boolean(etiqueta.value.trim())
    return coincide(haystackProds.value.get(p.id) ?? textoProducto(p), t)
  })
})
const fServs = computed(() => {
  const t = q.value.trim()
  return servs.value.filter((s: any) => {
    if (!pasaEtiqueta(s)) return false
    if (!t) return Boolean(etiqueta.value.trim())
    return coincide(haystackServs.value.get(s.id) ?? textoServicio(s), t)
  })
})
onMounted(load)
watch(() => route.query.q, (v) => { q.value = (v as string) ?? '' })
watch(() => route.query.etiqueta, (v) => { etiqueta.value = (v as string) ?? '' })
</script>
<template>
  <div>
    <section class="shop-section wrap">
      <p class="eyebrow">Búsqueda</p>
      <h1 class="display display-xl">Resultados para “{{ q }}”{{ etiqueta ? ` · #${etiqueta}` : '' }}</h1>
      <p class="lead">{{ fProds.length + fServs.length }} resultados</p>
    </section>
    <div class="wrap" v-if="fProds.length">
      <h2 class="display display-md" style="text-align:left">Productos ({{ fProds.length }})</h2>
      <div class="ml-grid">
        <ProductCard v-for="(p, i) in fProds" :key="p.id" :producto="p" v-reveal="(i % 8) * 50"
          :meta="`${p.categoria ?? ''} · ${p.marca ?? ''} ${p.medida ?? ''} · stock ${p.stockActual ?? 0}`">
          <p v-if="p.descripcion" class="meta">{{ p.descripcion }}</p>
          <p v-if="p.etiquetas" class="meta" @click.stop>
            <router-link v-for="t in tagsDe(p.etiquetas)" :key="t" :to="`/buscar?etiqueta=${encodeURIComponent(t)}`" style="margin-right:6px">#{{ t }}</router-link>
          </p>
        </ProductCard>
      </div>
    </div>
    <div class="wrap" v-if="fServs.length">
      <h2 class="display display-md" style="text-align:left">Servicios ({{ fServs.length }})</h2>
      <div class="ml-grid">
        <div v-for="(s, i) in fServs" :key="s.id" class="ml-card" v-reveal="(i % 8) * 50">
          <div class="body">
            <h3>{{ s.nombre }}</h3>
            <p class="meta">★ {{ s.puntos ?? 0 }} pts</p>
            <p v-if="s.etiquetas" class="meta">
              <router-link v-for="t in tagsDe(s.etiquetas)" :key="t" :to="`/buscar?etiqueta=${encodeURIComponent(t)}`" style="margin-right:6px">#{{ t }}</router-link>
            </p>
            <p class="price">
              <template v-if="tieneDscto(s)"><s>S/ {{ s.precioRef }}</s> <strong>Desde S/ {{ s.precioDescuento }}</strong></template>
              <template v-else>Desde <strong>S/ {{ s.precioRef }}</strong></template>
              <small class="aprox">aprox.</small>
            </p>
            <p class="meta ref-note">Precio referencial: el costo final depende del estado real del vehículo (se confirma en taller).</p>
            <div class="cot-row">
              <button v-if="!cotizacion.tiene('servicio', s.id)" type="button" class="btn btn-outline btn-sm" @click="cotizacion.agregar({ tipo: 'servicio', id: s.id, nombre: s.nombre })">+ Cotizar</button>
              <span v-else class="meta">✓ En cotización</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <p v-if="!fProds.length && !fServs.length" class="muted" style="text-align:center">Sin resultados. Prueba con otra palabra.</p>
  </div>
</template>

<style scoped>
.btn-sm { padding: 6px 12px; font-size: 14px; }
.ml-card h3 { font-size: 20px; font-weight: 700; }
.ref-note { font-size: 12px; }
.cot-row { margin-top: auto; padding-top: 10px; display: flex; gap: .5rem; flex-wrap: wrap; align-items: center; }
.ml-grid { margin-bottom: 24px; }
</style>
