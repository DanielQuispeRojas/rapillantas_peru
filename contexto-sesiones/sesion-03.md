# Sesión 03 — Requisitos ISO, arquitectura y slice Tienda+Ofertas

**Fecha:** 23-24/09/2026
**Estado:** Completada

---

## Objetivo
Pasar de encuesta a requisitos aprobados y a base técnica funcional (I0 cierre).

## Decisiones tomadas
1. BRS/StRS/SRS v1.0 PROPUESTO → APROBADO (misión/visión provisional: posicionarse en Huancayo, PEND-01 condicional). Evidencia: `docs/00-Indice-Maestro.md` a `docs/08-Registro-Riesgos-31000.md`.
2. Arquitectura desacoplada (ISO 42010): Vue SPA + Spring Boot 3.2 + PostgreSQL. Evidencia: `docs/09-Arquitectura.md`, `docs/11-API-OpenAPI.yaml`.
3. Backend I1 + tienda: productos/stock, OC, ventas, clientes/vehículos, OS, ofertas/servicios, catálogo público. Compila OK (31+ clases). Evidencia: `backend/`, `db/V1__schema.sql`, `db/V2__tienda.sql`.
4. Frontend: Router + Pinia + Axios, layouts Public/Admin y vistas mínimas (Home, Catálogo, Servicios, Ofertas, Contacto, Admin Productos/Ofertas). Type-check OK. Evidencia: `vue/src/`.
5. Alcance aclarado: sitio = tienda pública (neumáticos, refacciones, servicios, ofertas) + dashboard admin.

## Resultados generados
- `docs/` v1.1, `backend/`, `db/V2`, `vue/src/{router,layouts,views,lib}`

## Pendientes
- Definir framework UI final (I1), JWT por roles (I2), `iteraciones/iteracion-1/plan.md` con plantilla, commit inicial Git.
