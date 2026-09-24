<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api, waLink } from '../../lib/api'

const ofertas = ref<any[]>([])
const q = ref('')
onMounted(async () => {
  const { data } = await api.get('/ofertas').catch(() => ({ data: [] }))
  ofertas.value = data
})
</script>
<template>
  <div>
    <h1>Neumáticos y refacciones en Huancayo</h1>
    <form @submit.prevent="$router.push(`/neumaticos?medida=${q}`)">
      <input v-model="q" placeholder="Busca por medida ej. 205/55R16" />
      <button>Buscar</button>
    </form>
    <h2>Ofertas destacadas</h2>
    <ul>
      <li v-for="o in ofertas" :key="o.id">{{ o.titulo }} — <a :href="waLink(`Hola, me interesa la oferta ${o.titulo}`)" target="_blank" rel="noopener">Cotizar</a></li>
      <li v-if="!ofertas.length">Conecta el backend para ver ofertas (GET /ofertas).</li>
    </ul>
  </div>
</template>
