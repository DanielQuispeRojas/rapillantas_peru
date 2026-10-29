<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { api } from '../../lib/api'
import { useSubmit } from '../../composables/useSubmit'
const g = useSubmit()

// Bandeja de valoraciones: lo no visto (nuevo o editado) lleva indicador.
const items = ref<any[]>([])
const nuevos = computed(() => items.value.filter((v: any) => !v.vistoAdmin).length)
const estrellasTxt = (n: number) => '★'.repeat(n) + '☆'.repeat(5 - n)

async function load() {
  const { data } = await api.get('/admin/valoraciones').catch(() => ({ data: [] }))
  items.value = data
}
async function marcarVisto(id: string) {
  await api.put(`/admin/valoraciones/${id}/visto`)
  await load()
}
async function eliminar(id: string) {
  if (!confirm('¿Eliminar comentario?')) return
  await api.delete(`/admin/valoraciones/${id}`)
  await load()
}
onMounted(load)
</script>
<template>
  <div>
    <div class="title-row">
      <h1>Valoraciones de clientes</h1>
      <span v-if="nuevos" class="dscto-tag" style="margin-left:auto">{{ nuevos }} sin revisar</span>
    </div>
    <table style="margin-top:.5rem">
      <thead><tr><th></th><th>Cliente</th><th>★</th><th>Comentario</th><th>Actualizado</th><th>Acciones</th></tr></thead>
      <tbody>
        <tr v-for="v in items" :key="v.id" :style="v.vistoAdmin ? '' : 'background:#fdf0f2'">
          <td><strong v-if="!v.vistoAdmin" style="color:#c8102e">●</strong></td>
          <td>{{ v.nombres }}</td>
          <td style="white-space:nowrap"><span class="stars-static">{{ estrellasTxt(v.estrellas) }}</span></td>
          <td>{{ v.comentario }}</td>
          <td style="white-space:nowrap">{{ String(v.updatedAt ?? '').slice(0, 10) }}</td>
          <td style="white-space:nowrap">
            <button v-if="!v.vistoAdmin" @click="g.ejecutar(() => marcarVisto(v.id))" :disabled="g.ocupado.value">Marcar visto</button>
            <button @click="g.ejecutar(() => eliminar(v.id))" :disabled="g.ocupado.value">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="!items.length"><small>Sin valoraciones todavía.</small></p>
  </div>
</template>
