# 07 — Plan de Pruebas (ISO/IEC/IEEE 29119-2)
**Versión:** 1.0 | **Estado:** PROPUESTO | Niveles: unitaria (vitest) + integración + sistema + aceptación (playwright en `vue/e2e/`)

## Casos maestros (trazan a Matriz 04)

| TC | Cubre | Escenario / Oráculo |
|---|---|---|
| TC-01 | RF-01/02 | Ingreso 10 → venta 3 → ajuste -1 = saldo 6; cierre físico 6 vs 6 sin descuadre; físico 5 → descuadre -1 valorizado. |
| TC-02 | RF-03 | Comparativa ingresos vs físico por mes cuadra con Excel muestra. |
| TC-03 | RF-04/05 | OC confirmada→recepción parcial sube stock parcial; cancelada no mueve stock. |
| TC-04 | RF-06/07 | Cambio margen recalcula precio y guarda historial; comparativa costo vs margen correcta. |
| TC-05 | RF-08/09 | Venta descuenta; sin stock bloquea (salvo supervisor); anulación revierte. |
| TC-06 | RF-11/12 | Tendencia top-5 correcta; recomendación muestra motivo (cobertura/rotación/margen). |
| TC-07 | RF-21/22 | OS abierta→cerrada bloquea edición; expediente muestra historial completo. |
| TC-08 | RF-23 | Vehículo con +10.000 km desde último servicio aparece en bandeja; marcar contactado/agendado. |
| TC-08b | RF-20 | Crear ficha sin teléfono → rechazado con mensaje; teléfono inválido → rechazado. |
| TC-09 | RF-24 | Acumula puntos; canje válido descuenta; canje fuera de catálogo o sobre tope → rechazado. |
| TC-10 | RNF-01 | Dashboard 360px y laptop: mismos KPIs, sin scroll horizontal crítico. |
| TC-11 | RF-10 | Venta de consignación no afecta valorizado propio; liquidación por consignador cuadra. |
| TC-12 | RF-26 | Registro de consulta/publicación/pauta guarda y lista. |
| TC-13 | RF-25 | Catálogo público visible sin login, oculta sin stock si se configura. |
| TC-SEC | RNF-02/04 | Rol mecánico no ve margen/costo; auditoría registra quién movió stock; corte de red a mitad de venta no deja stock inconsistente. |

**Datos:** copia anonimizada del Excel real. **Salida:** informe por TC: APROBADO/FALLIDO + evidencia → alimenta columna Resultado de Matriz 04. Criterio de salida: 100% TC de prioridad Alta en verde.
