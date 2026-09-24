<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../lib/api'
const items = ref<any[]>([])
const form = ref({ sku: '', nombre: '', categoria: 'neumatico', marca: '', medida: '', costoPromedio: 0, margenPct: 30 })
async function load() {
  const { data } = await api.get('/productos').catch(() => ({ data: [] }))
  items.value = data
}
async function crear() {
  await api.post('/productos', form.value)
  form.value = { sku: '', nombre: '', categoria: 'neumatico', marca: '', medida: '', costoPromedio: 0, margenPct: 30 }
  await load()
}
onMounted(load)
</script>
<template>
  <div>
    <h1>Productos / Stock</h1>
    <form @submit.prevent="crear" style="display:flex;gap:.5rem;flex-wrap:wrap">
      <input v-model="form.sku" placeholder="SKU" required />
      <input v-model="form.nombre" placeholder="Nombre" required />
      <input v-model="form.marca" placeholder="Marca" />
      <input v-model="form.medida" placeholder="Medida" />
      <input v-model.number="form.costoPromedio" type="number" placeholder="Costo" />
      <input v-model.number="form.margenPct" type="number" placeholder="Margen %" />
      <button>Crear</button>
    </form>
    <ul>
      <li v-for="p in items" :key="p.id">{{ p.sku }} {{ p.nombre }} — S/ {{ p.precio }} — stock {{ p.stockActual }} — {{ p.visibleTienda ? 'visible' : 'oculto' }}</li>
    </ul>
  </div>
</template>
