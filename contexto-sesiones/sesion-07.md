# Sesión 07 — Descuentos programados + recordatorios 10K por expediente

**Fecha:** 24/09/2026
**Estado:** Completada — BE compila, `type-check` Vue OK

---

## Objetivo
Cerrar RF-08 (descuentos) y RF-23 (recordatorios 10K) según reglas del negocio.

## Decisiones tomadas
1. **Descuentos (`Catálogos → Descuentos`):** catálogo (productos|servicios), % , inicio
   (>= hoy, nunca menor), fin (>= inicio), categoría (default `todos`). Se permiten varios,
   pero **no dos en la misma categoría de un catálogo con fechas cruzadas** (validado en
   `DescuentoService`). Endpoints `CRUD /descuentos + /vigentes + /{id}/desactivar`.
2. **Precio dscto:** dinámico (no almacenado): `precio × (1 - %)` del vigente para la
   categoría exacta, si no el `todos`. Visible en tabla productos, servicios, catálogo
   público (tachado), y precargado editable en Nueva OS y Nueva Venta.
3. **Categorías de producto gestionadas (`/categorias`):** `generico` default protegido
   (no se modifica ni elimina); el CHECK cerrado de V4 se retiró y las existentes se
   migraron como categorías. Se administran en el panel de ubicaciones de Productos.
4. **Recordatorios 10K solo con expediente (`/taller/recordatorios`):** al abrir expediente
   se pide **km inicial opcional** (`vehiculos.km_inicial`); NULL = sin medidor → se obvia.
   Al finalizar OS se evalúa: si `kmAtencion - kmInicial >= 10000` genera aviso pendiente
   (sin duplicados). Bandeja con vehículo/cliente/teléfono + **Avisar WhatsApp** y estados
   contactado/agendado/cerrado.

## Resultados generados
- `db/V10__descuentos_10k.sql`, `Descuento*`, `CategoriaController`, `Recordatorio*`,
  `kmInicial`, hook en `TallerService.finalizar`, `DescuentosAdmin`, tab en `Catálogos`
