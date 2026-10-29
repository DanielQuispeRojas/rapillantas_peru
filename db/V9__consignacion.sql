-- V9 — Consignación RF-10 (RN-01: stock separado, excluida del valorizado propio)
-- Ventas de consignación por evento + liquidación por consignador y periodo.
CREATE TABLE IF NOT EXISTS consignacion_ventas (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  proveedor_id UUID NOT NULL REFERENCES proveedores(id),
  producto_nombre TEXT NOT NULL,
  cantidad INT NOT NULL CHECK (cantidad > 0),
  precio_unit NUMERIC(12,2) NOT NULL CHECK (precio_unit >= 0),
  liquidacion_id UUID REFERENCES consignacion_liquidaciones(id) ON DELETE SET NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_cvent_prov ON consignacion_ventas(proveedor_id, liquidacion_id);

ALTER TABLE consignacion_liquidaciones ADD COLUMN IF NOT EXISTS estado TEXT NOT NULL DEFAULT 'pendiente';
ALTER TABLE consignacion_liquidaciones DROP CONSTRAINT IF EXISTS ck_cliq_estado;
ALTER TABLE consignacion_liquidaciones ADD CONSTRAINT ck_cliq_estado
  CHECK (estado IN ('pendiente','pagada'));
