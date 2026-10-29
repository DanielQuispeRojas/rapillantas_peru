<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api, waLink } from '../../lib/api'
import { mMercaderias, aMercaderia } from '../../lib/moodu'
import { useSlider } from '../../composables/useSlider'
import { useRecomendaciones } from '../../composables/useRecomendaciones'
import { useAuth } from '../../stores/auth'
import { useSubmit } from '../../composables/useSubmit'
const gVal = useSubmit()
import ProductCard from '../../components/ProductCard.vue'

// Slider de portada (clave "portada", gestionado en Admin → Portada).
const { slides, idx } = useSlider('portada')
const top5 = ref<any[]>([])
const topNeumaticos = ref<any[]>([])
const novedades = ref<any[]>([])
const cargando = ref(true)
const error = ref('')
async function load() {
  cargando.value = true
  error.value = ''
  try {
    // Secciones desde MOODU (/pub/core/mercaderias). Sin ranking local en v1:
    // destacados = primeras, neumaticos = primeras con esa categoria,
    // novedades = 10 primeras (el top real por ventas propias va al dashboard).
    const items = (await mMercaderias(undefined, 50)).map(aMercaderia)
    top5.value = items.slice(0, 5)
    const neus = items.filter((p: any) => (p.categoria ?? '').toLowerCase() === 'neumatico')
    topNeumaticos.value = (neus.length ? neus : items).slice(0, 5)
    novedades.value = items.slice(0, 10)
  } catch {
    error.value = 'No se pudieron cargar los productos. Revisa tu conexi�n.'
  } finally {
    cargando.value = false
  }
}
// Panel cuenta dinámico: con sesión CLIENTE muestra historiales o primer mantenimiento.
const auth = useAuth()
const numVehs = ref<number | null>(null)
async function loadCuenta() {
  if (!auth.listo || auth.rol !== 'CLIENTE') return
  const { data } = await api.get('/perfil/vehiculos').catch(() => ({ data: null }))
  numVehs.value = Array.isArray(data) ? data.length : null
}

// Sobre nosotros: slider gestionado en Admin → Portada (clave "nosotros").
const { slides: nsSlides, idx: nsIdx } = useSlider('nosotros')

// Recomendaciones del taller (Admin → Portada, slider "recomendaciones").
// Coverflow: clic centra, laterales pequeños, hover pulsante.
const { tips } = useRecomendaciones()
const selTip = ref(0)
const tipRefs = ref<HTMLElement[]>([])
const tipsRow = ref<HTMLElement | null>(null)
function centrarTip(i: number) {
  tipRefs.value[i]?.scrollIntoView({ behavior: 'smooth', inline: 'center', block: 'nearest' })
}
function elegirTip(i: number) { selTip.value = i; centrarTip(i) }
function moverTip(d: number) {
  const n = (selTip.value + d + tips.value.length) % tips.value.length
  elegirTip(n)
}

// Valoraciones: una por cliente (se edita, máx 3 veces/día); lo fresco sube primero.
const mival = ref<any>(null)
const vals = ref<any[]>([])
const fest = ref(5)
const fcom = ref('')
const vmsg = ref('')
const estrellasTxt = (n: number) => '★'.repeat(n) + '☆'.repeat(5 - n)
function quedanHoy() {
  if (!mival.value?.fechaUltimaEdicion) return 3
  const hoy = new Date().toISOString().slice(0, 10)
  const f = String(mival.value.fechaUltimaEdicion).slice(0, 10)
  return f === hoy ? Math.max(0, 3 - Number(mival.value.vecesEditadoHoy ?? 0)) : 3
}
async function loadVals() {
  const { data } = await api.get('/valoraciones').catch(() => ({ data: [] }))
  vals.value = data
  // /perfil/** exige rol CLIENTE: no llamarlo con mecánico/colaborador (el 403
  // del interceptor mandaría a /sin-acceso y sacaría de la tienda).
  if (!auth.listo || auth.rol !== 'CLIENTE') return
  const { data: m } = await api.get('/perfil/valoracion').catch(() => ({ data: null }))
  mival.value = m
  if (m) { fest.value = m.estrellas; fcom.value = m.comentario }
}
async function guardarVal() {
  vmsg.value = ''
  try {
    if (mival.value) await api.put('/perfil/valoracion', { estrellas: fest.value, comentario: fcom.value })
    else await api.post('/perfil/valoracion', { estrellas: fest.value, comentario: fcom.value })
    await loadVals()
  } catch (e: any) {
    vmsg.value = e?.response?.data?.error ?? 'No se pudo guardar tu comentario.'
  }
}
onMounted(async () => { await Promise.all([load(), loadCuenta(), loadVals()]) })
</script>
<template>
  <div>
    <figure class="hero-bleed">
      <div v-for="(s, i) in slides" :key="i" class="hero-slide" :class="{ on: i === idx }">
        <img :src="s.src" :alt="s.titulo" />
        <div class="hero-caption">
          <div class="wrap">
            <h2>{{ s.titulo }}</h2>
            <p>{{ s.subtitulo }}</p>
            <p style="margin-top:12px"><a class="btn btn-primary" :href="waLink(s.mensaje)" target="_blank" rel="noopener">Cotizar ›</a></p>
          </div>
        </div>
      </div>
      <div v-if="slides.length > 1" class="hero-dots">
        <button v-for="(s, i) in slides" :key="i" :class="{ on: i === idx }" @click="idx = i" :aria-label="`Slide ${i + 1}`"></button>
      </div>
    </figure>

    <section v-reveal class="shop-section band-fog">
      <div class="wrap">
        <p class="eyebrow">Atención integral</p>
        <h2 class="display display-md">Todo para tu vehículo en un solo lugar</h2>
        <p class="lead">Neumáticos · Refacciones · Servicios</p>
        <div class="offer-cards">
          <RouterLink to="/neumaticos" class="offer-card">
            <h3>Neumáticos</h3>
            <p>Por medida, marca y vehículo</p>
            <span>› Ver</span>
          </RouterLink>
          <RouterLink to="/refacciones" class="offer-card">
            <h3>Refacciones</h3>
            <p>Frenos, suspensión y más</p>
            <span>› Ver</span>
          </RouterLink>
          <RouterLink to="/servicios" class="offer-card">
            <h3>Servicios</h3>
            <p>Alineamiento, frenos, rotación</p>
            <span>› Ver</span>
          </RouterLink>
        </div>
      </div>
    </section>

    <section v-reveal class="shop-section band-fog">
      <div class="wrap" style="text-align:center">
        <template v-if="!auth.listo">
          <p class="eyebrow">Recomendación</p>
          <h2 class="display display-md">Crea tu cuenta y lleva tu historial</h2>
          <p class="lead">Regístrate para recibir recomendaciones de mantenimiento y ver el historial de tus vehículos: próximos servicios, kilometrajes y pendientes del taller.</p>
          <p style="margin-top:24px"><RouterLink to="/registro" class="btn btn-primary">Crear mi cuenta ›</RouterLink></p>
        </template>
        <template v-else-if="auth.rol === 'CLIENTE' && (numVehs ?? 0) > 0">
          <p class="eyebrow">Tu taller</p>
          <h2 class="display display-md">Tienes {{ numVehs }} vehículo{{ numVehs === 1 ? '' : 's' }} con historial</h2>
          <p class="lead">Revisa próximos servicios, kilometrajes y pendientes del taller.</p>
          <p style="margin-top:24px"><RouterLink to="/perfil" class="btn btn-primary">Ver mis historiales ›</RouterLink></p>
        </template>
        <template v-else-if="auth.rol === 'CLIENTE'">
          <p class="eyebrow">Empieza hoy</p>
          <h2 class="display display-md">Realiza tu primer mantenimiento</h2>
          <p class="lead">Inicia tu historial y recibe recomendaciones técnicas de mantenimiento para tu vehículo.</p>
          <p style="margin-top:24px"><a class="btn btn-primary" :href="waLink('Hola, quiero agendar mi primer mantenimiento')" target="_blank" rel="noopener">Agendar por WhatsApp ›</a></p>
        </template>
        <template v-else>
          <p class="eyebrow">Tu taller</p>
          <h2 class="display display-md">Panel de trabajo</h2>
          <p style="margin-top:24px">
            <RouterLink v-if="auth.esAdmin || auth.paneles.length > 0" to="/admin" class="btn btn-primary">Ir al admin ›</RouterLink>
          </p>
        </template>
      </div>
    </section>

    <section v-reveal class="shop-section">
      <div class="wrap">
        <h2 class="display display-md">Neumáticos y refacciones que te mantienen rodando</h2>
        <p class="lead">Busca en el nav, filtra por tu vehículo y cotiza al instante por WhatsApp.</p>
        <a class="arrow-link" :href="waLink(slides[idx]?.mensaje ?? 'Hola, quisiera cotizar')" target="_blank" rel="noopener">› Cotizar por WhatsApp</a>
      </div>
    </section>

    <p v-if="error" class="muted form-error" role="alert" style="text-align:center">{{ error }}
      <br /><button class="btn btn-outline btn-retry" @click="load">Reintentar</button>
    </p>

    <section v-reveal class="shop-section band-fog">
      <div class="wrap">
        <p class="eyebrow">Lo más vendido</p>
        <h2 class="display display-md">Top 5 productos</h2>
      </div>
      <div v-if="cargando" class="ml-grid" aria-hidden="true">
        <div v-for="i in 5" :key="i" class="skel"><div class="skel-img"></div><div class="skel-line"></div><div class="skel-line short"></div></div>
      </div>
      <div v-else class="ml-grid">
        <ProductCard v-for="(p, i) in top5" :key="p.id" :producto="p" :indice="i + 1"
          v-reveal="(i % 5) * 70"
          :meta="`${p.marca ?? ''} ${p.medida ?? ''} · ${p.unidades ?? 0} vendidos`" />
      </div>
      <p v-if="!cargando && !top5.length && !error" class="muted">Aún sin ventas registradas.</p>
    </section>

    <section v-reveal class="shop-section">
      <div class="wrap">
        <p class="eyebrow">Los más rodados</p>
        <h2 class="display display-md">Neumáticos más pedidos en Huancayo</h2>
      </div>
      <div v-if="cargando" class="ml-grid" aria-hidden="true">
        <div v-for="i in 5" :key="i" class="skel"><div class="skel-img"></div><div class="skel-line"></div><div class="skel-line short"></div></div>
      </div>
      <div v-else class="ml-grid">
        <ProductCard v-for="(p, i) in topNeumaticos" :key="p.id" :producto="p" :indice="i + 1"
          v-reveal="(i % 5) * 70"
          :meta="`${p.marca ?? ''} ${p.medida ?? ''} · ${p.unidades ?? 0} vendidos`" />
      </div>
      <p v-if="!cargando && !topNeumaticos.length && !error" class="muted">Aún sin ventas de neumáticos.</p>
    </section>

    <section v-reveal class="shop-section band-fog">
      <div class="wrap">
        <p class="eyebrow">Recién llegados</p>
        <h2 class="display display-md">Últimos 10 ingresos</h2>
      </div>
      <div v-if="cargando" class="ml-grid" aria-hidden="true">
        <div v-for="i in 5" :key="i" class="skel"><div class="skel-img"></div><div class="skel-line"></div><div class="skel-line short"></div></div>
      </div>
      <div v-else class="ml-grid">
        <ProductCard v-for="(p, i) in novedades" :key="p.id" :producto="p" etiqueta="Nuevo" v-reveal="(i % 5) * 70" />
      </div>
      <p v-if="!cargando && !novedades.length && !error" class="muted">Sin novedades por ahora.</p>
    </section>

    <section v-reveal class="shop-section">
      <div class="wrap" style="text-align:center">
        <p class="eyebrow">El taller</p>
        <h2 class="display display-md">Sobre nosotros</h2>
        <p class="lead">Taller huancaíno: venta, servicio y seguimiento en un solo lugar.</p>
      </div>
      <div class="ns-grid">
        <figure class="hero-bleed" style="margin:0">
          <div v-for="(s, i) in nsSlides" :key="i" class="hero-slide" :class="{ on: i === nsIdx }">
            <img :src="s.src" :alt="s.titulo || 'Rapillantas'" />
          </div>
          <div v-if="nsSlides.length > 1" class="hero-dots">
            <button v-for="(s, i) in nsSlides" :key="i" :class="{ on: i === nsIdx }" @click="nsIdx = i" :aria-label="`Foto ${i + 1}`"></button>
          </div>
        </figure>
        <div class="ns-thumbs">
          <button v-for="(s, i) in nsSlides.slice(0, 4)" :key="i" :class="{ on: i === nsIdx }" @click="nsIdx = i" :aria-label="`Ver foto ${i + 1}`">
            <img :src="s.src" :alt="s.titulo || 'Rapillantas'" loading="lazy" />
          </button>
        </div>
      </div>
      <p style="text-align:center;margin-top:24px"><RouterLink to="/nosotros" class="btn btn-outline">Más sobre nosotros ›</RouterLink></p>
    </section>

    <section v-reveal class="shop-section band-fog">
      <div class="wrap" style="text-align:center">
        <p class="eyebrow">Cuida tu vehículo</p>
        <h2 class="display display-md">Recomendaciones</h2>
        <p class="lead">Salud básica para tu vehículo, directo del taller.</p>
      </div>
      <div class="tips-nav">
        <button @click="moverTip(-1)" aria-label="Anterior">‹</button>
        <button @click="moverTip(1)" aria-label="Siguiente">›</button>
      </div>
      <div class="tips-row" ref="tipsRow">
        <div v-for="(tip, i) in tips" :key="i" :ref="(el) => tipRefs[i] = el as HTMLElement"
          class="tip-card" :class="{ sel: i === selTip, dim: i !== selTip }"
          role="button" tabindex="0" :aria-label="tip.t"
          @click="elegirTip(i)" @keydown.enter="elegirTip(i)" @keydown.space.prevent="elegirTip(i)">
          <img v-if="tip.img" :src="tip.img" :alt="tip.t" loading="lazy" />
          <span class="tip-num">{{ i + 1 }}</span>
          <h3>{{ tip.t }}</h3>
          <p>{{ tip.d }}</p>
        </div>
      </div>
    </section>

    <section v-reveal class="shop-section">
      <div class="wrap" style="text-align:center">
        <p class="eyebrow">Opiniones</p>
        <h2 class="display display-md">Lo que dicen nuestros clientes</h2>
      </div>
      <div class="wrap" style="max-width:720px">
        <div v-if="!auth.listo" class="val-login">
          <p><RouterLink to="/login" class="btn btn-outline">Inicia sesión para dejar tu valoración ›</RouterLink></p>
        </div>
        <form v-else-if="auth.rol === 'CLIENTE'" @submit.prevent="gVal.ejecutar(guardarVal)" class="val-form">
          <p><small>{{ mival ? `Tu valoración (te quedan ${quedanHoy()} ediciones hoy)` : 'Tu valoración' }}</small></p>
          <div class="stars" role="radiogroup" aria-label="Estrellas">
            <button v-for="n in 5" :key="n" type="button" role="radio" :aria-checked="n === fest"
              :aria-label="`${n} estrella${n === 1 ? '' : 's'}`" class="star" :class="{ on: n <= fest }" @click="fest = n">★</button>
          </div>
          <textarea v-model="fcom" rows="3" maxlength="500" required placeholder="Cuéntanos cómo te fue…"></textarea>
          <div><button class="btn btn-primary btn-lg btn-block" :disabled="gVal.ocupado.value">★ {{ mival ? 'Actualizar mi comentario' : 'Publicar mi comentario' }}</button></div>
          <p v-if="vmsg" class="form-error" role="alert">{{ vmsg }}</p>
        </form>
        <div v-if="vals.length" class="val-list">
          <div v-for="v in vals" :key="v.id" class="val-card">
            <p class="val-head"><strong>{{ v.nombres }}</strong> <span class="stars-static" :aria-label="`${v.estrellas} de 5`">{{ estrellasTxt(v.estrellas) }}</span></p>
            <p>{{ v.comentario }}</p>
            <p><small>{{ String(v.updatedAt ?? '').slice(0, 10) }}</small></p>
          </div>
        </div>
        <p v-else class="muted">Aún no hay valoraciones. ¡Sé la primera voz!</p>
      </div>
    </section>
  </div>
</template>
<style scoped>
/* Inicio centrado con márgenes laterales (el resto de la tienda va a 100%). */
.wrap, .tips-row, .ml-grid, .ns-grid, .offer-cards { max-width: 1200px; margin-left: auto; margin-right: auto; }
.val-login { display: flex; justify-content: center; text-align: center; }
</style>
