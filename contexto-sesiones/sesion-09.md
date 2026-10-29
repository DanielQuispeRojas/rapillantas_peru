# Sesión 09 — Usuarios y auth JWT (4 niveles)

**Fecha:** 24/09/2026
**Estado:** Completada — BE compila, `type-check` Vue OK

---

## Objetivo
Cerrar RNF-02/I2: 4 niveles (VISITANTE anónimo + CLIENTE + MECANICO + ADMIN) con JWT,
registro, guards y perfil privado.

## Decisiones tomadas
1. **Roles finales (V13):** ADMIN (todo), MECANICO (taller, sin caja), CLIENTE (perfil),
   VISITANTE (anónimo, solo tienda pública — no es cuenta).
2. **Backend:** `jjwt 0.12.6`, `JwtService` + `JwtFilter`, `SecurityConfig` por roles
   (stateless), `AuthController` (`/auth/registro` solo CLIENTE + crea ficha con
   `cuenta=login`, `/auth/login`, `/auth/usuarios` CRUD interno solo ADMIN),
   `PerfilController` (`/perfil` privado: vehículos, pendientes, historial, puntos).
   Secreto por `app.jwt-secret` (env en prod). Seed dev: `admin/admin123`.
3. **Frontend:** store Pinia `auth` + interceptor JWT, `Login` (redirige por rol),
   `Registro`, `Perfil`, guards en router (`/admin` ADMIN, `/taller` MECANICO/ADMIN,
   `/perfil` con sesión), nav contextual + salir, vista `Usuarios` (ADMIN crea accesos).
4. `Cliente.cuenta` = login web: el mecánico solo abre expediente a registrados.

## Resultados generados
- `db/V13__roles.sql`, `auth/*` (JwtService, JwtFilter, AuthController), `PerfilController`,
  `SecurityConfig` por roles, `stores/auth`, `Login/Registro/Perfil/Usuarios`, guards
