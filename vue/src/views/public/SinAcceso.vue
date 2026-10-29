<script setup lang="ts">
import { useAuth } from '../../stores/auth'
const auth = useAuth()
</script>
<template>
  <div class="narrow" style="text-align:center">
    <section class="shop-section">
      <p class="eyebrow">Error 403</p>
      <h1 class="display display-xl">Sin acceso</h1>
      <p class="lead">Tu cuenta no tiene permiso para esta sección.</p>
      <p style="margin-top:24px;display:flex;gap:.6rem;justify-content:center;flex-wrap:wrap">
        <RouterLink v-if="!auth.listo" to="/login" class="btn btn-primary">Iniciar sesión ›</RouterLink>
        <RouterLink v-if="auth.esAdmin || auth.paneles.length > 0" to="/admin" class="btn btn-primary">Ir al admin ›</RouterLink>
        <RouterLink to="/" class="btn btn-outline">Ir a la tienda</RouterLink>
      </p>
    </section>
  </div>
</template>
