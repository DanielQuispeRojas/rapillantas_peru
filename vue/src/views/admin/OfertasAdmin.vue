<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../lib/api'
const items = ref<any[]>([])
const form = ref({ titulo: '', productoId: '', precioOferta: 0 })
async function load() {
  const { data } = await api.get('/admin/ofertas').catch(() => ({ data: [] }))
  items.value = data
}
async function crear() {
  await api.post('/admin/ofertas', { ...form.value, productoId: form.value.productoId || null })
  await load()
}
onMounted(load)
</script>
<template>
  <div>
    <h1>Ofertas (publican en tienda)</h1>
    <form @submit.prevent="crear" style="display:flex;gap:.5rem">
      <input v-model="form.titulo" placeholder="Título" required />
      <input v-model="form.productoId" placeholder="productoId (opcional)" />
      <input v-model.number="form.precioOferta" type="number" placeholder="Precio oferta" />
      <button>Publicar</button>
    </form>
    <ul><li v-for="o in items" :key="o.id">{{ o.titulo }} — S/ {{ o.precioOferta }}</li></ul>
  </div>
</template>
