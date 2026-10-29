<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api, waLink, imgSrc } from '../../lib/api'
import { useAuth } from '../../stores/auth'
import { useSubmit } from '../../composables/useSubmit'
const g = useSubmit()
const auth = useAuth()
const items = ref<any[]>([])
const canjeables = ref<any[]>([])
const misPuntos = ref<number | null>(null)
const cmsg = ref('')
const cok = ref(true)
const imgsDe = (o: any) => [o.imagenUrl, o.imagenUrl2, o.imagenUrl3].filter(Boolean)
function fmtFecha(f: any) {
  if (!f) return ''
  const s = Array.isArray(f) ? f.join('-') : String(f)
  const p = s.slice(0, 10).split('-')
  return p.length === 3 ? `${p[2]}/${p[1]}/${p[0]}` : s
}
async function canjear(c: any) {
  abrirConfirm(c)
}
const selCanje = ref<any>(null)
function abrirConfirm(c: any) { cmsg.value = ''; selCanje.value = c }
function cerrarConfirm() { selCanje.value = null }
async function confirmarCanje() {
  const c = selCanje.value
  if (!c) return
  cmsg.value = ''
  await api.post('/canjes', { canjeableId: c.id })
    .then(() => { cok.value = true; cmsg.value = `Canje registrado: "${c.nombre}" queda por recoger en tienda.` })
    .catch((e: any) => { cok.value = false; cmsg.value = e?.response?.data?.error ?? e?.response?.data?.message ?? 'No se pudo canjear.'; return })
  selCanje.value = null
  await cargarCanjeables()
}
async function cargarCanjeables() {
  const { data } = await api.get('/canjeables').catch(() => ({ data: [] }))
  canjeables.value = data
  if (auth.listo && auth.rol === 'CLIENTE') {
    const { data: p } = await api.get('/perfil').catch(() => ({ data: null }))
    misPuntos.value = p ? Number(p.cliente.puntos) : null
  }
}
onMounted(async () => {
  const { data } = await api.get('/ofertas').catch(() => ({ data: [] }))
  items.value = data
  await cargarCanjeables()
})
</script>
<template>
  <div>
    <section v-reveal class="shop-section wrap">
      <p class="eyebrow">Promociones</p>
      <h1 class="display display-xl">Ofertas</h1>
    </section>
    <div class="plist ofert-cards">
      <div v-for="(o, i) in items" :key="o.id" class="prow" v-reveal="(i % 8) * 50">
        <div>
          <img v-if="imgsDe(o).length" :src="imgSrc(imgsDe(o)[0])" :alt="o.titulo" loading="lazy" />
          <div v-else class="img-ph sm" role="img" :aria-label="`Sin foto: ${o.titulo}`">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z" /><circle cx="12" cy="13" r="4" /></svg>
          </div>
        </div>
        <div>
          <h3>{{ o.titulo }} <span class="dscto-tag">· Oferta</span></h3>
          <p v-if="o.descripcion" class="desc">{{ o.descripcion }}</p>
          <p class="price" v-if="o.precioOferta != null">S/ {{ o.precioOferta }}</p>
          <p class="meta">Del {{ fmtFecha(o.vigenciaDesde) }}<template v-if="o.vigenciaHasta"> al {{ fmtFecha(o.vigenciaHasta) }}</template></p>
        </div>
        <a class="arrow-link" style="margin:0" :href="waLink(`Hola, quiero la oferta ${o.titulo}`)" target="_blank" rel="noopener">› La quiero</a>
      </div>
      <p v-if="!items.length" class="muted">Muy pronto: nuevas promociones.</p>
    </div>

    <section v-reveal class="shop-section wrap" style="margin-top:48px">
      <p class="eyebrow">Fidelización</p>
      <h2 class="display display-md">Canjea tus puntos</h2>
      <p v-if="auth.listo && auth.rol === 'CLIENTE' && misPuntos !== null" class="lead">Tienes <strong>★ {{ misPuntos }} pts</strong> · el canje queda por recoger en tienda.</p>
      <p v-else class="muted">Acumula puntos con tus compras y canjéalos aquí. Solo clientes registrados.</p>
      <p v-if="cmsg" role="status" class="canje-msg" :class="cok ? 'ok' : 'err'"><strong>{{ cmsg }}</strong></p>
    </section>
    <div class="plist ofert-cards" v-if="canjeables.length">
      <div v-for="(c, i) in canjeables" :key="c.id" class="prow" v-reveal="(i % 8) * 50">
        <div>
          <img v-if="imgsDe(c).length" :src="imgSrc(imgsDe(c)[0])" :alt="c.nombre" loading="lazy" />
          <div v-else class="img-ph sm" role="img" :aria-label="`Sin foto: ${c.nombre}`">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z" /><circle cx="12" cy="13" r="4" /></svg>
          </div>
        </div>
        <div>
          <h3>{{ c.nombre }} <span class="dscto-tag">· ★ {{ c.puntosCosto }} pts</span></h3>
          <p v-if="c.descripcion" class="desc">{{ c.descripcion }}</p>
          <p v-if="c.stock != null" class="meta">Quedan {{ c.stock }} para canjear</p>
        </div>
        <button v-if="auth.listo && auth.rol === 'CLIENTE'" class="btn btn-primary" style="margin:0"
          @click="g.ejecutar(() => canjear(c))" :disabled="g.ocupado.value || (misPuntos !== null && misPuntos < c.puntosCosto)">› Canjear</button>
        <RouterLink v-else to="/login" class="arrow-link" style="margin:0">› Regístrate para canjear</RouterLink>
      </div>
    </div>
    <p v-else class="muted wrap">Aún no hay ítems canjeables.</p>

    <div v-if="selCanje" class="canje-overlay" @click.self="cerrarConfirm()" @keyup.escape="cerrarConfirm()">
      <div class="canje-modal" role="dialog" aria-modal="true" aria-labelledby="canje-titulo">
        <p class="eyebrow" style="text-align:left">Fidelización</p>
        <h3 id="canje-titulo">Confirmar canje</h3>
        <div class="canje-item">
          <img v-if="imgsDe(selCanje).length" :src="imgSrc(imgsDe(selCanje)[0])" :alt="selCanje.nombre" />
          <div>
            <strong>{{ selCanje.nombre }}</strong>
            <p class="meta">{{ selCanje.descripcion || 'Recógelo en tienda.' }}</p>
          </div>
        </div>
        <div class="canje-resumen">
          <span>Costo</span><strong>★ {{ selCanje.puntosCosto }} pts</strong>
        </div>
        <div v-if="misPuntos !== null" class="canje-resumen soft">
          <span>Tienes</span><strong>★ {{ misPuntos }} pts</strong>
        </div>
        <div v-if="misPuntos !== null" class="canje-resumen soft">
          <span>Te quedarían</span><strong>★ {{ misPuntos - selCanje.puntosCosto }} pts</strong>
        </div>
        <p class="meta">El canje queda por recoger en tienda. Tienes <strong>3 días</strong> para recogerlo; pasado ese plazo los puntos se devuelven solos.</p>
        <div class="canje-actions">
          <button class="btn btn-outline" type="button" @click="cerrarConfirm()" :disabled="g.ocupado.value">Volver</button>
          <button class="btn btn-primary" type="button" @click="g.ejecutar(confirmarCanje)" :disabled="g.ocupado.value">› Confirmar canje</button>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
/* Rail centrado como Inicio (el resto de la tienda va a 100%). */
.wrap, .plist { max-width: 1200px; margin-left: auto; margin-right: auto; box-sizing: border-box; }
/* Alerta de canje: banner success/error en vez de texto plano */
.canje-msg { border-radius: 12px; padding: 12px 16px; font-size: 15px; text-align: left; }
.canje-msg.ok { background: #dcfce7; border: 1px solid #15803d; color: #14532d; }
.canje-msg.err { background: #fee2e2; border: 1px solid #b91c1c; color: #7f1d1d; }
/* Modal de confirmación: mismo lenguaje que la tienda (Inter, radios, brand #c8102e) */
.canje-overlay { position: fixed; inset: 0; background: rgba(38,38,38,.55); display: flex;
  align-items: center; justify-content: center; z-index: 50; padding: 16px; box-sizing: border-box; }
.canje-modal { background: #fff; border-radius: var(--radius-m); padding: 28px 24px;
  width: min(440px, 100%); box-sizing: border-box; text-align: left; }
.canje-modal h3 { font-size: 24px; font-weight: 300; margin: 4px 0 12px; color: var(--color-carbon); }
.canje-item { display: flex; gap: 12px; align-items: center; margin: 12px 0; }
.canje-item img { width: 64px; height: 64px; object-fit: cover; border-radius: var(--radius-s); background: var(--color-fog); }
.canje-item .meta { margin: 2px 0 0; }
.canje-resumen { display: flex; justify-content: space-between; align-items: center;
  border: 1px solid var(--color-concrete); border-radius: var(--radius-s); padding: 10px 12px; margin: 8px 0; }
.canje-resumen.soft { background: var(--color-fog); border-color: transparent; }
.canje-resumen strong { color: var(--color-brand); }
.canje-modal .meta { text-align: left; }
.canje-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 16px; flex-wrap: wrap; }
/* Móvil: ofertas y canjeables como cards de 2 columnas (como servicios/productos) */
@media (max-width: 640px) {
  .plist.ofert-cards { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; padding: 0 12px; }
  .plist.ofert-cards .prow { display: flex; flex-direction: column; gap: 8px; align-items: stretch; background: #fff; border: 1px solid var(--color-concrete); border-radius: var(--radius-m); padding: 0 0 12px; overflow: hidden; }
  .plist.ofert-cards .prow img { width: 100%; height: 140px; border-radius: 0; }
  .plist.ofert-cards .prow .img-ph.sm { width: 100%; height: 140px; border-radius: 0; }
  .plist.ofert-cards .prow > div:nth-child(2) { padding: 0 10px; }
  .plist.ofert-cards .prow h3 { font-size: 14px; }
  .plist.ofert-cards .prow .price { font-size: 18px; }
  .plist.ofert-cards .prow > a.arrow-link, .plist.ofert-cards .prow > button.btn, .plist.ofert-cards .prow > a.btn { margin: 0 10px !important; text-align: center; justify-content: center; }
  .plist.ofert-cards > p.muted { grid-column: 1 / -1; }
}
</style>
