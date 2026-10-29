<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../lib/api'
import { useSubmit } from '../../composables/useSubmit'
const g = useSubmit()

// Datos de la empresa: WhatsApp (lo consumen los botones), dirección, correo,
// RUC y redes (variables dinámicas que usa la tienda: footer, contacto).
// Más estado del taller (banner) + aviso (ej. "sábado sin atención").
const DIAS = [
  { key: 'horarioLun', label: 'Lunes' }, { key: 'horarioMar', label: 'Martes' },
  { key: 'horarioMie', label: 'Miércoles' }, { key: 'horarioJue', label: 'Jueves' },
  { key: 'horarioVie', label: 'Viernes' }, { key: 'horarioSab', label: 'Sábado' },
  { key: 'horarioDom', label: 'Domingo' },
] as const
const form = ref({ whatsapp: '', direccion: '', correo: '', ruc: '', facebook: '', instagram: '', tiktok: '', tallerAbierto: true, avisoTaller: '',
  horarioLun: '', horarioMar: '', horarioMie: '', horarioJue: '', horarioVie: '', horarioSab: '', horarioDom: '' })
const msg = ref('')
async function load() {
  const { data } = await api.get('/empresa').catch(() => ({ data: null }))
  if (data) form.value = {
    whatsapp: data.whatsapp, direccion: data.direccion, correo: data.correo,
    ruc: data.ruc ?? '', facebook: data.facebook ?? '',
    instagram: data.instagram ?? '', tiktok: data.tiktok ?? '',
    tallerAbierto: data.tallerAbierto ?? true, avisoTaller: data.avisoTaller ?? '',
    horarioLun: data.horarioLun ?? '', horarioMar: data.horarioMar ?? '',
    horarioMie: data.horarioMie ?? '', horarioJue: data.horarioJue ?? '',
    horarioVie: data.horarioVie ?? '', horarioSab: data.horarioSab ?? '',
    horarioDom: data.horarioDom ?? '',
  }
}
async function guardar() {
  await api.put('/admin/empresa', form.value).catch((e: any) => alert(e?.response?.data?.message ?? 'No se pudo guardar'))
  msg.value = 'Datos actualizados.'
  setTimeout(() => (msg.value = ''), 2500)
}
onMounted(load)
</script>
<template>
  <div style="max-width:560px">
    <h1>Datos de la empresa</h1>
    <div style="display:flex;flex-direction:column;gap:.6rem">
      <label>WhatsApp (solo números, con código país) *
        <input v-model="form.whatsapp" required placeholder="51999999999" style="width:100%" />
      </label>
      <label>Dirección *
        <input v-model="form.direccion" required style="width:100%" />
      </label>
      <label>Correo *
        <input v-model="form.correo" type="email" required style="width:100%" />
      </label>
      <label>RUC
        <input v-model="form.ruc" inputmode="numeric" maxlength="11" placeholder="20…" style="width:100%" />
      </label>
      <label>Facebook (URL completa)
        <input v-model="form.facebook" type="url" placeholder="https://facebook.com/…" style="width:100%" />
      </label>
      <label>Instagram (URL completa)
        <input v-model="form.instagram" type="url" placeholder="https://instagram.com/…" style="width:100%" />
      </label>
      <label>TikTok (URL completa)
        <input v-model="form.tiktok" type="url" placeholder="https://tiktok.com/…" style="width:100%" />
      </label>
      <fieldset style="display:flex;gap:.6rem;flex-wrap:wrap;align-items:end">
        <legend>Estado del taller (banner de la tienda)</legend>
        <label><input v-model="form.tallerAbierto" type="checkbox" /> Taller abierto</label>
        <label style="flex:1;min-width:220px">Aviso (ej. sábado 12 sin atención)
          <input v-model="form.avisoTaller" placeholder="Dejar vacío para ocultar" style="width:100%" />
        </label>
      </fieldset>
      <fieldset style="display:flex;gap:.6rem;flex-wrap:wrap">
        <legend>Horario por día ("8:00–18:00" o "cerrado")</legend>
        <label v-for="d in DIAS" :key="d.key">{{ d.label }}
          <input v-model="(form as any)[d.key]" placeholder="8:00–18:00" style="width:130px" />
        </label>
      </fieldset>
      <small>Si el taller está cerrado, el banner y el estado junto al logo lo muestran en rojo.</small>
      <button class="primary" @click="g.ejecutar(guardar)" :disabled="g.ocupado.value">Guardar</button>
      <p v-if="msg">{{ msg }}</p>
    </div>
  </div>
</template>
