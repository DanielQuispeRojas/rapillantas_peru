import { createRouter, createWebHistory } from 'vue-router'
import PublicLayout from '../layouts/PublicLayout.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import { applySeo, SITE, type SeoMeta } from '../lib/seo'

// Niveles: VISITANTE (anonimo, tienda) - CLIENTE (perfil) -
// COLABORADOR (paneles a medida) - ADMIN (todo).
// El taller y el almacen viven en MOODU; sin vistas de mecanico ni catalogo operativo.

const seo = (path: string, meta: SeoMeta): SeoMeta => ({
  ...meta,
  canonical: `${SITE.baseUrl}${path}`,
})

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/', component: PublicLayout, children: [
        { path: '', component: () => import('../views/public/Home.vue'), meta: { seo: seo('/', { title: 'Rapillantas Huancayo — Neumáticos, refacciones y taller', description: 'Neumáticos, refacciones y taller mecánico en Huancayo: alineamiento, balanceo, frenos, cambio de aceite. Cotiza por WhatsApp.', keywords: 'neumáticos Huancayo, llantas Huancayo, taller mecánico Huancayo, alineamiento, balanceo, Rapillantas' }) } },
        { path: 'neumaticos', component: () => import('../views/public/Catalogo.vue'), props: { categoria: 'neumatico' }, meta: { seo: seo('/neumaticos', { title: 'Neumáticos en Huancayo | Rapillantas', description: 'Catálogo de neumáticos en Huancayo por marca, modelo y medida: auto, camioneta, camión. Stock real y cotización inmediata.', keywords: 'neumáticos Huancayo, llantas auto, llantas camioneta, medidas llantas, 185/65R15, 265/70R16, Rapillantas' }) } },
        { path: 'refacciones', component: () => import('../views/public/Catalogo.vue'), props: { categoria: 'refaccion' }, meta: { seo: seo('/refacciones', { title: 'Refacciones y repuestos en Huancayo | Rapillantas', description: 'Refacciones y repuestos en Huancayo: pastillas de freno, filtros, amortiguadores, baterías. Busca por marca, modelo o etiqueta.', keywords: 'refacciones Huancayo, repuestos auto, pastillas freno, filtros aceite, amortiguadores, baterías auto' }) } },
        { path: 'servicios', component: () => import('../views/public/Servicios.vue'), meta: { seo: seo('/servicios', { title: 'Taller mecánico en Huancayo: alineamiento, frenos | Rapillantas', description: 'Taller en Huancayo: alineamiento, balanceo, frenos, cambio de aceite, revisión técnica. Precios referenciales y cotización por WhatsApp.', keywords: 'taller mecánico Huancayo, alineamiento Huancayo, balanceo, frenos, cambio aceite, mantenimiento vehicular' }) } },
        { path: 'ofertas', component: () => import('../views/public/Ofertas.vue'), meta: { seo: seo('/ofertas', { title: 'Ofertas en llantas y servicios | Rapillantas Huancayo', description: 'Ofertas vigentes en neumáticos, refacciones y servicios en Huancayo. Descuentos y canje de puntos.', keywords: 'ofertas llantas Huancayo, descuentos neumáticos, promociones taller, canje puntos' }) } },
        { path: 'buscar', component: () => import('../views/public/Buscar.vue'), meta: { seo: seo('/buscar', { title: 'Buscar productos y servicios | Rapillantas', description: 'Busca neumáticos, refacciones y servicios por marca, medida o etiqueta en Rapillantas Huancayo.', keywords: 'buscar llantas, buscar repuestos, buscador taller', noindex: true }) } },
        { path: 'producto/:id', component: () => import('../views/public/ProductoDetalle.vue'), meta: { seo: seo('/producto', { title: 'Detalle de producto | Rapillantas Huancayo', description: 'Ficha de producto con precio, stock y descripción en Rapillantas Huancayo.', keywords: 'llantas, refacciones, detalle producto, Rapillantas' }) } },
        { path: 'login', component: () => import('../views/public/Login.vue'), meta: { seo: { noindex: true } } },
        { path: 'registro', component: () => import('../views/public/Registro.vue'), meta: { seo: { noindex: true } } },
        { path: 'perfil', component: () => import('../views/public/Perfil.vue'), meta: { rol: 'CLIENTE', seo: { noindex: true } } },
        { path: 'contacto', component: () => import('../views/public/Contacto.vue'), meta: { seo: seo('/contacto', { title: 'Contacto y ubicación | Rapillantas Huancayo', description: 'Contacto, WhatsApp, dirección y horarios de Rapillantas en Huancayo.', keywords: 'contacto Rapillantas, dirección taller Huancayo, teléfono llantería' }) } },
        { path: 'terminos', component: () => import('../views/public/Terminos.vue'), meta: { seo: { noindex: true } } },
        { path: 'nosotros', component: () => import('../views/public/Nosotros.vue'), meta: { seo: seo('/nosotros', { title: 'Nosotros | Rapillantas Huancayo', description: 'Taller y tienda de neumáticos en Huancayo con años de experiencia. Conoce a Rapillantas.', keywords: 'nosotros Rapillantas, historia taller Huancayo, llantería Huancayo' }) } },
        { path: 'sin-acceso', component: () => import('../views/public/SinAcceso.vue') },
        { path: 'error', component: () => import('../views/public/ErrorServidor.vue') },
        { path: ':pathMatch(.*)*', component: () => import('../views/public/NoEncontrado.vue') },
      ],
    },
    {
      // Panel admin: entra ADMIN o quien tenga el panel (meta.panel por vista)
      path: '/admin', component: AdminLayout, meta: { rol: 'ADMIN', panel: true }, children: [
        { path: '', redirect: '/admin/portada' },
        { path: 'portada', component: () => import('../views/admin/Portada.vue'), meta: { panel: 'portada' } },
        { path: 'ofertas', component: () => import('../views/admin/OfertasAdmin.vue'), meta: { panel: 'portada' } },
        { path: 'valoraciones', component: () => import('../views/admin/ValoracionesAdmin.vue'), meta: { panel: 'valoraciones' } },
        { path: 'empresa', component: () => import('../views/admin/Empresa.vue'), meta: { panel: 'empresa' } },
        { path: 'usuarios', component: () => import('../views/admin/Usuarios.vue'), meta: { panel: 'usuarios' } },
        { path: 'canjes', component: () => import('../views/admin/CanjesAdmin.vue'), meta: { panel: 'canjes' } },
      ],
    },
  ],
})

// Guard por rol + panel: sin token → /login; sin permiso → inicio.
router.beforeEach((to) => {  const rol = localStorage.getItem('rol') ?? ''
  const token = localStorage.getItem('token') ?? ''
  const panels = (localStorage.getItem('paneles') ?? '').split(',').filter(Boolean)
  const puede = (p?: string) => rol === 'ADMIN' || (!!p && panels.includes(p))
  const rec = to.matched.find((r) => r.meta.rol ?? r.meta.panel)
  const need = rec?.meta.rol as string | undefined
  const panel = [...to.matched].reverse().find((r) => r.meta.panel)?.meta.panel as string | undefined
  if (!need && !panel) return true
  if (!token) return '/login'
  const orden: [string, string][] = [['canjes', '/admin/canjes'],
    ['portada', '/admin/portada'],
    ['empresa', '/admin/empresa'], ['usuarios', '/admin/usuarios']]
  // /admin raíz: manda al primer panel permitido en vez de rebotar a sin-acceso.
  if ((to.path === '/admin' || to.path === '/admin/') && (need === 'ADMIN' || panel)) {
    if (rol !== 'ADMIN' && !(panel && puede(panel))) {
      for (const [p, r] of orden) if (puede(p)) return r
      return '/sin-acceso'
    }
    return true
  }
  if (need === 'ADMIN' && rol !== 'ADMIN' && !(panel && puede(panel))) return '/sin-acceso'
  if (need === 'CLIENTE' && !token) return '/login'
  if (panel && !puede(panel)) {
    return '/sin-acceso'
  }
  return true
})

// SEO: cada navegación pública actualiza title/description/keywords/OG/canonical.
router.afterEach((to) => {
  const seo = [...to.matched].reverse().find((r) => (r.meta as any)?.seo)?.meta.seo as SeoMeta | undefined
  if (seo) applySeo(seo)
})
