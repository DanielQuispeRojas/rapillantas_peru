<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../lib/api'
import { useAuth } from '../stores/auth'
import { useSubmit } from '../composables/useSubmit'
const g = useSubmit()

// Botón "Regístrate con Google" (Google Identity Services).
// Requiere GOOGLE_CLIENT_ID en el backend; sin él muestra aviso.
const clientId = ref('')
const listo = ref(false)
const msg = ref('')
const auth = useAuth()
const router = useRouter()

function destino(d: any) {
  if (d.rol === 'ADMIN') router.push('/admin')
  else router.push('/perfil')
}
function onCredential(resp: any) {
  g.ejecutar(async () => {
    msg.value = ''
    try {
      const { data } = await api.post('/auth/google', { idToken: resp.credential })
      auth.guardar(data)
      destino(data)
    } catch (e: any) { msg.value = e?.response?.data?.message ?? 'Google no válido.' }
  })
}
onMounted(async () => {
  const { data } = await api.get('/auth/google-client-id').catch(() => ({ data: { clientId: '' } }))
  clientId.value = data.clientId ?? ''
  listo.value = true
  if (!clientId.value) return
  const s = document.createElement('script')
  s.src = 'https://accounts.google.com/gsi/client'
  s.onload = () => {
    ;(window as any).google.accounts.id.initialize({ client_id: clientId.value, callback: onCredential })
    ;(window as any).google.accounts.id.renderButton(document.getElementById('gbtn'), { theme: 'outline', size: 'large', text: 'signup_with' })
  }
  document.head.appendChild(s)
})
defineExpose({})
</script>
<template>
  <!-- Pendiente de configurar GOOGLE_CLIENT_ID: no se muestra nada hasta entonces -->
  <div v-if="listo && clientId">
    <div id="gbtn"></div>
    <p v-if="msg">{{ msg }}</p>
  </div>
</template>
