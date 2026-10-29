# RAPILLANTAS Web — Tienda pública + Dashboard admin

Sistema web para taller automotriz en Huancayo: tienda pública (neumáticos, refacciones, servicios, ofertas por WhatsApp) + dashboard admin (stock, OC, ventas, OS, clientes/vehículos, ofertas). Ver `docs/00-Indice-Maestro.md` (ISO 29148/12207/25010/29119/31000/42010) y `contexto-sesiones/`.

## Stack
| Capa | Tech |
|---|---|
| Frontend | Vue 3 + TS + Router 4 + Pinia + Axios (`vue/`) |
| Backend | Spring Boot 3.2 + Java 21 + JPA/Security/Validation (`backend/`) |
| BD | PostgreSQL 15+ (`db/V1__schema.sql` + `db/V2__tienda.sql`) |

## Estructura
```
backend/  API REST /api/v1 (puerto 8080)
vue/      SPA tienda / + admin /admin (dev puerto 5173, VITE_API_URL)
db/       migraciones V1 (núcleo) + V2 (tienda: ofertas, servicios, visible_tienda)
docs/     BRS/StRS/SRS, trazabilidad, arquitectura, calidad, pruebas, riesgos, OpenAPI
doc/      fuentes encuesta original
contexto-sesiones/  sesiones e iteraciones del proyecto
```

## Requisitos
Java 21, Node 25 + npm, PostgreSQL 18. Maven no hace falta instalar: el repo trae wrapper (`backend/mvnw.cmd`).

## Arranque (verificado 24/09/2026)
### 1. BD (desarrollo: Spring la genera sola — `ddl-auto: update`, sin psql manual)
```powershell
$env:PGPASSWORD='1234'
psql -h localhost -U rapillantas_dev -d postgres -c "CREATE DATABASE rapillantas;"
# listo: al arrancar el backend se crean/actualizan tablas y seeds (4 servicios, almacen, margen 30)
```
> Prod/final: perfil `prod` (`ddl-auto: validate`) + migraciones `db/V1..V8` en orden para
> generar el .sql definitivo. Arranque prod: `.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod`.

### 2. Backend
```powershell
cd backend
.\mvnw.cmd spring-boot:run
# espera: Started RapillantasApiApplication — prueba:
#   http://localhost:8080/api/v1/servicios  (4 servicios seed)
#   http://localhost:8080/api/v1/catalogo   ([] hasta crear productos)
```

### 3. Frontend
```powershell
cd vue
npm install
npm run dev   # http://localhost:5173  (.env ya trae VITE_API_URL=http://localhost:8080/api/v1)
```

## URLs de prueba
Tienda: `/ /neumaticos /refacciones /servicios /ofertas /contacto` · Admin: `/admin /admin/productos /admin/ofertas`.

## Notas
- V2 tiene `INSERT ... ON CONFLICT DO NOTHING` sin constraint único en servicios: si aplicas V2 dos veces se duplican; limpia con `DELETE FROM servicios a USING servicios b WHERE a.id > b.id AND a.nombre = b.nombre;`.
- Seguridad temporal en `permitAll` salvo catálogo; JWT por roles ACT-001..004 va en I2 (`backend/.../config/SecurityConfig.java`).
