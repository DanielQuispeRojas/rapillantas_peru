import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1',
})

// JWT: se adjunta solo si hay sesión (visitantes navegan sin token)
api.interceptors.request.use((cfg) => {
  const t = localStorage.getItem('token')
  if (t) cfg.headers.Authorization = `Bearer ${t}`
  return cfg
})

// Errores predecibles: 401 (sesión expirada) → login; 403 (sin permiso) → sin-acceso.
// El resto se propaga para el manejo local de cada vista.
api.interceptors.response.use(
  (r) => r,
  (e) => {
    const s = e?.response?.status
    const aqui = window.location.pathname
    if (s === 401 && !aqui.startsWith('/login') && !aqui.startsWith('/registro')) {
      localStorage.removeItem('token'); localStorage.removeItem('rol')
      localStorage.removeItem('nombre'); localStorage.removeItem('login'); localStorage.removeItem('paneles')
      window.location.href = '/login'
    } else if (s === 403 && aqui !== '/sin-acceso') {
      window.location.href = '/sin-acceso'
    }
    return Promise.reject(e)
  },
)

// Datos de la empresa (los botones de WhatsApp consumen este número).
// Se cargan una vez desde /empresa; el fallback es el seed.
export const empresa = {
  whatsapp: '51999999999', direccion: 'Huancayo, Perú', correo: 'contacto@rapillantas.pe',
  ruc: '', facebook: '', instagram: '', tiktok: '',
  tallerAbierto: true, avisoTaller: '',
  horarioLun: '8:00–18:00', horarioMar: '8:00–18:00', horarioMie: '8:00–18:00',
  horarioJue: '8:00–18:00', horarioVie: '8:00–18:00', horarioSab: '8:00–13:00',
  horarioDom: 'cerrado',
}
export async function cargarEmpresa() {
  const { data } = await api.get('/empresa').catch(() => ({ data: null }))
  if (data) Object.assign(empresa, data)
}

export const waLink = (msg: string) =>
  `https://wa.me/${empresa.whatsapp}?text=${encodeURIComponent(msg)}`

// Las fotos subidas desde el dispositivo se guardan como /uploads/... en el backend.
// El flyer de portada vive en el frontend (/portada/..., /banners/...) y se sirve directo.
// Esta ayuda resuelve cada caso para <img>.
export const imgSrc = (url?: string | null) => {
  if (!url) return ''
  if (/^(https?:|data:|blob:)/.test(url)) return url
  if (url.startsWith('/portada/') || url.startsWith('/banners/')) return url
  const base = (import.meta.env.VITE_API_URL as string | undefined) ?? 'http://localhost:8080/api/v1'
  return base.replace(/\/api\/v1\/?$/, '') + url
}
