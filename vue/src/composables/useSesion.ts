import { api } from '../lib/api'
import { useAuth } from '../stores/auth'

// Sesión por inactividad (60 min):
// - cualquier interacción resetea el temporizador;
// - sin actividad 60 min => cierre + aviso al próximo login;
// - con actividad y token por vencer (<10 min) => renovación silenciosa.
const INACTIVO_MS = 60 * 60 * 1000
const RENOVAR_ANTES_MS = 10 * 60 * 1000
const REVISAR_CADA_MS = 60 * 1000

function expDe(token: string): number {
  try {
    const seg = token.split('.')[1] ?? ''
    const p = JSON.parse(atob(seg.replace(/-/g, '+').replace(/_/g, '/')))
    return Number(p.exp ?? 0) * 1000
  } catch {
    return 0
  }
}

let iniciado = false
export function iniciarSesion() {
  if (iniciado) return
  iniciado = true
  let ultima = Date.now()
  const tocar = () => { ultima = Date.now() }
  for (const ev of ['click', 'keydown', 'scroll', 'touchstart', 'mousemove']) {
    window.addEventListener(ev, tocar, { passive: true })
  }
  window.setInterval(async () => {
    const auth = useAuth()
    const t = localStorage.getItem('token')
    if (!auth.listo || !t) return
    if (Date.now() - ultima > INACTIVO_MS) {
      auth.salir()
      localStorage.setItem('sesionMsg', 'Sesión cerrada por una hora de inactividad.')
      window.location.href = '/login'
      return
    }
    // Renovación deslizante: token por vencer + usuario activo => token fresco.
    const exp = expDe(t)
    if (exp && exp - Date.now() < RENOVAR_ANTES_MS) {
      try {
        const { data } = await api.post('/auth/renovar')
        if (data?.token) {
          localStorage.setItem('token', data.token)
          auth.token = data.token
        }
      } catch {
        // Si falla (401), el interceptor lleva a /login.
      }
    }
  }, REVISAR_CADA_MS)
}
