<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { api, waLink } from '../../lib/api'

const props = defineProps<{ categoria: string }>()
const route = useRoute()
const items = ref<any[]>([])
const marca = ref('')
const medida = ref((route.query.medida as string) ?? '')

async function load() {
  const { data } = await api.get('/catalogo', { params: { categoria: props.categoria, marca: marca.value || undefined, medida: medida.value || undefined } }).catch(() => ({ data: [] }))
  items.value = data
}
onMounted(load)
watch(() => route.query.medida, (v) => { medida.value = (v as string) ?? ''; load() })
</script>
<template>
  <div>
    <h1>{{ props.categoria === 'neumatico' ? 'Neumáticos' : 'Refacciones' }}</h1>
    <div style="display:flex;gap:.5rem">
      <input v-model="marca" placeholder="Marca" />
      <input v-model="medida" placeholder="Medida" />
      <button @click="load">Filtrar</button>
    </div>
    <ul>
      <li v-for="p in items" :key="p.id">{{ p.nombre }} — {{ p.marca }} {{ p.medida }} — S/ {{ p.precio }} (stock {{ p.stockActual }}) <a :href="waLink(`Hola, me interesa ${p.nombre} ${p.medida ?? ''}`)" target="_blank" rel="noopener">Cotizar</a></li>
      <li v-if="!items.length">Sin datos — verifica backend GET /catalogo.</li>
    </ul>
  </div>
</template>
