# Sesión 13 — Mini ERP retirado: MOODU hace taller/almacén, el sitio consume /pub (localhost)

ISO: 12207 desarrollo+verificación (compila + tests + smoke 3 servicios) ·
29148 cada contrato con criterio antes de codificar · 15288 MOODU + sitio +
PG + localhost como un sistema · 25010 compatibilidad (SKU sin FK) +
mantenibilidad (overlay delgado) + seguridad (sin PII en /pub).

## Decidido con el usuario
- Retirar del sitio: workshop (OS/taller/mecánico), purchasing (OC/
  proveedores), inventory operativo (productos/stock/ubicaciones/categorías/
  etiquetas) y consignment (ya descartada).
- El sitio conserva: clientes/auth/perfil, ventas, puntos/canjes, ofertas,
  gastos, empresa/portada, valoraciones + overlay por SKU (imágenes,
  compatibilidad, puntos).
- Servicios: nuevo `GET /pub/core/servicios` en MOODU (whitelist
  nombre/precio) + sitio conserva sus imágenes por SKU.
- Localhost primero; Render en la siguiente fase.

## MOODU (repo principal, sin push: lo hace el usuario manual)
- `PubWebController` + `GET /pub/core/servicios` (solo HABILITADO, lista
  acotada, `?sku/?q/?limite`; whitelist sku/nombre/precioBase/precio/
  etiquetas/categorias/estado, sin dscto interno).
- `WebApiService`: clave `servicios` + ruta + entrada en el catálogo.
- `mercaderiaPublica` + `categoria/dimensiones/unidadMedida` (la tienda
  filtra por categoria y muestra medida).
- `PubWebControllerTest` +1 (4/4). Suite **476/476** verde.

## Backend sitio (compila + context-load verde)
- BORRADOS: `workshop/*` (13), `purchasing/*` (9), `consignment/*` (8),
  inventory operativo (21: Producto, categorías, etiquetas, inventarios,
  stock, ubicaciones, AppConfig), `store/Servicio*` (3),
  `store/TiendaStatsController` (top/novedades/relacionados: van por /pub).
- MIGRADOS a SKU sin FK: `ProductoImagen`/`ProductoCompatibilidad`
  (productoId UUID → sku TEXT) + repos (`findBySku*`); `Oferta`
  (productoId/servicioId → sku/servicioSku); `Canjeable` (+ sku);
  `VentaItem` (productoId → sku).
- NUEVO `loyalty/PuntosSku` (sku PK, puntos) + `PuntosSkuController`
  (`GET/PUT/DELETE /api/v1/admin/puntos-sku`, PANEL_CANJES).
- `VentaService` reescrito: items con sku + snapshot, puntos desde
  `PuntosSku` (fuente de verdad), SIN mover stock (lo mueve MOODU; el
  cuadre se hace en MOODU, a futuro emitir la salida). Fuera `cobrarOs`
  (la OT se cobra en MOODU caja). `anular` ya no restaura stock.
- `TiendaController`: fuera catalogo*/servicios; quedan ofertas CRUD +
  overlay (`/sku/{sku}/imagenes|compatibilidad`,
  `/compatibilidad/buscar`, `/puntos` público).
- `DashboardController`: solo ventas propias (topSku/topServicios,
  proyecciones, estacionalidad, sugerencias). Fuera stock/OS valorizados.
- `PerfilController.mio`: cliente + vehículos (historial por /pub en el
  frontend). `ReporteService`: fuera inversión-OC.
- `DevDataSeeder`: solo sliders, categorías de gasto y admin (nada de
  catálogo/OC/OS/ventas: viven en MOODU).
- `SecurityConfig`: fuera rutas muertas; `/sku/**`,
  `/compatibilidad/**`, `/puntos` públicos; puntos-sku admin con
  PANEL_CANJES.
- Tablas viejas quedan stale en PG dev (create-drop las limpia al
  reiniciar; en prod-validate exigen migración: pendiente fase Render).

## Frontend sitio (build verde; vitest con 1 rojo pre-existente del scaffold)
- NUEVO `lib/moodu.ts` (cliente MOODU por `VITE_MOODU_URL` + adapters al
  shape de la tienda con `id = sku`).
- Rewiring: `Catalogo` (filtra categoria si MOODU la trae, si no no oculta),
  `ProductoDetalle` (ruta `:id` = SKU + fotos overlay + relacionados por
  marca), `Servicios` (sin filtro duración; puntos del overlay),
  `Buscar`, `Cotizador`, `Home` (secciones con primeras; el top real por
  ventas propias va al dashboard), `Perfil` (ficha MOODU por placa),
  `VentasAdmin` (items por SKU, sin por-cobrar), `Dashboard` (claves
  nuevas), `CanjesAdmin` (+ tab Puntos por SKU), `DescuentosAdmin`
  (categorías a texto libre).
- BORRADOS: `views/mecanico/*`, `MecanicoLayout`, admin Productos,
  ServiciosAdmin, Catalogos, Ocs, Consignacion, OsAdmin, OsDetalle,
  InventariosAdmin, UbicacionesAdmin, EtiquetasAdmin; router y menú
  AdminLayout limpios.
- `.env`: `VITE_API_URL=http://localhost:8081/api/v1` (el sitio ya no
  puede ir en :8080: es de MOODU) + `VITE_MOODU_URL=http://localhost:8080`.
- Test `App.spec.ts` en rojo PRE-EXISTENTE (scaffold "You did it!", la App
  real es shell de RouterView; no se tocó).

## Smoke localhost (todo OK, instancias detenidas tras verificar)
- MOODU :8093 (H2 fresca): 7 apis activas, ALM-00001 + SRV-1, whitelist
  exacta en /pub anónimo.
- Sitio :8081 (PG rapillantas): ofertas 200, puntos {}, overlay [], login
  admin OK, `PUT puntos-sku/ALM-00001=10`, venta con sku (total 560,
  snapshot 10 pts), stock MOODU intacto en 12 (desacople probado).
- Rutas viejas (`/catalogo`, `/servicios`, `/os/*`, `/productos`,
  `/ocs`, `/consignacion`) responden igual que cualquier ruta sin
  mapping (403 global pre-existente, sin regresión).
- Vue :5174 con `VITE_MOODU_URL=:8093`: `/`, `/refacciones`,
  `/servicios`, `/buscar`, `/producto/ALM-00001` en 200.

## Cómo correrlo (localhost)
1. MOODU `:8080` (tu backend VSC reiniciado con el código nuevo) con
   licencia sync + las 7 apis web activas (Configuración > Web) + 1
   mercadería y 1 servicio de prueba.
2. Sitio backend `:8081`: `java -jar target/*.jar --server.port=8081`
   (el `--server.port` va DESPUÉS de `-jar`; con espacio en la ruta el
   `-jar` lleva comillas).
3. Vue: `npm run dev` (usa `.env`: api :8081, moodu :8080).

## Pendiente fase Render
Migración PG prod-validate (tablas stale), deploy sitio + CORS del origen,
`VITE_MOODU_URL` a la URL pública, `movilesPermitidos` (cupo móvil
decidido sin implementar), salida DESPACHO automática al vender (hoy el
cuadre es manual en MOODU).

## REGLAS
- El catálogo vive en MOODU: el sitio jamás crea Producto/Servicio/OC/OS
  propios (solo overlay por SKU, sin FK).
- La venta acredita puntos por SKU desde tabla propia; el stock no lo toca
  el sitio (ni al vender ni al anular).
- `/pub` apagada o dato ausente = la tienda muestra vacío con motivo,
  jamás error.

## Adenda 2026-10-10 — Fuera ventas y gastos; usuarios acotado
Pedido directo: MOODU caja ya hace las ventas (fuera sales/* 7 archivos);
gastos fuera aunque MOODU no lo tenga (xpenses/* 7); dashboard fuera
(era 100% ventas/stock/OS). Usuarios: roles ADMIN/COLABORADOR (fuera
MECANICO) y paneles solo canjes,portada,empresa,usuarios,valoraciones;
el admin entra a fotos/promociones. Router /admin redirige a portada;
menu sin Principal/Comercial-Taller; login/Google sin rama taller;
DescuentosAdmin huerfana eliminada. Security sin matchers muertos;
seeder sin gastos. Backend compila + context-load verde; vue build verde.
NOTA: la ceremonia docker de sesion-14 que probaba ventas con SKU queda
obsoleta (ese flujo murio aqui); los puntos-sku siguen como tabla a futuro.
