<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../lib/api'
const items = ref<any[]>([])
onMounted(async () => {
  const { data } = await api.get('/servicios').catch(() => ({ data: [] }))
  items.value = data
})
</script>
<template>
  <div>
    <h1>Servicios</h1>
    <ul>
      <li v-for="s in items" :key="s.id">{{ s.nombre }} — {{ s.descripcion }} — S/ {{ s.precioRef }}</li>
      <li v-if="!items.length">Alineamiento, frenos, suspensión y rotación (backend GET /servicios).</li>
    </ul>
  </div>
</template>
