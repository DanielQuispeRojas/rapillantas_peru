-- V6 — Ubicaciones taller + OC con proveedor razón social/RUC + ventas con comprobante y pagos fraccionados
-- 1. Ubicaciones del taller (almacen = default protegido, se auto-crea si falta)
CREATE TABLE IF NOT EXISTS ubicaciones (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre TEXT UNIQUE NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
INSERT INTO ubicaciones (nombre) VALUES ('almacen') ON CONFLICT (nombre) DO NOTHING;

-- 2. OC: snapshot proveedor (razón social + RUC) en vez de solo UUID manual
ALTER TABLE ocs ADD COLUMN IF NOT EXISTS proveedor_nombre TEXT;
ALTER TABLE ocs ADD COLUMN IF NOT EXISTS proveedor_ruc TEXT;
ALTER TABLE ocs ALTER COLUMN proveedor_id DROP NOT NULL;
ALTER TABLE ocs DROP CONSTRAINT IF EXISTS ocs_proveedor_id_fkey;
ALTER TABLE ocs ADD CONSTRAINT ocs_proveedor_id_fkey FOREIGN KEY (proveedor_id) REFERENCES proveedores(id);
CREATE INDEX IF NOT EXISTS ix_ocs_fecha_estado ON ocs(fecha, estado);
CREATE INDEX IF NOT EXISTS ix_ocs_prov_nombre ON ocs(proveedor_nombre);

-- 3. Clientes: apellidos (la venta pide nombres + apellidos)
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS apellidos TEXT NOT NULL DEFAULT '';

-- 4. Ventas: correlativo + comprobante + snapshot cliente + pagos fraccionados
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS numero_correlativo TEXT;
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS tipo_comprobante TEXT NOT NULL DEFAULT 'ninguno';
ALTER TABLE ventas DROP CONSTRAINT IF EXISTS ck_ventas_comprobante;
ALTER TABLE ventas ADD CONSTRAINT ck_ventas_comprobante
  CHECK (tipo_comprobante IN ('boleta','factura','ninguno'));
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS cliente_nombres TEXT;
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS cliente_apellidos TEXT;

CREATE TABLE IF NOT EXISTS venta_pagos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  venta_id UUID NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
  metodo TEXT NOT NULL CHECK (metodo IN ('efectivo','yape','transferencia','tarjeta')),
  monto NUMERIC(12,2) NOT NULL CHECK (monto > 0)
);
CREATE INDEX IF NOT EXISTS ix_vpagos_venta ON venta_pagos(venta_id);

-- ítems con snapshot producto/servicio (precio final se define en la venta/OS)
ALTER TABLE venta_items ADD COLUMN IF NOT EXISTS descripcion TEXT NOT NULL DEFAULT '';
ALTER TABLE venta_items ADD COLUMN IF NOT EXISTS origen TEXT NOT NULL DEFAULT 'producto';
ALTER TABLE venta_items DROP CONSTRAINT IF EXISTS ck_vitems_origen;
ALTER TABLE venta_items ADD CONSTRAINT ck_vitems_origen
  CHECK (origen IN ('producto','servicio','libre'));
ALTER TABLE venta_items ALTER COLUMN producto_id DROP NOT NULL;
