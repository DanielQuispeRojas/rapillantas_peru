# 04 — Matriz de Trazabilidad (ISO/IEC/IEEE 29148)
**Versión:** 1.1 | **Estado:** DISEÑO APROBADO | Cadena: Necesidad → StR → SRS → Diseño → Código → Prueba → Resultado
**Diseño base:** `docs/09-Arquitectura.md` + `db/V1__schema.sql` + `docs/11-API-OpenAPI.yaml` + `backend/`

| NEC | StR | SRS | Diseño (a completar) | Código (a completar) | Prueba (Plan 29119) |
|---|---|---|---|---|---|
| NEC-01 | StR-01, StR-08 | RF-01, RF-02, RF-03 | MOD-INV-01 | `vue/src/modules/inventory/*` | TC-01, TC-02 |
| NEC-02 | StR-09 | RF-04, RF-05 | MOD-COM-01 | `vue/src/modules/purchasing/*` | TC-03 |
| NEC-03 | StR-02 | RF-06, RF-07 | MOD-PRC-01 | `vue/src/modules/pricing/*` | TC-04 |
| NEC-04 | StR-09 | RF-08, RF-09 | MOD-VEN-01 | `vue/src/modules/sales/*` | TC-05 |
| NEC-05 | StR-03 | RF-11, RF-12 | MOD-ANA-01 | `vue/src/modules/analytics/*` | TC-06 |
| NEC-06 | StR-04 | RNF-01 + RF-03/07/11 | MOD-UI-RESP | `vue/src/views/Dashboard*` | TC-10 |
| NEC-07 | StR-12 | RF-20, RF-21, RF-22 | MOD-TLL-01 | `vue/src/modules/workshop/*` | TC-07 |
| NEC-08 | StR-05 | RF-23 | MOD-REC-01 | `vue/src/modules/reminders/*` | TC-08 |
| NEC-09 | StR-06 | RF-20 | MOD-CLI-01 | `vue/src/modules/customers/*` | TC-08b |
| NEC-10 | StR-07 | RF-24 | MOD-LOY-01 | `vue/src/modules/loyalty/*` | TC-09 |
| NEC-11 | StR-10 | RF-10 | MOD-CON-01 | `vue/src/modules/consignment/*` | TC-11 |
| NEC-12 | StR-11 | RF-26 | MOD-SOC-01 (básico) | `vue/src/modules/social/*` | TC-12 |
| NEC-13 | StR-13 | RF-25 | MOD-CAT-01 | `vue/src/views/Catalog*` | TC-13 |

## Reglas de mantenimiento
1. Todo RF debe tener ≥1 TC; todo TC debe apuntar a ≥1 RF (cobertura 100% en TC-01..13).
2. Si cambia una NEC → marcar StR/SRS afectados como MODIFICADO y re-verificar.
3. "Resultado" se llena en ejecución: APROBADO / FALLIDO + evidencia.
4. Huérfanos prohibidos: sin NEC no se implementa; sin prueba no se da por VALIDADO.
