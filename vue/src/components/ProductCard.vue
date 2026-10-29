<script setup lang="ts">
import { useRouter } from 'vue-router'
import { imgSrc } from '../lib/api'
import { tieneDscto } from '../lib/precios'
import { useCotizacion } from '../stores/cotizacion'
const cotizacion = useCotizacion()

const props = withDefaults(defineProps<{
  producto: any
  indice?: number | null
  etiqueta?: string | null
  meta?: string | null
}>(), { indice: null, etiqueta: null, meta: null })

const router = useRouter()
function abrir() { router.push(`/producto/${props.producto.id}`) }
function metaDefecto() {
  const p = props.producto
  return [p.marca, p.modelo, p.medida].filter(Boolean).join(' ')
}
</script>
<template>
  <div class="ml-card" role="button" tabindex="0" @click="abrir"
    @keydown.enter="abrir" @keydown.space.prevent="abrir"
    :aria-label="`Ver ${producto.nombre}`">
    <img v-if="producto.imagenUrl" :src="imgSrc(producto.imagenUrl)"
      :alt="producto.nombre" loading="lazy" />
    <div v-else class="img-ph" role="img" :aria-label="`Sin foto: ${producto.nombre}`">
      <svg width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z" /><circle cx="12" cy="13" r="4" /></svg>
    </div>
    <div class="body">
      <h3><template v-if="indice != null">#{{ indice }} </template>{{ producto.nombre }}<span v-if="etiqueta" class="dscto-tag"> · {{ etiqueta }}</span></h3>
      <p class="meta">{{ meta ?? metaDefecto() }}</p>
      <slot />
      <p class="price">
        <template v-if="tieneDscto(producto)"><s>S/ {{ producto.precio }}</s> <strong>S/ {{ producto.precioDescuento }}</strong></template>
        <template v-else>S/ {{ producto.precio }}</template>
      </p>
      <div class="cot-row" @click.stop>
        <button v-if="!cotizacion.tiene('producto', producto.id)" type="button" class="btn btn-outline btn-sm" @click="cotizacion.agregar({ tipo: 'producto', id: producto.id, nombre: producto.nombre, detalle: [producto.marca, producto.medida].filter(Boolean).join(' ') })">+ Cotizar</button>
        <span v-else class="meta">✓ En cotización</span>
      </div>
    </div>
  </div>
</template>
<style scoped>
.btn-sm { padding: 6px 12px; font-size: 14px; }
/* El botón queda pegado al borde inferior del card, sin importar el texto */
.cot-row { margin-top: auto; padding-top: 10px; display: flex; gap: .5rem; align-items: center; }
</style>
