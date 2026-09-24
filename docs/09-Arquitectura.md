# 09 — Diseño de Arquitectura (ISO/IEC/IEEE 42010 + 12207 proceso de Diseño)
**Versión:** 1.0 | **Estado:** PROPUESTO | **Decisión base:** Arquitectura desacoplada Vue + Spring Boot + PostgreSQL — APROBADA como dirección

## 1. Decisión y por qué es válida
Tu propuesta es correcta para este caso y cumple RNF-01/02/04/05:
* Frontend Vue ya existe en `vue/` → se conserva como SPA, no se reescribe.
* Backend Spring Boot desacopla reglas pesadas (stock atómico, costo/margen, consignación, puntos con topes, recordatorios 10k km) del UI.
* PostgreSQL da integridad transaccional (exigida por RNF-04) y reportes analíticos (comparativas, tendencias) mejor que Excel/MySQL básico.
* Costo: exige saber Java + despliegue JVM; para equipo de 1-2 devs sigue viable. Alternativa Node/Nest sería más liviana, pero Spring Boot es superior en auditoría, seguridad por roles y transacciones.

## 2. Vista de contenedores (C4 nivel 2)
```
[Vue SPA] --HTTPS/JSON--> [API Spring Boot] --> [PostgreSQL]
   |                           |--Spring Security (JWT, roles ACT-001..004)
   +--PWA responsive (móvil+laptop)  |--Scheduler recordatorios 10k km
                                     |--Módulo reportes/dashboard
[Visitante catálogo] --> [Vue pública / misma SPA ruta /catalogo] --> [API pública read-only]
```

## 3. Módulos backend ↔ SRS
* `inventory` (RF-01/02/03) · `purchasing-oc` (RF-04/05) · `pricing-margin` (RF-06/07) · `sales` (RF-08/09) · `consignment` (RF-10) · `analytics` (RF-11/12) · `customers-vehicles` (RF-20) · `workshop-os` (RF-21/22) · `reminders` (RF-23 + scheduler) · `loyalty` (RF-24) · `catalog` (RF-25) · `social-basic` (RF-26) · `auth-audit` (RNF-02/04).
* Frontend espejo en `vue/src/modules/*` según Matriz 04.

## 4. API REST (v1, prefijo /api/v1)
* `POST /auth/login` (JWT) · `GET /dashboard/resumen?periodo=` (RF-03/07/11) · `GET/POST /stock/movimientos, POST /stock/cierre` · `CRUD /ocs` + `POST /ocs/{id}/recepcion` · `CRUD /ventas` + `POST /ventas/{id}/anular` · `PUT /productos/{id}/margen` · `CRUD /clientes, /vehiculos (placa única, teléfono obligatorio)` · `CRUD /os` + `POST /os/{id}/cerrar` · `GET /recordatorios/vencidos` + `POST /{id}/contactado` · `GET /puntos/{cliente} POST /puntos/canje` · `GET /catalogo` (público) · `CRUD /consignacion + POST /liquidar`.
* Reglas: paginación, validación Bean Validation (teléfono obligatorio → 400 si falta), auditoría en tablas `aud_*`, transacciones `@Transactional` en venta/OS/stock.

## 5. Base de datos PostgreSQL (entidades núcleo)
`proveedores, productos(costo_promedio, margen, precio), stock_movimientos, stock_saldos, ocs, oc_items, ventas, venta_items, consignaciones, clientes(telefono NOT NULL, consentimiento), vehiculos(placa UNIQUE, km_actual), os, os_items, puntos_movimientos, recordatorios, usuarios_roles, aud_log`.
* Consignación en tablas separadas, excluida de valorizado propio (RN-01). Índices en placa, fecha, producto. Backup diario (RNF-04).

## 6. Seguridad y NFR
* JWT + roles ACT-001..004, hash bcrypt, teléfono solo para roles autorizados, CORS restringido al dominio SPA, HTTPS obligatorio. Cumple RNF-02/07.
* Rendimiento RNF-03: índices + agregados materializados para dashboard (<5s), paginación <3s con 10k filas.
* Despliegue v1.0: 1 VPS: `frontend (Nginx) + backend jar (systemd/Docker) + postgres`. Fase 2: Docker Compose separado.

## 7. Qué sigue (para cerrar Diseño)
1. DDL inicial + script migración Excel. 2. OpenAPI del §4. 3. Maquetas Dashboard/Cierre/OS. Con eso pasas a I1 (12207 Implementación).
