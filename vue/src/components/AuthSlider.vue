<script setup lang="ts">
import { useSlider } from '../composables/useSlider'

// Slider lateral de auth (clave "login", gestionado en Admin → Portada).
const { slides, idx } = useSlider('login', 5000)
</script>
<template>
  <div class="auth-side">
    <img v-for="(s, i) in slides" :key="i" :src="s.src" alt="" :class="{ on: i === idx }" />
  </div>
</template>
