# Sesión 04 — Arranque verificado, wrapper Maven y dashboard mecánico

**Fecha:** 24/09/2026 (tarde)
**Estado:** Completada — cierre del día

---

## Objetivo
Dejar el sistema corriendo de punta a punta y cerrar decisiones de acceso por rol.

## Decisiones tomadas
1. **Arranque verificado:** Postgres OK (`rapillantas_dev`), V1 regenerado + V2 aplicados (21 tablas), seed de servicios deduplicado a 4, backend responde (`GET /servicios`), frontend `npm run build` OK, `.env` con `VITE_API_URL`. README raíz reescrito con arranque completo; `backend/README` sincronizado.
2. **Maven wrapper:** agregado a `backend/` (`mvnw`, `mvnw.cmd`, `.mvn/`); se corre con `.\mvnw.cmd spring-boot:run`. READMEs actualizados; ruta temporal de Temp ya no se usa.
3. **Sin CSS framework por ahora:** vistas con HTML + estilos mínimos; se probará funcionalidad primero (Tailwind + PrimeVue propuestos para después).
4. **Admin oculto:** quitado el link del nav público; `/admin/*` solo por URL directa hasta login por roles (I2: detectar rol y redirigir).
5. **Dashboard mecánico `/taller` (mobile-first 360px):** Expedientes por placa, Nueva OS, OS abiertas/cerrar. Backend `TallerController` (`GET /taller/vehiculos?placa=`, `GET /taller/vehiculos/{id}/os`, `GET /taller/os-abiertas`). BE y FE compilan (0 errores).
6. **Secretos:** van por variables de entorno en hosting (`SPRING_DATASOURCE_*`, `JWT_SECRET`); V3 de usuarios admin + JWT quedan para I2. Flyway propuesto, pendiente.

## Resultados generados
- `README.md`, `backend/README.md`, `backend/mvnw*`, `.mvn/`, `db/V1__schema.sql` (regenerado), `vue/.env`
- `vue/src/layouts/MecanicoLayout.vue`, `vue/src/views/mecanico/{Expedientes,NuevaOs,OsAbiertas}.vue`, router con `/taller`, `PublicLayout` sin link admin
- `backend/.../workshop/TallerController.java`

## Pendientes (siguiente día)
- V3 usuarios admin + `application.yml` por env vars, JWT por roles y redirect por rol
- `iteraciones/iteracion-1/plan.md`, framework UI final, commit inicial Git
