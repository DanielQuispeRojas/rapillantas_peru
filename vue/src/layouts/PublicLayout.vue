<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useAuth } from '../stores/auth'
import { useRouter } from 'vue-router'
import { api, cargarEmpresa, empresa } from '../lib/api'
import Cotizador from '../components/Cotizador.vue'
const auth = useAuth()
const router = useRouter()
const bq = ref('')
const searchOpen = ref(false)
const statusOpen = ref(false)
function salir() { auth.salir(); router.push('/') }
function buscar() { if (bq.value.trim()) { router.push(`/buscar?q=${encodeURIComponent(bq.value.trim())}`); searchOpen.value = false } }
// Al elegir una opción del menú, suelta el foco para que el desplegable se cierre (:focus-within)
function cerrarMenu() { (document.activeElement as HTMLElement)?.blur?.() }
// Puntos del CLIENTE junto al nombre en el nav
const misPuntos = ref<number | null>(null)
async function cargarPuntos() {
  if (!auth.listo || auth.rol !== 'CLIENTE') { misPuntos.value = null; return }
  const { data } = await api.get('/perfil').catch(() => ({ data: null }))
  misPuntos.value = data ? Number(data.cliente?.puntos ?? 0) : null
}
onMounted(async () => { cargarEmpresa(); await cargarPuntos() })
watch(() => [auth.listo, auth.rol], cargarPuntos)
// Estado real: interruptor manual + horario del día (ej. sábado hasta la 1pm).
const DIAS = ['Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'] as const
const HORKEY = ['horarioDom', 'horarioLun', 'horarioMar', 'horarioMie', 'horarioJue', 'horarioVie', 'horarioSab'] as const
function enHorario(rango: string, ahora: Date): boolean {
  const m = rango.match(/(\d{1,2}):(\d{2})\s*[–\-]\s*(\d{1,2}):(\d{2})/)
  if (!m) return false
  const min = ahora.getHours() * 60 + ahora.getMinutes()
  return min >= Number(m[1]) * 60 + Number(m[2]) && min < Number(m[3]) * 60 + Number(m[4])
}
const abiertoAhora = computed(() => {
  if (!empresa.tallerAbierto) return false
  const hoy = new Date().getDay()
  const key = HORKEY[hoy] ?? 'horarioDom'
  return enHorario(String((empresa as any)[key] ?? ''), new Date())
})
const filasHorario = computed(() => [
  { d: 'Lunes', h: empresa.horarioLun }, { d: 'Martes', h: empresa.horarioMar },
  { d: 'Miércoles', h: empresa.horarioMie }, { d: 'Jueves', h: empresa.horarioJue },
  { d: 'Viernes', h: empresa.horarioVie }, { d: 'Sábado', h: empresa.horarioSab },
  { d: 'Domingo', h: empresa.horarioDom },
])
</script>
<template>
  <div class="shop">
    <div class="announce" :class="{ cerrado: !empresa.tallerAbierto }">
      <template v-if="!empresa.tallerAbierto">🔧 Taller cerrado temporalmente{{ empresa.avisoTaller ? ` — ${empresa.avisoTaller}` : '' }}</template>
      <template v-else-if="empresa.avisoTaller">🔧 {{ empresa.avisoTaller }}</template>
      <template v-else>Neumáticos y refacciones en Huancayo — cotiza por WhatsApp</template>
    </div>
    <header class="shop-nav">
      <div class="shop-nav-inner">
        <div class="brand-wrap">
          <RouterLink to="/" class="wordmark" aria-label="Rapillantas inicio">
            <img src="/rapillantas_logo.png" alt="Rapillantas" class="logo-img" />
          </RouterLink>
          <div class="store-status" tabindex="0" @click="statusOpen = !statusOpen" :class="{ show: statusOpen }">
            <span class="dot" :class="abiertoAhora ? 'open' : 'closed'"></span>
            {{ abiertoAhora ? 'Abierto' : 'Cerrado' }}
            <div class="status-panel">
              <strong>Horario de atención</strong>
              <p v-for="f in filasHorario" :key="f.d" class="hrow"><span>{{ f.d }}</span><span>{{ f.h || '—' }}</span></p>
              <p v-if="empresa.avisoTaller" class="status-aviso">{{ empresa.avisoTaller }}</p>
            </div>
          </div>
        </div>
        <nav class="shop-links">
          <RouterLink to="/">Inicio</RouterLink>
          <RouterLink to="/neumaticos">Neumáticos</RouterLink>
          <RouterLink to="/refacciones">Refacciones</RouterLink>
          <RouterLink to="/servicios">Servicios</RouterLink>
          <RouterLink to="/ofertas">Ofertas</RouterLink>
        </nav>
        <div class="nav-right">
        <form @submit.prevent="buscar" class="nav-search" :class="{ open: searchOpen }">
          <input v-model="bq" placeholder="Buscar productos y servicios…" aria-label="Buscar" />
          <button type="button" class="icon-btn" @click="searchOpen = !searchOpen" aria-label="Buscar">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><circle cx="11" cy="11" r="7" /><path d="M20 20l-3.5-3.5" /></svg>
          </button>
        </form>
        <div class="nav-auth">
          <template v-if="!auth.listo">
            <RouterLink to="/login" class="btn-login" aria-label="Iniciar sesión">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><circle cx="12" cy="8" r="4" /><path d="M4 21c0-4 3.6-6.5 8-6.5s8 2.5 8 6.5" /></svg>
              <span class="login-txt">Iniciar sesión</span>
            </RouterLink>
          </template>
          <template v-else>
            <div class="nav-user">
              <button aria-label="Mi cuenta">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><circle cx="12" cy="8" r="4" /><path d="M4 21c0-4 3.6-6.5 8-6.5s8 2.5 8 6.5" /></svg>
                <span class="user-name">{{ auth.nombre }}</span>
                <span v-if="misPuntos !== null" class="pts-badge" title="Tus puntos">★ {{ misPuntos }}</span>
              </button>
              <div class="menu" @click="cerrarMenu()">
            <RouterLink v-if="auth.esCliente" to="/perfil">Mi perfil</RouterLink>
            <RouterLink v-if="auth.esAdmin || auth.paneles.length > 0" to="/admin">Admin</RouterLink>
                <a href="#" @click.prevent="salir">Salir</a>
              </div>
            </div>
          </template>
        </div>
        </div>
      </div>
    </header>
    <main class="shop-main"><RouterView /></main>
    <footer class="shop-footer">
      <div class="shop-footer-inner">
        <div>
          <h4>Rapillantas</h4>
          <p>{{ empresa.direccion }}</p>
          <p>{{ empresa.correo }}</p>
          <p v-if="empresa.ruc">RUC {{ empresa.ruc }}</p>
          <p v-if="empresa.facebook || empresa.instagram || empresa.tiktok">
            <a v-if="empresa.facebook" :href="empresa.facebook" target="_blank" rel="noopener">Facebook</a><span v-if="empresa.facebook && (empresa.instagram || empresa.tiktok)"> · </span><a v-if="empresa.instagram" :href="empresa.instagram" target="_blank" rel="noopener">Instagram</a><span v-if="empresa.instagram && empresa.tiktok"> · </span><a v-if="empresa.tiktok" :href="empresa.tiktok" target="_blank" rel="noopener">TikTok</a>
          </p>
        </div>
        <div>
          <h4>Tienda</h4>
          <RouterLink to="/neumaticos">Neumáticos</RouterLink>
          <RouterLink to="/refacciones">Refacciones</RouterLink>
          <RouterLink to="/servicios">Servicios</RouterLink>
        </div>
        <div>
          <h4>Clientes</h4>
          <RouterLink to="/perfil">Mi perfil</RouterLink>
          <RouterLink to="/contacto">Contacto</RouterLink>
        </div>
        <div>
          <h4>Empresa</h4>
          <RouterLink to="/nosotros">Nosotros</RouterLink>
          <RouterLink to="/terminos">Términos y condiciones</RouterLink>
        </div>
      </div>
    </footer>
    <Cotizador />
  </div>
</template>
<style scoped>
.pts-badge { font-weight: 800; font-size: 13px; color: #92400e; background: #fef3c7;
  border-radius: 999px; padding: 2px 10px; white-space: nowrap; }
.announce.cerrado { background: #7f1d1d; }
.brand-wrap { display: flex; align-items: center; gap: 10px; }
.store-status { position: relative; display: inline-flex; align-items: center; gap: 6px;
  font-size: 13px; font-weight: 700; cursor: default; white-space: nowrap; }
.store-status .dot { width: 10px; height: 10px; border-radius: 999px; }
.store-status .dot.open { background: #15803d; box-shadow: 0 0 0 3px #dcfce7; }
.store-status .dot.closed { background: #b91c1c; box-shadow: 0 0 0 3px #fee2e2; }
.status-panel { display: none; position: absolute; left: 0; top: 100%; margin-top: 8px; min-width: 220px;
  background: #fff; border: 1px solid var(--color-fog); border-radius: var(--radius-m);
  padding: 12px 14px; z-index: 30; box-shadow: 0 8px 24px rgba(0,0,0,.12); font-weight: 400; }
.store-status:hover .status-panel, .store-status:focus-within .status-panel, .store-status.show .status-panel { display: block; }
.status-panel p { margin: 4px 0 0; font-size: 14px; }
.hrow { display: flex; justify-content: space-between; gap: 16px; }
.status-aviso { color: var(--color-brand); font-weight: 700; }
/* Header en 3 zonas: marca | links centrados | buscador+cuenta */
.shop-nav-inner { grid-template-columns: 1fr auto 1fr; }
.brand-wrap { grid-column: 1; justify-self: start; }
.shop-links { grid-column: 2; }
.nav-right { grid-column: 3; justify-self: end; display: flex; align-items: center; gap: 12px; }
@media (max-width: 760px) {
  /* Móvil: [estado] [buscador centro] [usuario] + links en fila propia */
  .shop-nav-inner { grid-template-columns: auto 1fr auto; row-gap: 8px; }
  .nav-right { display: contents; }
  .brand-wrap .wordmark { display: none; }
  .brand-wrap { min-width: 0; }
  .nav-search { grid-column: 2; grid-row: 1; width: 100%; justify-content: stretch; }
  .nav-search .icon-btn { padding: 8px; order: 2; }
  .nav-search input, .nav-search.open input { width: 100%; flex: 1; min-width: 0; opacity: 1; border-color: var(--color-concrete); padding: 8px 16px; }
  .nav-auth { grid-column: 3; grid-row: 1; }
  .login-txt, .user-name, .pts-badge { display: none; }
  .btn-login { padding: 8px; border: none; }
  .shop-links { grid-column: 1 / -1; grid-row: 2; order: 3; flex-wrap: nowrap; justify-content: safe center; overflow-x: auto; overflow-y: hidden; gap: 16px; padding-bottom: 4px; }
  .shop-links a { white-space: nowrap; }
  .status-panel { position: fixed; left: 12px; right: 12px; top: auto; min-width: 0; }
}
</style>
