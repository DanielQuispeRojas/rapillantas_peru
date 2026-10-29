# Sesión 05 — Operativa completa: dashboard, OC, taller→caja, stock y expedientes

**Fecha:** 24/09/2026
**Estado:** Completada — BE compila, `type-check` Vue OK (0 errores)

---

## Objetivo
Convertir el esqueleto I1 en la operativa real pedida por el negocio: dashboard gerencial,
compras con recepción, taller que deriva a caja, stock con inventario físico y
expedientes técnicos por vehículo.

## Decisiones tomadas
1. **Dashboard gerencial (`/admin`):** KPIs + ventas 12 meses en barras + **proyección = media
   móvil 3 meses** (tendencia ALCISTA/BAJISTA/ESTABLE con recomendación), top 8 productos
   (uds + ingreso + margen + stock) y sugerencias de qué potenciar.
   `GET /dashboard/resumen` (`backend/.../dashboard/DashboardController.java`).
2. **Compras OC (`/admin/ocs`):** pendientes siempre visibles + historial anual con
   desplazamiento ◀ año ▶ y filtro mes (no se cargan todas). Crear tras botón **Crear OC**,
   proveedor por **razón social + RUC** con autocompletado **top 10 últimos 12 meses**,
   resumen de costos en vivo. Ítems en 2 modos: **1. Existente** (selector de stock con
   autocompletado de datos + costo) y **2. Nuevo** (nombre, descripción, marca, modelo,
   medida, cantidad, costo). Recepción valida pedido vs llegado + adicionales; eliminar
   solo programadas. Endpoints: `/ocs/pendientes`, `/ocs/historial`, `/ocs/proveedores-sugeridos`.
3. **Productos/Stock separados de Servicios:** `V4` saca `servicio` de productos (CHECK solo
   bienes físicos). Servicios = mano de obra (nombre + duración aprox + **precio aprox**;
   el final se fija en la OS). **OS y ventas guardan screenshot sin FK**: si el catálogo
   cambia, el historial no se altera.
4. **Productos/Stock (`/admin/productos`):** SKU automático, descripción, marca/modelo/medida,
   cantidad, ubicación (selector desde **Ubicaciones del taller**; `almacen` default protegido
   y auto-creado), costo adquisición, margen %, precio final auto, tipo vehículo
   (`auto/camioneta/moto/camion/otro/no_aplica`) + compatibilidad fina (marca/modelo/año).
   **Fotos:** máx 10 por producto, desde **dispositivo (móvil/PC)** con upload multipart
   (`POST /productos/{id}/imagenes/upload`, servidas en `/uploads/**`) y/o URL; botón
   **Galería** (modal flotante) para quitar/reordenar (portada = primera).
   **Margen global** configurable + **Aplicar a todos**. **Hacer inventario**: sesión con
   conteo +/− por ubicación, cierre con **Por resolver** (faltante/sobrante) que se concilia
   con venta/salida/ingreso (todo a Kardex). Botón **Ver Kardex** por producto.
5. **Ventas (`/admin/ventas`):** historial año+mes con desplazamiento anual y filtro mes.
   Ítems con radio **Producto/Servicio**, todo **snapshot** (correlativo auto al confirmar,
   sin digitar). **Pago fraccionado** (efectivo/yape/transferencia/tarjeta + repartir total,
   suma debe cuadrar). Sección **Por cobrar** (OS finalizadas) → **Cobrar** precarga el
   detalle de la OS y `POST /ventas/desde-os/{osId}` genera la venta y cierra la OS.
6. **Circuito taller→caja:** OS con cliente (nombres+apellidos) y vehículo (marca, modelo,
   año, placa) inline o desde expediente. Taller **Finaliza** (estado `finalizada`, no genera
   ni cobra); administración cobra. `POST /os/{id}/finalizar`, `GET /os/finalizadas`.
7. **Expedientes técnicos (por vehículo):** solo para clientes **registrados con cuenta web**
   (`clientes.cuenta`); mecánico busca usuario → **Abrir expediente** (registra vehículo).
   **Crear OS** desde expediente autocompleta datos y muestra **pendientes flotantes**
   (clic → al detalle; los no cumplidos persisten). Mecánico configura recomendados
   **en X km o X meses** (`recomendaciones`); el cliente los ve en **Mi mantenimiento**
   (`/mi-mantenimiento`, lookup por placa).
8. **Ofertas admin retirada:** a pedido del negocio, se eliminó `admin/ofertas` (ruta, enlace
   y vista). Carrusel del Home ahora **manual**: archivos en `vue/public/banners/` + lista
   en `Home.vue` (ver `LEEME.txt`). Endpoints `/ofertas` del backend quedan sin uso admin.

## Resultados generados
- `db/V3__operativa.sql` → `db/V8__os_caja_expediente.sql` (6 migraciones; aplicar en orden)
- Backend ~25 clases nuevas (inventory, purchasing, sales, workshop, dashboard, store, config)
- Frontend: `Dashboard`, `Ocs`, `OsAdmin`, `Productos`, `ServiciosAdmin`, `VentasAdmin`,
  `NuevaOs`, `OsAbiertas`, `Expedientes`, `MiMantenimiento`, `Home` (manual), `Catalogo`
  (filtro vehículo + `imgSrc`), `lib/api.imgSrc`

## Pendientes (siguiente)
- JWT por roles + redirect por rol (I2) y login web de clientes (perfil privado por cuenta)
- `uploads/` a disco persistente o S3 al desplegar (`app.uploads-dir`)
- Retirar endpoints `/ofertas` si se confirma que no se usarán
- `iteraciones/iteracion-1/plan.md`, framework UI final, commit inicial Git
