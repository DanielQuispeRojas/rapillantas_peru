# Iteración 0 — Inicialización

**Semana:** Pre-producción
**Fecha inicio:** 09/09/2026
**Fecha fin:** 14/09/2026 (Lunes 15 inicia Iteración 1)
**Estado:** Completada (24/09/2026)

---

## 1. Objetivo de la Iteración
Establecer las bases técnicas, arquitectónicas y documentales del proyecto antes de comenzar el desarrollo incremental.

---

## 2. Alcance General del Proyecto

### 2.1 Descripción
Sistema web para la empresa RAPILLANTAS (llantería) que permita:
- **Visualización de stock** de llantas disponibles
- **Catálogo de servicios** ofrecidos por la empresa
- **Publicación de fliers y videos** para atraer clientes
- **Información de la empresa** (contacto, ubicación, etc.)
- **Gestión de clientes** con sistema de fidelización
- **Panel administrativo** para gestión del contenido

### 2.2 Público Objetivo
- Clientes potenciales (visitantes del sitio)
- Clientes existentes (sistema de fidelización)
- Administradores de la empresa (gestión del sistema)

---

## 3. Stack Tecnológico

| Capa | Tecnología | Justificación |
|------|------------|---------------|
| Framework | Vue 3 (Composition API) | Setup existente, reactivo, ligero |
| Lenguaje | TypeScript | Type safety, mantenibilidad |
| Bundler | Vite 8 | Velocidad de desarrollo, HMR |
| Testing unitario | Vitest | Nativo con Vite, rápido |
| Testing e2e | Playwright | Multi-browser, confiable |
| Linting | ESLint + OxLint | Calidad de código |
| Formateo | Prettier | Consistencia de estilo |

### 3.1 Tecnologías definidas (24/09/2026)
| Capa | Selección | Evidencia |
|------|-----------|-----------|
| Router | Vue Router 4 | `vue/src/router/index.ts` |
| Estado global | Pinia | `vue/src/main.ts` |
| HTTP | Axios (`VITE_API_URL`) | `vue/src/lib/api.ts` |
| Backend/API | Spring Boot 3.2 (Java 21) desacoplado | `backend/` compila OK (31 clases) |
| Base de datos | PostgreSQL 15+ (V1 esquema + V2 tienda) | `db/V1__schema.sql`, `db/V2__tienda.sql` |
| Autenticación | Spring Security (JWT por roles ACT-001..004 en I2; permitAll temporal) | `backend/.../config/SecurityConfig.java` |
| UI | Sin framework pesado en I0 (layouts propios Public/Admin); Tailwind/Vuetify a evaluar en I1 | `vue/src/layouts/` |

---

## 4. Arquitectura de Alto Nivel

```
┌─────────────────────────────────────────────┐
│              RAPILLANTAS WEB                │
├─────────────────────────────────────────────┤
│                                             │
│  ┌─────────────┐  ┌─────────────────────┐  │
│  │   FRONTEND  │  │      BACKEND        │  │
│  │ Vue 3 + TS  │◄►│ Spring Boot 3.2     │  │
│  │ Router/Pinia│  │ REST /api/v1 + JPA  │  │
│  └─────────────┘  └─────────────────────┘  │
│         │                   │               │
│         ▼                   ▼               │
│  ┌─────────────┐  ┌─────────────────────┐  │
│  │   RUTAS     │  │   BASE DE DATOS     │  │
│  │ pública+admin│  │ PostgreSQL V1+V2    │  │
│  └─────────────┘  └─────────────────────┘  │
│                                             │
└─────────────────────────────────────────────┘
```
Ver detalle en `docs/09-Arquitectura.md` y `docs/11-API-OpenAPI.yaml`.

---

## 5. Estructura de Directorios (Propuesta)

```
rapillantas-web/               # raíz del proyecto
├── backend/                   # Spring Boot 3.2 (API /api/v1)
├── vue/src/                   # SPA (layouts Public/Admin, views public/admin, router, lib/api.ts)
├── db/                        # V1__schema.sql + V2__tienda.sql
├── docs/                      # BRS/StRS/SRS, trazabilidad, arquitectura, API
├── doc/                       # fuentes encuesta (necesidades, docx)
└── contexto-sesiones/         # gestión de sesiones (este doc) + iteraciones/
```
Nota: la propuesta anterior con `pages/composables` se reemplazó por `views/public+admin, layouts, router, lib` (ver `vue/src/`).

---

## 6. Herramientas de Desarrollo

| Herramienta | Propósito |
|-------------|-----------|
| VS Code + Volar | IDE con soporte Vue/TS |
| Git | Control de versiones |
| npm | Gestión de paquetes |
| ESLint + OxLint | Linting |
| Prettier | Formateo |
| Vitest | Tests unitarios |
| Playwright | Tests e2e |

---

## 7. Plan de Iteraciones (Resumen)

| Iter. | Semana | Incremento | Fecha |
|-------|--------|------------|-------|
| I0 | Pre | Inicialización | 09/09 - 14/09 |
| I1 | 1 | Homepage + Catálogo servicios | 15/09 - 21/09 |
| I2 | 2 | Sistema de stock | 22/09 - 28/09 |
| I3 | 3 | Sección información | 29/09 - 05/10 |
| I4 | 4 | Gestión de clientes | 06/10 - 12/10 |
| I5 | 5 | Sistema de fidelización | 13/10 - 19/10 |
| I6 | 6 | Publicación fliers/videos | 20/10 - 26/10 |
| I7 | 7 | Panel administrativo | 27/10 - 02/11 |
| I8 | 8 | Cierre e integración | 03/11 - 09/11 |

---

## 8. Pendientes → Completados (24/09/2026)

- [x] Definir backend → Spring Boot 3.2 desacoplado (`backend/`, BUILD SUCCESS)
- [x] Definir base de datos → PostgreSQL V1+V2 (`db/`)
- [x] Definir autenticación → Spring Security (JWT por roles en I2)
- [x] Configurar Vue Router → `vue/src/router` (rutas pública + admin)
- [x] Configurar Pinia + Axios → instalados (`main.ts`, `lib/api.ts`)
- [x] Configurar estructura de carpetas → layouts/views por tienda y admin
- [ ] Definir framework UI (Tailwind/Vuetify/PrimeVue) → pasa a I1
- [ ] Primer commit con la base → pendiente de Git

---

## 9. Bitácora de Cambios
| Fecha | Cambio | Justificación |
|-------|--------|---------------|
| 09/09/2026 | Creación del documento de inicialización | Inicio del proyecto |
| 24/09/2026 | Cierre I0: stack definido, arquitectura 42010, backend I1 + slice tienda | Ver docs/09, db/V2, sesion-03 |

---

*Generado: 09/09/2026*
