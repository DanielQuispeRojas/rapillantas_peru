<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { mMercaderias, aMercaderia } from '../../lib/moodu'
import { textoProducto, coincide, tagsDe } from '../../lib/seo'
import ProductCard from '../../components/ProductCard.vue'

const props = defineProps<{ categoria: string }>()
const route = useRoute()
const todos = ref<any[]>([])
const cargando = ref(true)
const error = ref('')
const q = ref((route.query.medida as string) ?? '')
const etiquetaQ = ref((route.query.etiqueta as string) ?? '')
const selMarcas = ref<string[]>([])
const selModelos = ref<string[]>([])
const selMedidas = ref<string[]>([])
const selEtiquetas = ref<string[]>([])

async function load() {
  cargando.value = true
  error.value = ''
  try {
    // Catalogo en MOODU (/pub/core/mercaderias, solo habilitadas).
    const items = await mMercaderias(undefined, 50)
    let lista = items.map(aMercaderia)
    // La ruta pide categoria (neumatico/refaccion): MOODU ya no expone
    // 'categoria' (es responsabilidad del modulo, no del core). Se discrimina
    // por etiquetas (tags) y, como red de seguridad, por nombre/descripcion.
    if (props.categoria) {
      const c = props.categoria.toLowerCase()
      const porTag = lista.filter((p: any) => tagsDe(p.etiquetas).includes(c)
        || (p.etiquetas ?? '').toLowerCase().includes(c))
      if (porTag.length) {
        lista = porTag
      } else {
        const porNombre = lista.filter((p: any) =>
          ((p.nombre ?? '') + ' ' + (p.descripcion ?? '')).toLowerCase().includes(c))
        if (porNombre.length) lista = porNombre
      }
    }
    todos.value = lista
    if (!lista.length) error.value = 'Sin catalogo por ahora (revisa que MOODU este encendido y su API Web activa).'
  } catch {
    error.value = 'No se pudo cargar el catálogo. Revisa tu conexión.'
    todos.value = []
  } finally {
    cargando.value = false
  }
}
const uniq = (xs: (string | null | undefined)[]) => [...new Set(xs.filter(Boolean) as string[])].sort()
const marcas = computed(() => uniq(todos.value.map((p: any) => p.marca)))
const modelos = computed(() => uniq(todos.value.map((p: any) => p.modelo)))
const medidas = computed(() => uniq(todos.value.map((p: any) => p.medida)))
const etiquetasTodas = computed(() => [...new Set(todos.value.flatMap((p: any) => tagsDe(p.etiquetas)))].sort())

const filtrados = computed(() => todos.value.filter((p: any) => {
  if (selMarcas.value.length && !selMarcas.value.includes(p.marca)) return false
  if (selModelos.value.length && !selModelos.value.includes(p.modelo)) return false
  if (selMedidas.value.length && !selMedidas.value.includes(p.medida)) return false
  const exigidas = [...selEtiquetas.value, ...(etiquetaQ.value.trim() ? [etiquetaQ.value.trim().toLowerCase()] : [])]
  if (exigidas.length && !exigidas.every((e) => tagsDe(p.etiquetas).includes(e))) return false
  // q incluye etiquetas + descripción gracias a textoProducto(), con match por tokens.
  if (q.value.trim() && !coincide(textoProducto(p), q.value)) return false
  return true
}))
function limpiar() { selMarcas.value = []; selModelos.value = []; selMedidas.value = []; selEtiquetas.value = []; etiquetaQ.value = ''; q.value = '' }
const filtrosAbiertos = ref(false)
const numFiltros = computed(() =>
  selMarcas.value.length + selModelos.value.length + selMedidas.value.length + selEtiquetas.value.length
  + (q.value.trim() ? 1 : 0) + (etiquetaQ.value.trim() ? 1 : 0),
)
onMounted(load)
watch(() => [props.categoria, route.query.medida, route.query.etiqueta], () => {
  q.value = (route.query.medida as string) ?? ''
  etiquetaQ.value = (route.query.etiqueta as string) ?? ''
  selMarcas.value = []; selModelos.value = []; selMedidas.value = []; selEtiquetas.value = []
  load()
})
</script>
<template>
  <div class="wrap catalogo">
    <div class="filter-bar">
      <button class="btn btn-outline btn-sm" @click="filtrosAbiertos = true">Filtros<span v-if="numFiltros"> ({{ numFiltros }})</span></button>
      <span class="muted">{{ filtrados.length }} de {{ todos.length }}</span>
    </div>
    <div class="shop-layout" style="margin-top:24px">
      <div v-if="filtrosAbiertos" class="filter-backdrop" @click="filtrosAbiertos = false"></div>
      <aside class="shop-aside" :class="{ open: filtrosAbiertos }">
        <div class="filter-head">
          <strong>Filtros<span v-if="numFiltros"> ({{ numFiltros }})</span></strong>
          <button class="btn btn-outline btn-sm" @click="filtrosAbiertos = false" aria-label="Cerrar filtros">✕</button>
        </div>
        <h4>Buscar</h4>
        <input v-model="q" type="text" placeholder="Medida, marca, etiqueta…" />
        <h4>Marca</h4>
        <label v-for="m in marcas" :key="m"><input type="checkbox" :value="m" v-model="selMarcas" /> {{ m }}</label>
        <h4>Modelo</h4>
        <label v-for="m in modelos" :key="m"><input type="checkbox" :value="m" v-model="selModelos" /> {{ m }}</label>
        <h4>Dimensiones</h4>
        <label v-for="m in medidas" :key="m"><input type="checkbox" :value="m" v-model="selMedidas" /> {{ m }}</label>
        <h4>Etiquetas</h4>
        <label v-for="t in etiquetasTodas" :key="t"><input type="checkbox" :value="t" v-model="selEtiquetas" /> #{{ t }}</label>
        <div class="filter-foot">
          <button class="btn btn-outline btn-sm" @click="limpiar">Limpiar</button>
          <button class="btn btn-primary btn-sm" @click="filtrosAbiertos = false">Ver {{ filtrados.length }} resultados</button>
        </div>
        <p class="filter-desktop-only"><button class="btn btn-outline btn-sm" @click="limpiar">› Limpiar filtros</button></p>
      </aside>
      <div>
        <p class="muted count-line" style="text-align:left">{{ filtrados.length }} de {{ todos.length }} productos</p>
        <p v-if="error" class="muted form-error" role="alert" style="text-align:left">{{ error }}
          <br /><button class="btn btn-outline btn-retry" @click="load">Reintentar</button>
        </p>
        <div v-if="cargando" class="ml-grid" aria-hidden="true">
          <div v-for="i in 8" :key="i" class="skel"><div class="skel-img"></div><div class="skel-line"></div><div class="skel-line short"></div></div>
        </div>
        <div v-else class="ml-grid">
          <ProductCard v-for="(p, i) in filtrados" :key="p.id" :producto="p" v-reveal="(i % 8) * 50"
            :meta="`${p.marca ?? ''} ${p.modelo ?? ''} ${p.medida ?? ''}`">
            <p v-if="p.descripcion" class="meta">{{ p.descripcion }}</p>
            <p class="meta">Stock {{ p.stockActual }}</p>
          </ProductCard>
        </div>
        <p v-if="!cargando && !filtrados.length && !error" class="muted" style="text-align:left">Sin resultados con esos filtros.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
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
