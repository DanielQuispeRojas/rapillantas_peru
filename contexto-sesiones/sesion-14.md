# Sesión 14 — Simulación del despliegue con Docker (previa a Render)

ISO: 12207 transición/operación · 29148 ceremonia verificable · 15288
MOODU + sitio + Postgres + host simulado como un sistema · 25010
portabilidad (mismo JAR en 3 destinos, solo cambia el entorno).

## Piezas nuevas
- `backend/Dockerfile` (espejo del de MOODU: Maven → JRE 21, perfil prod).
- `docker-compose.sim.yml`: `pg-moodu` + `pg-rapi` (Postgres 16) +
  `moodu-core` (build `../backend/core`, prod) + `rapillantas-api`
  (prod + `SPRING_JPA_HIBERNATE_DDL_AUTO=update` SOLO para simular: las
  migraciones `db/` aún no cubren el retiro del mini ERP; en el deploy
  real va `validate` + migración V35, pendiente fase Render).
- Puertos host: MOODU :8090 (el :8080 lo ocupa el backend MOODU de VSC,
  no se toca), sitio :8082 (el :8081 lo ocupa el backend propio del sitio,
  no se toca), PG moodu :5433, PG sitio :5434.
  El vue en dev usa overrides: VITE_MOODU_URL=http://localhost:8090 y
  VITE_API_URL=http://localhost:8082/api/v1 (el .env apunta a :8081/:8080
  para el flujo sin docker).
- Secretos con default SOLO-simulation (`JWT_SECRET`, `MOODU_SETUP_TOKEN`,
  `APP_JWT_SECRET`); en host real van generados y el setup-token se rota.

## Requisito previo
Docker Desktop encendido (el daemon estaba apagado al crear esto:
`docker compose ...` daba `dockerDesktopLinuxEngine` no encontrado).
Verificar: `docker info` OK.

## Ceremonia (una sola vez, volúmenes frescos) — VERIFICADA 2026-10-10
```powershell
cd "C:\Users\User\Desktop\Moodu\Rapillantas web"
docker compose -f docker-compose.sim.yml up --build -d
docker compose -f docker-compose.sim.yml logs -f moodu-core rapillantas-api
```
NOTAS de la corrida real: hubo que hacer `docker pull postgres:16-alpine`
a mano (el primer `up` murió con `No such image`); el compose quedó en
`:8090/:8082` porque el usuario ya ocupa `:8080` (MOODU VSC) y `:8081`
(sitio propio).

1. **MOODU** (`:8090`): `GET /api/setup/status` (`configured:false`) →
   `POST /api/setup/admin` con `X-Setup-Token` → login → `POST
   /sistema/licencia/guardar` con los 4 módulos (+ asignación de almacén
   PRODUCTOS a `dueno`, si no el crear-mercadería da 400) → activar las 7
   apis en `PUT /sistema/web/apis/{clave}` → crear 1 mercadería + 1
   servicio. Todo verificado en vivo (ALM-00001 + SRV-1, /pub anónimo con
   whitelist exacta).
2. **Sitio** (`:8082`): `POST /api/v1/auth/registro` (pide nombres,
   apellidos, correo, teléfono, login, clave) + `UPDATE users SET
   rol='ADMIN'` por psql (`:5434`) + login fresco con rol ADMIN;
   `PUT /api/v1/admin/puntos-sku/{SKU}` (tabla a futuro; las ventas ya no
   existen en el sitio: las hace MOODU caja).
3. **Vue** (`:5173` con overrides): home y `/refacciones` listan MOODU.

## Apagar / limpiar
```powershell
docker compose -f docker-compose.sim.yml down      # conserva datos
docker compose -f docker-compose.sim.yml down -v   # borra las BDs
```

## Pendiente fase Render (no simular, hacer)
Migración V35 del sitio (columnas SKU + tablas retiradas), deploy de
ambos servicios + CORS del dominio real, `VITE_MOODU_URL` pública,
`movilesPermitidos`, salida DESPACHO automática al vender.

## REGLAS
- En simulación vale `update`; en host real `validate` + migraciones.
- Secretos de simulación jamás salen a un host real.
- La ceremonia se hace una sola vez por volúmenes frescos (con `-v` se
  repite desde cero).
