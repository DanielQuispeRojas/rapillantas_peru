# Modelo de Ciclo de Vida Seleccionado

## Nombre del Modelo
**Iterativo-Incremental**

## Referencia Normativa
- **ISO/IEC/IEEE 12207:2017** — Sección 6.4.3: Desarrollo iterativo e incremental
- **ISO/IEC/IEEE 15288:2023** — Procesos técnicos de definición, desarrollo y verificación
- **ISO/IEC/IEEE 29148:2018** — Ingeniería de requisitos (estructura por incremento)
- **ISO/IEC/IEEE 15289:2019** — Contenido de planes del ciclo de vida

---

## Justificación de la Selección

| Factor del Proyecto | Implicación | Modelo Iterativo-Incremental |
|---------------------|-------------|------------------------------|
| Solo desarrollador | Sin overhead de coordinación | Iteraciones autónomas y ligeras |
| Requisitos no definidos completamente | No se puede planificar todo de una | Descubrimiento por iteración |
| Entregas semanales obligatorias | Cada semana debe haber avance funcional | Cada iteración = 1 incremento |
| Proyecto real con evidencia académica | Cada incremento genera documentación | Artefactos por iteración |
| Duración: 8 semanas | Marco temporal corto | 8 iteraciones de 1 semana |

> Nota 24/09/2026: el alcance evolucionó a Tienda pública + Admin (ver `docs/09-Arquitectura.md`). La tabla de incrementos de contexto-general se mantiene como base académica; el plan operativo vigente está en `docs/05-Plan-Gestion-12207.md` (I0-I3).

---

## Ciclo de Vida — Estructura

```
┌──────────────────────────────────────────────────────────┐
│                    CICLO DE VIDA                         │
│                                                          │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐             │
│  │ITERACIÓN 0│   │ITERACIÓN 1│   │ITERACIÓN 2│   ...     │
│  │Inicializa-│   │Incremento│   │Incremento│             │
│  │  ción     │   │    1     │   │    2     │             │
│  └────┬─────┘   └────┬─────┘   └────┬─────┘             │
│       │              │              │                    │
│       ▼              ▼              ▼                    │
│  ┌──────────────────────────────────────────┐            │
│  │         FASE POR CADA ITERACIÓN          │            │
│  │                                          │            │
│  │  1. Planificación    (requisitos incr.)  │            │
│  │  2. Diseño           (arquitectura)      │            │
│  │  3. Implementación   (código funcional)  │            │
│  │  4. Verificación     (tests unitarios/   │            │
│  │                       e2e)               │            │
│  │  5. Documentación    (evidencia ISO)     │            │
│  │  6. Entrega          (avance al cliente) │            │
│  └──────────────────────────────────────────┘            │
│                                                          │
│  ┌──────────┐                                            │
│  │ITERACIÓN 8│                                           │
│  │  Cierre  │                                            │
│  └──────────┘                                            │
└──────────────────────────────────────────────────────────┘
```

---

## Desglose de Incrementos

| Iter. | Semana | Incremento | Funcionalidad | Evidencia Documental |
|-------|--------|------------|---------------|----------------------|
| **I0** | Pre | Inicialización | Stack, arquitectura, plan de proyecto | Plan de proyecto (ISO 29148 §5) |
| **I1** | 1 | Página principal + Catálogo de servicios | Layout, navegación, homepage, sección servicios | SRS v1, SDD v1, Plan I1 |
| **I2** | 2 | Sistema de stock | Visualización de stock disponible, búsqueda, filtros | SRS v2, SDD v2, Plan I2 |
| **I3** | 3 | Sección de información | Información de la empresa, ubicación, contacto, FAQ | SRS v3, SDD v3, Plan I3 |
| **I4** | 4 | Gestión de clientes | Registro, login, perfil de cliente | SRS v4, SDD v4, Plan I4 |
| **I5** | 5 | Sistema de fidelización | Historial de clientes, puntos/beneficios, recomm. | SRS v5, SDD v5, Plan I5 |
| **I6** | 6 | Publicación de fliers/videos | Gestión de contenido multimedia, galería | SRS v6, SDD v6, Plan I6 |
| **I7** | 7 | Panel administrativo | CRUD de stock, servicios, clientes, contenido | SRS v7, SDD v7, Plan I7 |
| **I8** | 8 | Cierre e integración | Pruebas finales, pulido, documentación consolidada | SRS final, SDD final, STD completo |

---

## Artefactos Documentales por Iteración

Cada iteración genera los siguientes artefactos (ISO 29148 / 15289):

### Obligatorios por semana
- **Plan de iteración** — Objetivos, tareas, criterios de aceptación
- **Registro de requisitos** — Requisitos funcionales y no funcionales del incremento
- **Registro de diseño** — Decisiones de diseño del incremento
- **Registro de pruebas** — Casos de prueba y resultados
- **Bitácora de cambios** — Decisiones tomadas y justificación

### Consolidados al final
- **SRS completo** (Software Requirements Specification)
- **SDD completo** (Software Design Description)
- **STD completo** (Software Test Documentation)
- **Informe final de proyecto**

---

## Flujo de Trabajo Semanal

```
Lunes        Martes       Miércoles    Jueves       Viernes
─────────    ─────────    ─────────    ─────────    ─────────
Planificar   Diseñar      Implementar  Verificar    Documentar
requisitos   módulo       código       con tests    y entregar
del incr.    del incr.    funcional    (Vitest/     avance
                                      Playwright)
```

---

*Documento generado: 09/09/2026*
*Última actualización: 09/09/2026*
