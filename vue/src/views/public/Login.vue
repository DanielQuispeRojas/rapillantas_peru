<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../../stores/auth'
import { useSubmit } from '../../composables/useSubmit'
const g = useSubmit()
import GoogleButton from '../../components/GoogleButton.vue'
import AuthSlider from '../../components/AuthSlider.vue'

const login = ref('')
const clave = ref('')
const msg = ref('')
const auth = useAuth()
const router = useRouter()
const aviso = localStorage.getItem('sesionMsg') ?? ''
if (aviso) localStorage.removeItem('sesionMsg')

async function entrar() {
  msg.value = ''
  try {
    const d = await auth.loginU(login.value, clave.value)
    if (d.rol === 'ADMIN') router.push('/admin')
    else router.push('/perfil')
  } catch { msg.value = 'Credenciales inválidas.' }
}
</script>
<template>
  <div class="auth-split">
    <div class="auth-wrap">
    <div class="auth-card">
    <p class="eyebrow">Cuenta</p>
    <h1 class="display display-md">Iniciar sesión</h1>
      <form @submit.prevent="g.ejecutar(entrar)">
        <input v-model="login" class="inp" placeholder="Usuario" required autocomplete="username" />
        <input v-model="clave" class="inp" type="password" placeholder="Clave" required autocomplete="current-password" />
        <button class="btn btn-primary" :disabled="g.ocupado.value">Entrar</button>
      </form>
      <p v-if="aviso" class="muted">{{ aviso }}</p>
      <p v-if="msg">{{ msg }}</p>
      <p class="auth-alt">o</p>
      <GoogleButton />
      <p class="muted">¿No tienes cuenta? <RouterLink to="/registro">Regístrate</RouterLink></p>
    </div>
    </div>
    <AuthSlider />
  </div>
</template>

<style scoped>
@media (max-width: 859px) {
  .auth-side { display: none; }
}
</style>
