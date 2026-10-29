<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../lib/api'
import { useSubmit } from '../../composables/useSubmit'
const g = useSubmit()
import { PANELES } from '../../stores/auth'

// ADMIN: crea accesos internos eligiendo rol + paneles a medida.
// Ej. encargado de tienda: canjes + portada (fotos, promociones, ofertas).
const items = ref<any[]>([])
const form = ref({ nombre: '', login: '', clave: '', rol: 'COLABORADOR', paneles: [] as string[] })
async function load() {
  const { data } = await api.get('/auth/usuarios').catch(() => ({ data: [] }))
  items.value = data
}
async function crear() {
  await api.post('/auth/usuarios', { ...form.value, paneles: form.value.paneles.join(',') })
    .catch((e: any) => alert(e?.response?.data?.message ?? 'No se pudo crear'))
  form.value = { nombre: '', login: '', clave: '', rol: 'COLABORADOR', paneles: [] }
  await load()
}
onMounted(load)
</script>
<template>
  <div>
    <h1>Usuarios internos</h1>
    <form @submit.prevent="g.ejecutar(crear)" style="display:flex;gap:.5rem;flex-wrap:wrap;align-items:end">
      <label>Nombre <input v-model="form.nombre" required /></label>
      <label>Login <input v-model="form.login" required /></label>
      <label>Clave <input v-model="form.clave" type="password" required /></label>
      <label>Rol
        <select v-model="form.rol">
          <option value="COLABORADOR">COLABORADOR (paneles a medida)</option>
          <option value="ADMIN">ADMIN (todo)</option>
        </select>
      </label>
      <button>Crear acceso</button>
    </form>
    <fieldset v-if="form.rol !== 'ADMIN'" style="margin-top:.6rem;max-width:640px">
      <legend>Paneles con acceso</legend>
      <label v-for="p in PANELES" :key="p" style="display:inline-flex;flex-direction:row;align-items:center;gap:4px;margin:0 .8rem .4rem 0">
        <input type="checkbox" :value="p" v-model="form.paneles" /> {{ p }}
      </label>
    </fieldset>
    <ul><li v-for="u in items" :key="u.id">{{ u.login }} — {{ u.nombre }} ({{ u.rol }}) <small>{{ u.paneles }}</small></li></ul>
  </div>
</template>
