<script setup lang="ts">
import { onMounted } from 'vue'
import { cargarEmpresa, empresa, waLink } from '../../lib/api'
onMounted(cargarEmpresa)
</script>
<template>
  <div class="narrow left">
    <section class="shop-section">
      <p class="eyebrow">Visítanos</p>
      <h1 class="display display-md">Contacto Huancayo</h1>
      <p class="lead">Taller presencial + WhatsApp y llamadas. Escríbenos para cotizar neumáticos, refacciones o reservar servicio.</p>
      <p>{{ empresa.direccion }} · {{ empresa.correo }}</p>
      <p v-if="empresa.ruc">RUC {{ empresa.ruc }}</p>
      <p><a class="btn btn-primary" :href="waLink('Hola, quisiera cotizar')" target="_blank" rel="noopener">WhatsApp Rapillantas</a></p>
      <p v-if="empresa.facebook || empresa.instagram || empresa.tiktok" style="margin-top:16px">
        <a v-if="empresa.facebook" class="arrow-link" style="display:inline" :href="empresa.facebook" target="_blank" rel="noopener">Facebook</a><span v-if="empresa.facebook && (empresa.instagram || empresa.tiktok)"> · </span><a v-if="empresa.instagram" class="arrow-link" style="display:inline" :href="empresa.instagram" target="_blank" rel="noopener">Instagram</a><span v-if="empresa.instagram && empresa.tiktok"> · </span><a v-if="empresa.tiktok" class="arrow-link" style="display:inline" :href="empresa.tiktok" target="_blank" rel="noopener">TikTok</a>
      </p>
    </section>
  </div>
</template>
