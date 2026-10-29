# Sesión 11 — Tienda BMW + portada slider + empresa + comprobantes

**Fecha:** 24/09/2026
**Estado:** Completada — BE compila, type-check/build OK

---

## Objetivo
Vestir la tienda con el nuevo DESIGN (BMW + rojo empresa) y cerrar funcionales de
portada, empresa y comprobantes.

## Decisiones tomadas
1. **Tema BMW adaptado (`theme.css`):** Inter 300 display 60px centrado, radio 0, sin
   bordes/sombras, héroe full-bleed sin overlay de UI, filas limpias, footer banda oscura.
   Rojo `#c8102e` como único acento (logo, activo, dsctos, CTA). Paleta empresa final.
2. **Nav:** grid logo-izq / tabs-centro / usuario-der; buscador icono que expande a la
   izquierda (`/buscar` productos+servicios); login como botón separado; usuario en
   dropdown hover; logo `/rapillantas_logo.png` (44px).
3. **Home:** slider multi-imagen con fundido + título/descripción/botón por slide
   (V17 `portada_slides`, manager en Admin→Portada con orden y visibilidad); panel
   oferta en cuadros; tops por ventas + últimos 10 ingresos (`/tienda/top`, `/tienda/novedades`).
4. **Catálogo/Servicios:** aside izquierdo (búsqueda + marca/modelo/dimensiones por
   facetas) + cards estilo marketplace (imagen + marca/modelo/medida/descripción).
5. **Empresa (`/admin/empresa`, V15):** WhatsApp (lo consumen los botones), dirección
   y correo dinámicos en footer/Contacto. Footer sticky abajo (flex 100vh).
6. **Comprobantes (V12):** `numero_comprobante` en ventas (nueva y cobro OS) y gastos;
   input obligatorio si hay boleta/factura, visible en tablas.
7. **Cuenta:** Mi mantenimiento dentro de Mi perfil (solo registrados); Registro solo
   desde Iniciar sesión; recordatorios 10K dentro del expediente (sin tab propio).

## Resultados generados
- `theme.css` reescrito, 11 vistas públicas, `Portada*`+slides, `Empresa*`, `TiendaStats*`,
  `db/V12, V14, V15, V16, V17`
