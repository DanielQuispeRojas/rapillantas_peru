# Contexto General del Proyecto

## Nombre del Proyecto
**RAPILLANTAS**

---

## Normativas Base / Sustento

El proyecto RAPILLANTAS se sustenta en las siguientes normativas ISO:

| Norma | Título | Versión | Estado |
|-------|--------|---------|--------|
| **ISO/IEC/IEEE 12207** | Sistemas e ingeniería de software — Procesos del ciclo de vida del software | 2017 | Vigente |
| **ISO/IEC/IEEE 15288** | Sistemas e ingeniería de sistemas — Procesos del ciclo de vida del sistema | 2023 | Vigente |
| **ISO/IEC/IEEE 15289** | Sistemas e ingeniería de software — Contenido de los planes de ciclo de vida | 2019 | Vigente |
| **ISO/IEC/IEEE 29148** | Ingeniería de sistemas y software — Ingeniería de requisitos | 2018 | Vigente |

---

## Resumen de Aplicación

- **ISO 12207 (2017):** Define los procesos del ciclo de vida del software, incluyendo actividades y tareas desde la adquisición hasta el mantenimiento. Se aplica como marco para la definición de procesos de ingeniería de software del proyecto.

- **ISO 15288 (2023):** Define los procesos del ciclo de vida del sistema, proporcionando un marco de referencia para los procesos de ingeniería de sistemas. Establece una perspectiva de sistema que integra hardware, software y datos.

- **ISO 29148 (2018):** Establece la ingeniería de requisitos (BRS/StRS/SRS) y la trazabilidad. Se aplica en `docs/01-BRS, 02-StRS, 03-SRS, 04-Matriz`.

- **ISO 15289 (2019):** Define el contenido de los planes del ciclo de vida del sistema, del software y del servicio. Establece qué información debe contener cada plan documentado en las diferentes fases del proyecto.

---

## Modelo de Ciclo de Vida

**Selección:** Iterativo-Incremental

**Referencia:** ISO/IEC/IEEE 12207:2017 §6.4.3

**Justificación:**
- Solo desarrollador → proceso ligero
- Requisitos que evolucionan → descubrimiento por iteración
- Entregas semanales obligatorias → 1 incremento por semana
- Evidencia académica → documentación por cada iteración
- Duración: 8 semanas → 8 iteraciones

**Detalle completo:** [modelo-ciclo-vida.md](modelo-ciclo-vida.md)

### Resumen de Iteraciones

| Iter. | Semana | Incremento | Estado |
|-------|--------|------------|--------|
| I0 | Pre | Inicialización | Completada |
| I1 | 1 | Homepage + Catálogo servicios | Pendiente |
| I2 | 2 | Sistema de stock | Pendiente |
| I3 | 3 | Sección información | Pendiente |
| I4 | 4 | Gestión de clientes | Pendiente |
| I5 | 5 | Sistema de fidelización | Pendiente |
| I6 | 6 | Publicación fliers/videos | Pendiente |
| I7 | 7 | Panel administrativo | Pendiente |
| I8 | 8 | Cierre e integración | Pendiente |

---

## Registro de Sesiones

| Sesión | Fecha | Descripción | Archivo |
|--------|-------|-------------|---------|
| S01 | 09/09/2026 | Creación de contexto, establecimiento de normativas base | `sesion-01.md` |
| S02 | 09/09/2026 | Selección de modelo de ciclo de vida, definición de incrementos | `sesion-02.md` |
| S03 | 23-24/09/2026 | Requisitos ISO aprobados, arquitectura desacoplada, backend I1 + slice tienda | `sesion-03.md` |
| S04 | 24/09/2026 | Arranque verificado, wrapper Maven, admin oculto y dashboard mecánico | `sesion-04.md` |
| S05 | 24/09/2026 | Operativa completa: dashboard+proyección, OC dual, taller→caja, stock+inventario, expedientes | `sesion-05.md` |
| S06 | 24/09/2026 | Adquisiciones (renombre) + consignación RF-10 + auditoría RF-01→26 | `sesion-06.md` |
| S07 | 24/09/2026 | Descuentos programados + categorías + recordatorios 10K por expediente | `sesion-07.md` |
| S08 | 24/09/2026 | Gastos operativos + reporte mensual ganado-vs-gastado | `sesion-08.md` |
| S09 | 24/09/2026 | Usuarios y auth JWT (VISITANTE/CLIENTE/MECANICO/ADMIN) + perfil privado | `sesion-09.md` |
| S10 | 24/09/2026 | Tema Vibrants aplicado a la tienda pública | `sesion-10.md` |
| S11 | 24/09/2026 | Tema BMW+rojo, slider portada, empresa, comprobantes, catálogo marketplace | `sesion-11.md` |
| S12 | 24/09/2026 | ERP admin/taller, permisos por panel, tienda final y fixes | `sesion-12.md` |

---

*Última actualización: 24/09/2026 — S12 cierre del día (todo verificado; siguiente: RF-24/RF-26).
