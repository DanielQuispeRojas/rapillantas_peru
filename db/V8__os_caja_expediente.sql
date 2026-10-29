-- V8 — Circuito OS → caja → expediente
-- OS con snapshot cliente + vehículo (la OS envía los detalles para generar la venta),
-- estado 'finalizada' (por cobrar), clientes con cuenta web, pendientes de mantenimiento.

-- 1. OS: cliente inline + snapshot vehículo + vehiculo_id opcional
ALTER TABLE os ADD COLUMN IF NOT EXISTS cliente_id UUID REFERENCES clientes(id);
ALTER TABLE os ADD COLUMN IF NOT EXISTS cliente_nombres TEXT;
ALTER TABLE os ADD COLUMN IF NOT EXISTS cliente_apellidos TEXT;
ALTER TABLE os ADD COLUMN IF NOT EXISTS veh_marca TEXT;
ALTER TABLE os ADD COLUMN IF NOT EXISTS veh_modelo TEXT;
ALTER TABLE os ADD COLUMN IF NOT EXISTS veh_anio INT;
ALTER TABLE os ADD COLUMN IF NOT EXISTS veh_placa TEXT;
ALTER TABLE os ALTER COLUMN vehiculo_id DROP NOT NULL;

-- estado finalizada = taller terminó, pasa a administración para cobrar
ALTER TABLE os DROP CONSTRAINT IF EXISTS os_estado_check;
ALTER TABLE os DROP CONSTRAINT IF EXISTS os_estado_chk;
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_os_estado') THEN
    ALTER TABLE os ADD CONSTRAINT ck_os_estado
      CHECK (estado IN ('abierta','en_proceso','finalizada','cerrada','entregada'));
  END IF;
END $$;

-- 2. Clientes: cuenta web (solo usuarios registrados pueden tener expediente)
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS cuenta TEXT UNIQUE;

-- 3. Pendientes de mantenimiento por vehículo (expediente técnico)
CREATE TABLE IF NOT EXISTS recomendaciones (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  vehiculo_id UUID NOT NULL REFERENCES vehiculos(id) ON DELETE CASCADE,
  os_origen_id UUID REFERENCES os(id) ON DELETE SET NULL,
  descripcion TEXT NOT NULL,
  km_objetivo INT,
  meses INT,
  estado TEXT NOT NULL DEFAULT 'pendiente' CHECK (estado IN ('pendiente','cumplido')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_reco_veh ON recomendaciones(vehiculo_id, estado);
