# Sistema Web Taller Automotriz RAPILLANTAS — Índice Maestro Documental

**Versión:** 1.1 | **Fecha:** 2026-09-24 | **Estado:** BRS/StRS/SRS APROBADOS (provisional PEND-01) → fase DISEÑO
**Normas base:** ISO/IEC/IEEE 29148:2018 (requisitos) · ISO/IEC 12207:2017 (ciclo de vida) · ISO/IEC 25010:2023 (calidad) · ISO/IEC/IEEE 29119 (pruebas) · ISO 31000:2018 (riesgos) · ISO/IEC/IEEE 42010 (arquitectura)
**Fuentes:** `doc/necesidades del cliente.txt` + `doc/DESARROLLO DE SISTEMA WEB PARA TALLER AUTOMOTRIZ RAPILLANTAS.docx` + `doc/Nuevo Documento de texto.md`

## Fuentes consolidadas (21 necesidades unificadas)

| ID Unificado | Origen | Descripción |
|---|---|---|
| NEC-01 | DOCX NEC-001 + TXT NEC-008 | Control de stock mensual + comparativa ingresos vs físico |
| NEC-02 | DOCX NEC-002/003 + TXT NEC-009 | Registrar y consultar adquisiciones (OC) |
| NEC-03 | DOCX NEC-004 + TXT NEC-002 + Encuesta Lima | Margen de ganancia por producto, comparativa costo vs margen |
| NEC-04 | DOCX NEC-005/006 + TXT NEC-009 | Registrar y consultar ventas |
| NEC-05 | DOCX NEC-007 + TXT NEC-003 | Tendencias de ventas + análisis/recomendación qué comprar y promocionar |
| NEC-06 | TXT NEC-001/004 | Dashboard gerencial responsive (móvil + laptop) con NEC-01/03/05 |
| NEC-07 | DOCX NEC-008 + NEC-010 + Mecánico | Expediente técnico por vehículo + Órdenes de Servicio (OS) e historial |
| NEC-08 | DOCX NEC-009 + TXT NEC-005 | Recordatorio mantenimiento cada 10.000 km |
| NEC-09 | TXT NEC-006 | Teléfono de cliente obligatorio para seguimiento |
| NEC-10 | TXT NEC-007 | Fidelización por puntos canjeables (revisión suspensión/frenos, rotación) |
| NEC-11 | TXT NEC-010 | Control mercadería en consignación |
| NEC-12 | TXT NEC-011 | Gestión redes sociales (atención, publicación, pauta) — alcance a definir |
| NEC-13 | DOCX Intro | Catálogo público para Huancayo |
| PEND-01 | TXT | Misión y visión no existen — deben definirse antes de aprobar BRS |

> Conflicto resuelto: DOCX pedía aviso "cada 10000 km" y TXT igual → se unifica en NEC-08. DOCX "ver tendencias" y TXT "análisis qué comprar/promocionar" → NEC-05 como evolutivo de lo primero.

## Paquete documental

| # | Documento | Norma | Archivo |
|---|---|---|---|
| 1 | BRS — Especificación de Requisitos de Negocio | 29148 § Business Requirements | `01-BRS.md` |
| 2 | StRS — Especificación de Requisitos de Interesados | 29148 § Stakeholder Requirements | `02-StRS.md` |
| 3 | SRS — Especificación de Requisitos de Sistema/Software | 29148 § System/Software Requirements | `03-SRS.md` |
| 4 | Matriz de Trazabilidad NEC→REQ→Diseño→Código→Prueba | 29148 § Trazabilidad | `04-Matriz-Trazabilidad.md` |
| 5 | Plan de Gestión de Proyecto y Ciclo de Vida | 12207 | `05-Plan-Gestion-12207.md` |
| 6 | Plan de Calidad y Criterios de Aceptación | 25010 / 25023 | `06-Plan-Calidad-25010.md` |
| 7 | Plan de Pruebas | 29119 | `07-Plan-Pruebas-29119.md` |
| 8 | Registro de Riesgos | 31000 | `08-Registro-Riesgos-31000.md` |
| 9 | Diseño de Arquitectura desacoplada | 42010 | `09-Arquitectura.md` |
| 10 | Modelo de datos PostgreSQL | 12207 diseño | `../db/V1__schema.sql` |
| 11 | API OpenAPI v1 | 12207 diseño | `11-API-OpenAPI.yaml` |
| 12 | Backend Spring Boot (esqueleto) | 12207 impl. | `../backend/` |

## Convenciones usadas en todo el paquete

* Requisito: `ID | Tipo (F/NF) | Prioridad (Alta/Media/Baja) | Fuente | Estado | Versión | Riesgo | Verificación | Trazabilidad NEC`.
* Estados permitidos: PROPUESTO, EN ANALISIS, REVISADO, APROBADO, IMPLEMENTADO, VERIFICADO, VALIDADO, RECHAZADO, CANCELADO, OBSOLETO, MODIFICADO.
* Verificación: Inspección / Revisión por pares / Prueba / Demostración / Análisis.
* Todos inician en `v1.0, PROPUESTO, Riesgo Medio` salvo que se indique lo contrario.

## Próximo paso

1. Definir misión/visión (PEND-01) → 2. Revisar BRS/StRS con Lima + Huancayo → 3. Aprobar SRS → 4. Diseñar e implementar por iteraciones según Plan 12207.
