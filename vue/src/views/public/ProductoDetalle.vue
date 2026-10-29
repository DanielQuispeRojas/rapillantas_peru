<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api, imgSrc } from '../../lib/api'
import { mMercaderiaPorSku, mMercaderias, aMercaderia } from '../../lib/moodu'
import { applySeo, SITE, tagsDe } from '../../lib/seo'
import { useCotizacion } from '../../stores/cotizacion'
const cotizacion = useCotizacion()

// Detalle estilo marketplace: fotos a la izquierda, descripción a la derecha,
// recomendados debajo.
const route = useRoute()
const router = useRouter()
const p = ref<any>(null)
const fotos = ref<string[]>([])
const fotoIdx = ref(0)
const rel = ref<any[]>([])
const tieneDscto = (x: any) => x?.precioDescuento && Number(x.precioDescuento) < Number(x.precio)

async function load() {
  const sku = route.params.id as string
  // Ficha en MOODU por SKU (:id ahora es el SKU).
  const m = await mMercaderiaPorSku(sku)
  if (!m) { router.push('/'); return }
  const data = aMercaderia(m)
  p.value = data
  // SEO dinámico: keywords del producto = etiquetas + marca/modelo/medida.
  const kws = [...tagsDe(data.etiquetas), data.marca, data.modelo, data.medida, data.categoria].filter(Boolean).join(', ')
  applySeo({
    title: `${data.nombre} ${[data.marca, data.medida].filter(Boolean).join(' ')} | Rapillantas Huancayo`.trim(),
    description: `${data.nombre} ${[data.marca, data.modelo, data.medida].filter(Boolean).join(' ')} en Huancayo. ${data.descripcion ?? 'Precio y stock disponible. Cotiza por WhatsApp.'}`.slice(0, 160),
    keywords: kws || undefined,
    canonical: `${SITE.baseUrl}/producto/${data.sku}`,
  })
  fotoIdx.value = 0
  // Fotos del overlay propio (por SKU); sin fotos cae al placeholder del card.
  const { data: f } = await api.get(`/sku/${encodeURIComponent(data.sku)}/imagenes`).catch(() => ({ data: [] }))
  fotos.value = f.length ? f : []
  // Relacionados: misma marca en MOODU (sin el actual).
  const relM = await mMercaderias(data.marca || undefined, 50)
  rel.value = relM.map(aMercaderia).filter((r: any) => r.sku !== data.sku).slice(0, 8)
}
onMounted(load)
watch(() => route.params.id, load)
</script>
<template>
  <div class="wrap" v-if="p">
    <p style="margin-top:24px"><a class="arrow-link" style="margin:0;text-align:left" @click="$router.back()" href="#">‹ Volver</a></p>
    <div class="det-grid">
      <div>
        <img v-if="fotos[fotoIdx]" :src="imgSrc(fotos[fotoIdx])" alt="" class="det-main" />
        <div class="det-thumbs">
          <img v-for="(f, i) in fotos" :key="i" :src="imgSrc(f)" alt="" :class="{ on: i === fotoIdx }" @click="fotoIdx = i" />
        </div>
      </div>
      <div>
        <p class="eyebrow" style="text-align:left">{{ p.categoria }} · {{ p.sku }}</p>
        <h1 class="display display-md" style="text-align:left">{{ p.nombre }}</h1>
        <p class="muted" style="text-align:left">{{ p.marca }} {{ p.modelo }} {{ p.medida }}</p>
        <p class="det-price">
          <template v-if="tieneDscto(p)"><s>S/ {{ p.precio }}</s> <strong>S/ {{ p.precioDescuento }}</strong></template>
          <template v-else>S/ {{ p.precio }}</template>
        </p>
        <p class="muted" style="text-align:left">{{ p.stockActual > 0 ? `Stock disponible (${p.stockActual})` : 'Sin stock' }}</p>
        <p>
          <button v-if="!cotizacion.tiene('producto', p.id)" class="btn btn-primary" @click="cotizacion.agregar({ tipo: 'producto', id: p.id, nombre: p.nombre, detalle: [p.marca, p.medida].filter(Boolean).join(' ') })">+ Agregar a cotización</button>
          <span v-else class="muted">✓ En tu cotización (botón flotante Cotizar)</span>
        </p>
        <p v-if="p.etiquetas"><small class="muted">
          <router-link v-for="t in tagsDe(p.etiquetas)" :key="t" :to="`/buscar?etiqueta=${encodeURIComponent(t)}`" style="margin-right:6px">#{{ t }}</router-link>
        </small></p>
        <h3>Descripción</h3>
        <p>{{ p.descripcion || 'Sin descripción.' }}</p>
      </div>
    </div>
    <section class="shop-section" v-if="rel.length">
      <p class="eyebrow">También te puede interesar</p>
      <h2 class="display display-md">Recomendados</h2>
      <div class="ml-grid">
        <div v-for="r in rel" :key="r.id" class="ml-card" @click="$router.push(`/producto/${r.id}`)" style="cursor:pointer">
          <img v-if="r.imagenUrl" :src="imgSrc(r.imagenUrl)" alt="" />
          <div class="body">
            <h3>{{ r.nombre }}</h3>
            <p class="meta">{{ r.marca }} {{ r.medida }}</p>
            <p class="price">
              <template v-if="tieneDscto(r)"><s>S/ {{ r.precio }}</s> <strong>S/ {{ r.precioDescuento }}</strong></template>
              <template v-else>S/ {{ r.precio }}</template>
            </p>
          </div>
        </div>
      </div>
    </section>
  </div>
  <p v-else class="muted">Cargando…</p>
</template>
