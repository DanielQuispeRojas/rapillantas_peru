<script setup lang="ts">
import { ref } from 'vue'
import { useAuth } from '../stores/auth'
import { useRouter } from 'vue-router'
const auth = useAuth()
const router = useRouter()
const menu = ref(false)
function salir() { auth.salir(); router.push('/') }
</script>
<template>
  <div class="erp">
    <div class="erp-shell">
      <aside class="erp-side" :class="{ open: menu }">
        <div class="erp-side-top">
          <img src="/rapillantas_extendido.png" alt="Rapillantas" class="logo-img" />
          <button class="erp-side-close" @click="menu = false" aria-label="Cerrar menú">✕</button>
        </div>
        <div class="erp-user">{{ auth.nombre }} · <a href="#" @click.prevent="salir">Salir</a></div>
        <nav class="erp-nav" @click="menu = false">
          <h5 v-if="auth.puede('canjes')"><span aria-hidden="true">??</span> Comercial</h5>
          <RouterLink v-if="auth.puede('canjes')" to="/admin/canjes">Canjes</RouterLink>
          <h5 v-if="auth.puede('portada') || auth.puede('empresa') || auth.puede('usuarios')"><span aria-hidden="true">⚙️</span> Sistema</h5>
          <RouterLink v-if="auth.puede('portada')" to="/admin/portada">Portada</RouterLink>
          <RouterLink v-if="auth.puede('portada')" to="/admin/ofertas">Ofertas</RouterLink>
          <RouterLink v-if="auth.puede('valoraciones')" to="/admin/valoraciones">Valoraciones</RouterLink>
          <RouterLink v-if="auth.puede('empresa')" to="/admin/empresa">Empresa</RouterLink>
          <RouterLink v-if="auth.puede('usuarios')" to="/admin/usuarios">Usuarios</RouterLink>
          <RouterLink to="/">← Volver a la tienda</RouterLink>
        </nav>
      </aside>
      <div v-if="menu" class="erp-backdrop" @click="menu = false"></div>
      <div class="erp-main">
        <header class="erp-top">
          <button class="erp-burger" @click="menu = !menu">☰</button>
          <span class="crumb">Administración</span>
        </header>
        <main class="erp-body"><RouterView /></main>
      </div>
    </div>
  </div>
</template>

<style scoped>
.erp-side-top { display: flex; align-items: center; gap: 8px; padding: 16px 14px 4px; }
.erp-side-top .logo-img { flex: 1; min-width: 0; object-fit: contain; }
.erp-side-close { display: none; width: 32px; height: 32px; border-radius: 999px; border: 1px solid rgba(255,255,255,0.25); background: transparent; color: #fff; flex-shrink: 0; line-height: 1; padding: 0; }
.erp-backdrop { display: none; }
.erp-nav h5 { display: flex; align-items: center; gap: 6px; }
@media (max-width: 900px) {
  .erp-side-close { display: inline-flex; align-items: center; justify-content: center; }
  .erp-backdrop { display: block; position: fixed; inset: 0; background: rgba(0,0,0,0.45); z-index: 30; }
  .erp-side.open { z-index: 40; }
}
</style>
