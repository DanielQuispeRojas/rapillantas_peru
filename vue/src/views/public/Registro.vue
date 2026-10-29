<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../../stores/auth'
import { useSubmit } from '../../composables/useSubmit'
const g = useSubmit()
import GoogleButton from '../../components/GoogleButton.vue'
import AuthSlider from '../../components/AuthSlider.vue'

const form = ref({ login: '', clave: '', nombres: '', apellidos: '', correo: '', telefono: '' })
const msg = ref('')
const auth = useAuth()
const router = useRouter()

async function crear() {
  msg.value = ''
  try {
    await auth.registro(form.value)
    router.push('/perfil')
  } catch (e: any) { msg.value = e?.response?.data?.message ?? 'No se pudo registrar.' }
}
</script>
<template>
  <div class="auth-split">
    <div class="auth-wrap">
    <div class="auth-card">
    <p class="eyebrow">Cuenta</p>
    <h1 class="display display-md">Registro de cliente</h1>
      <form @submit.prevent="g.ejecutar(crear)">
        <input v-model="form.nombres" class="inp" placeholder="Nombres *" required autocomplete="given-name" />
        <input v-model="form.apellidos" class="inp" placeholder="Apellidos *" required autocomplete="family-name" />
        <input v-model="form.correo" class="inp" type="email" placeholder="Correo *" required autocomplete="email" />
        <input v-model="form.telefono" class="inp" placeholder="Teléfono *" required autocomplete="tel" />
        <input v-model="form.login" class="inp" placeholder="Usuario *" required autocomplete="username" />
        <input v-model="form.clave" class="inp" type="password" placeholder="Clave (mín 4) *" required autocomplete="new-password" />
        <button class="btn btn-primary" :disabled="g.ocupado.value">Crear cuenta ›</button>
      </form>
      <p v-if="msg">{{ msg }}</p>
      <p class="auth-alt">o</p>
      <GoogleButton />
    </div>
    </div>
    <AuthSlider />
  </div>
</template>
