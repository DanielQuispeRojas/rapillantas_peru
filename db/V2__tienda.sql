-- V2 tienda pública: ofertas + servicios + visibilidad (slice Tienda+Ofertas)
ALTER TABLE productos ADD COLUMN IF NOT EXISTS visible_tienda BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS medida TEXT;      -- ej. 205/55R16 (neumáticos)
ALTER TABLE productos ADD COLUMN IF NOT EXISTS marca TEXT;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS imagen_url TEXT;

CREATE TABLE IF NOT EXISTS servicios (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre TEXT NOT NULL, descripcion TEXT, precio_ref NUMERIC(12,2) NOT NULL DEFAULT 0,
  visible BOOLEAN NOT NULL DEFAULT TRUE, activo BOOLEAN NOT NULL DEFAULT TRUE
);
INSERT INTO servicios (nombre, descripcion, precio_ref) VALUES
  ('Alineamiento computarizado','Alineamiento 3D',0),
  ('Revisión de frenos','Inspección + regulación',0),
  ('Revisión de suspensión','Diagnóstico técnico',0),
  ('Rotación de neumáticos','Rotación + balanceo',0)
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS ofertas (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  producto_id UUID REFERENCES productos(id) ON DELETE CASCADE,
  servicio_id UUID REFERENCES servicios(id) ON DELETE CASCADE,
  titulo TEXT NOT NULL, precio_oferta NUMERIC(12,2),
  vigencia_desde DATE NOT NULL DEFAULT CURRENT_DATE, vigencia_hasta DATE,
  banner TEXT, activa BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT ck_oferta_target CHECK (producto_id IS NOT NULL OR servicio_id IS NOT NULL)
);
CREATE INDEX IF NOT EXISTS ix_ofertas_activas ON ofertas(activa, vigencia_desde, vigencia_hasta);
CREATE INDEX IF NOT EXISTS ix_prod_tienda ON productos(visible_tienda, activo, categoria, marca);
