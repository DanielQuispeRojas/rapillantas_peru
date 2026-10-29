# Sesión 06 — Adquisiciones, consignación y auditoría RF

**Fecha:** 24/09/2026
**Estado:** Completada — BE compila, `type-check` Vue OK

---

## Objetivo
Renombrar el módulo a su nombre de negocio y cerrar la brecha RF-10 (consignación),
tras auditar RF-01→RF-26 contra lo implementado.

## Decisiones tomadas
1. **Adquisiciones:** ruta `/admin/adquisiciones` (redirect desde `/admin/ocs`), título y
   sidebar renombrados. Sin cambio funcional.
2. **Consignación RF-10/RN-01 (`/admin/consignacion`):** proveedores con flag consignador
   (`CRUD /proveedores`), stock separado por consignador (ingreso), ventas que descuentan
   SOLO consignación (no tocan stock propio ni valorizado) y quedan pendientes, liquidación
   por periodo (cantidad + monto a pagar) con estado pendiente/pagada. `V9` + paquete
   `backend/.../consignment/*` + `ProveedorController`.
3. **Auditoría RF pendiente (para siguientes sesiones):**
   - RF-23 recordatorios 10k (bandeja vencidos/próximos, contactado/agendado) — grande
   - RF-24 puntos + RF-26 redes — fase 3
   - Parciales: descuento en ventas (RF-08), teléfono obligatorio burlado con `000000000`
     (RF-20), comparativa ingresos-vs-físico + costo-vs-margen dedicada (RF-03/07),
     recepción parcial OC, historial de cambios de margen (RF-06), auth por roles (RNF-02)

## Resultados generados
- `db/V9__consignacion.sql`, `backend/.../consignment/*`, `Proveedor*`,
  `vue/src/views/admin/Consignacion.vue`, ruta + enlace
