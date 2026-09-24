# Sesión 02 — Selección de Modelo de Ciclo de Vida

**Fecha:** 09/09/2026
**Estado:** Completada

---

## Objetivo
Definir el modelo de ciclo de vida del proyecto y establecer el plan de iteraciones.

---

## Decisiones Tomadas

### 1. Modelo de Ciclo de Vida
Se selecciona el modelo **Iterativo-Incremental** por:
- Proyecto nuevo con requisitos en evolución
- Solo desarrollador (proceso ligero)
- Entregas semanales obligatorias (8 semanas)
- Necesidad de evidencia documental por iteración
- Alineado con ISO/IEC/IEEE 12207:2017 §6.4.3

### 2. Plan de Iteraciones
Se definen 9 iteraciones (I0-I8):
- **I0:** Inicialización (pre-producción)
- **I1-I7:** Desarrollo incremental (1 semana c/u)
- **I8:** Cierre e integración

### 3. Artefactos por Iteración
Cada semana genera:
- Plan de iteración
- SRS iterativo
- SDD iterativo
- Registro de pruebas
- Bitácora de cambios

### 4. Flujo de Trabajo Semanal
```
Lunes      Martes     Miércoles   Jueves     Viernes
Planificar  Diseñar    Implementar Verificar  Documentar
```

---

## Resultados Generados
- `contexto-sesiones/modelo-ciclo-vida.md` — Documento del modelo seleccionado
- `contexto-sesiones/plantilla-iteracion.md` — Plantilla para cada iteración
- `contexto-sesiones/iteraciones/iteracion-0/plan.md` — Plan de inicialización
- `contexto-sesiones/contexto-general.md` — Actualizado con modelo de ciclo de vida

---

## Pendientes para Iteración 0
- Definir backend
- Definir base de datos
- Definir framework UI
- Definir sistema de autenticación
- Configurar proyecto base

---

*Última actualización: 09/09/2026*
