import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '../lib/api'

export const PANELES = ['canjes', 'portada', 'empresa', 'usuarios', 'valoraciones'] as const

export const useAuth = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') ?? '')
  const rol = ref(localStorage.getItem('rol') ?? '')
  const nombre = ref(localStorage.getItem('nombre') ?? '')
  const login = ref(localStorage.getItem('login') ?? '')
  const paneles = ref<string[]>((localStorage.getItem('paneles') ?? '').split(',').filter(Boolean))

  const listo = computed(() => !!token.value)
  const esAdmin = computed(() => rol.value === 'ADMIN')
  const esMecanico = computed(() => rol.value === 'MECANICO' || rol.value === 'ADMIN')
  const esCliente = computed(() => rol.value === 'CLIENTE')
  // ADMIN ve todo; el resto solo sus paneles (MECANICO trae 'taller' por defecto)
  const puede = (panel: string) => esAdmin.value || paneles.value.includes(panel)

  function guardar(d: any) {
    token.value = d.token
    rol.value = d.rol
    nombre.value = d.nombre
    login.value = d.login
    const p = typeof d.paneles === 'string' ? d.paneles : (d.paneles ?? []).join?.(',') ?? ''
    paneles.value = p.split(',').filter(Boolean)
    localStorage.setItem('token', d.token)
    localStorage.setItem('rol', d.rol)
    localStorage.setItem('nombre', d.nombre)
    localStorage.setItem('login', d.login)
    localStorage.setItem('paneles', p)
  }

  async function loginU(l: string, clave: string) {
    const { data } = await api.post('/auth/login', { login: l, clave })
    guardar(data)
    return data
  }
  async function registro(d: any) {
    const { data } = await api.post('/auth/registro', d)
    guardar(data)
    return data
  }
  function salir() {
    token.value = ''; rol.value = ''; nombre.value = ''; login.value = ''; paneles.value = []
    localStorage.removeItem('token'); localStorage.removeItem('rol')
    localStorage.removeItem('nombre'); localStorage.removeItem('login'); localStorage.removeItem('paneles')
  }
  return { token, rol, nombre, login, paneles, listo, esAdmin, esMecanico, esCliente, puede, loginU, registro, salir, guardar }
})
