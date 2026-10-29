# Sesión 12 — ERP admin/taller, permisos por panel y tienda final

**Fecha:** 24/09/2026
**Estado:** Completada — BE clean-compile, type-check/build OK

---

## Objetivo
Vestir admin/taller como ERP moderno, permisos granulares por panel y cerrar la tienda.

## Decisiones tomadas
1. **ERP (`erp.css` + shells):** sidebar grafito por secciones, topbar, tablas/tabs/modales
   estándar, FABs, taller móvil con pills. Sin negros puros (#26262b) ni letras rojas
   en contenido (tinta + rojo solo hover).
2. **Permisos por panel (V22):** rol COLABORADOR + `paneles` en JWT (`PANEL_*`); backend
   exige panel por endpoint; frontend filtra nav y redirige al primer panel permitido.
   MECANICO trae `taller` y lectura de catálogos para armar OS.
3. **Catálogos:** tabs Productos/Servicios/Descuentos/Ubicaciones/Etiquetas; registro y
   Kardex en modales; edición de ficha (stock genera ajuste); inventario y Por resolver
   en modales con FABs; etiquetas reemplazan tipoVehiculo y compatibilidad (V20/V21);
   tab Etiquetas global (renombrar/quitar en bloque) y Ubicaciones (conteo, propaga,
   `almacen` protegido).
4. **Tienda BMW+rojo:** nav grid (logo/tabs/user dropdown hover/buscador expandible),
   slider multi-imagen por clave (`useSlider`), tops y novedades en cards marketplace,
   detalle `/producto/:id` con galería + recomendados, `/buscar` global, footer sticky,
   bordes redondeados, Barlow→Inter, logo 32px, fondo sin textura.
5. **Portada/Empresa:** slides por clave con upload a `vue/public/portada/`; empresa
   editable (WhatsApp/dirección/correo) consumida por botones; comprobantes con número
   (V12); gastos+reporte (V11); consignación (V9); descuentos+categorías+10K (V10);
   auth JWT+CORS+roles (V13); sliders (V17/V19); correo cliente (V18).
6. **Fixes:** CORS 5173, SKU en seed, `Map` import, registro (nombres/apellidos/correo/
   teléfono), Mi mantenimiento dentro de Perfil, Google OAuth pendiente de CLIENT_ID.

## Pendientes
- RF-24 puntos / RF-26 redes, `GOOGLE_CLIENT_ID`, `uploads/` persistente en prod,
  retirar `/ofertas` si se confirma, commit inicial Git.
