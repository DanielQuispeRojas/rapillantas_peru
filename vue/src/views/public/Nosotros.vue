<script setup lang="ts">
import { cargarEmpresa, empresa } from '../../lib/api'
import { onMounted } from 'vue'
onMounted(cargarEmpresa)
</script>
<template>
  <div>
    <section v-reveal class="shop-section wrap">
      <p class="eyebrow">El taller</p>
      <h1 class="display display-xl">Sobre nosotros</h1>
      <p class="lead">TODO: frase de presentación en una línea (ej. "Tu taller de confianza en Huancayo desde 20XX").</p>
    </section>

    <section v-reveal class="shop-section band-fog">
      <div class="wrap">
        <p class="eyebrow">Cómo llegar</p>
        <h2 class="display display-md">Encuéntranos</h2>
        <p class="lead">{{ empresa.direccion }}</p>
        <!-- TODO: pega aquí el iframe de Google Maps (Compartir > Insertar mapa).
             Ejemplo:
             <iframe src="https://www.google.com/maps/embed?pb=..." width="100%" height="320"
               style="border:0" loading="lazy" title="Mapa Rapillantas"></iframe> -->
        <div style="background:#fff;border:1px dashed #bbbbbb;border-radius:12px;padding:48px 16px;text-align:center;color:#6b6b6b">
          Aquí irá el mapa de cómo llegar (pega el iframe de Google Maps)
        </div>
      </div>
    </section>

    <section v-reveal class="shop-section">
      <div class="wrap">
        <p class="eyebrow">Historia</p>
        <h2 class="display display-md">Nuestra historia</h2>
        <p class="lead">TODO: 2–3 párrafos — cómo empezó el taller, hitos (apertura, ampliación, servicios nuevos).</p>
        <p>TODO: párrafo 1 — orígenes.</p>
        <p>TODO: párrafo 2 — crecimiento y presente.</p>
      </div>
    </section>

    <section v-reveal class="shop-section band-fog">
      <div class="wrap">
        <p class="eyebrow">Propósito</p>
        <h2 class="display display-md">Misión y visión</h2>
        <div class="offer-cards">
          <div class="offer-card">
            <h3>Misión</h3>
            <p>TODO: qué hacemos hoy y para quién (ej. "Mantener rodando a Huancayo con…").</p>
          </div>
          <div class="offer-card">
            <h3>Visión</h3>
            <p>TODO: a dónde vamos (ej. "Ser el taller de referencia en…").</p>
          </div>
        </div>
      </div>
    </section>

    <section v-reveal class="shop-section">
      <div class="wrap" style="text-align:center">
        <h2 class="display display-md">¿Cotizamos?</h2>
        <p style="margin-top:16px"><a class="btn btn-primary" :href="`https://wa.me/${empresa.whatsapp}?text=${encodeURIComponent('Hola, quisiera cotizar')}`" target="_blank" rel="noopener">WhatsApp ›</a></p>
      </div>
    </section>
  </div>
</template>
