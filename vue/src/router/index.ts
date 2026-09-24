import { createRouter, createWebHistory } from 'vue-router'
import PublicLayout from '../layouts/PublicLayout.vue'
import AdminLayout from '../layouts/AdminLayout.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/', component: PublicLayout, children: [
        { path: '', component: () => import('../views/public/Home.vue') },
        { path: 'neumaticos', component: () => import('../views/public/Catalogo.vue'), props: { categoria: 'neumatico' } },
        { path: 'refacciones', component: () => import('../views/public/Catalogo.vue'), props: { categoria: 'refaccion' } },
        { path: 'servicios', component: () => import('../views/public/Servicios.vue') },
        { path: 'ofertas', component: () => import('../views/public/Ofertas.vue') },
        { path: 'contacto', component: () => import('../views/public/Contacto.vue') },
      ],
    },
    {
      path: '/admin', component: AdminLayout, children: [
        { path: '', component: () => import('../views/admin/Dashboard.vue') },
        { path: 'productos', component: () => import('../views/admin/Productos.vue') },
        { path: 'ofertas', component: () => import('../views/admin/OfertasAdmin.vue') },
        { path: 'ocs', component: () => import('../views/admin/Dashboard.vue') },
        { path: 'ventas', component: () => import('../views/admin/Dashboard.vue') },
        { path: 'os', component: () => import('../views/admin/Dashboard.vue') },
      ],
    },
  ],
})
