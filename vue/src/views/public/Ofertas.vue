<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api, waLink } from '../../lib/api'
const items = ref<any[]>([])
onMounted(async () => {
  const { data } = await api.get('/ofertas').catch(() => ({ data: [] }))
  items.value = data
})
</script>
<template>
  <div>
    <h1>Ofertas</h1>
    <ul>
      <li v-for="o in items" :key="o.id"><strong>{{ o.titulo }}</strong> — S/ {{ o.precioOferta }} <a :href="waLink(`Hola, quiero la oferta ${o.titulo}`)" target="_blank" rel="noopener">La quiero</a></li>
      <li v-if="!items.length">Publica desde Admin → Ofertas.</li>
    </ul>
  </div>
</template>
