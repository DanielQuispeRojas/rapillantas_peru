# Sesión 08 — Gastos operativos + reporte mensual

**Fecha:** 24/09/2026
**Estado:** Completada — BE compila, `type-check` Vue OK

---

## Objetivo
Que la administradora registre gastos y vea la macro mensual (gastado vs ganado)
con historial, para decidir.

## Decisiones tomadas
1. **Gastos (`/admin/gastos`):** fecha, categoría (pasajes, compras, personal, luz, agua,
   publicidad, otros +personalizables, `otros` protegido), descripción, monto > 0,
   comprobante. Lista del mes con total + navegación anual y filtro mes.
2. **Reporte (`/admin/reporte`):** KPIs del mes (ganado/ventas no anuladas, gastado,
   utilidad, margen), tabla por categoría, barras 12 meses ganado vs gastado + tabla
   mensual. Endpoints `GET /reporte/mensual` y `/reporte/anual`.
3. Nota honesta: utilidad = ventas − gastos (no descuenta costo de mercadería; el
   valorizado de stock ya se ve en Dashboard).

## Resultados generados
- `db/V11__gastos.sql`, `backend/.../expenses/*`, `Gastos.vue`, `Reporte.vue`, rutas + enlaces
