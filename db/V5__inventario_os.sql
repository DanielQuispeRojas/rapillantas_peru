-- V5 — Productos con descripción/tipo vehículo/ubicación + margen global + inventario físico + OS snapshot
-- 1. Productos: campos que pide la vista Productos/Stock
ALTER TABLE productos ADD COLUMN IF NOT EXISTS descripcion TEXT;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS tipo_vehiculo TEXT NOT NULL DEFAULT 'no_aplica';
ALTER TABLE productos DROP CONSTRAINT IF EXISTS ck_productos_tipo_vehiculo;
ALTER TABLE productos ADD CONSTRAINT ck_productos_tipo_vehiculo
  CHECK (tipo_vehiculo IN ('auto','camioneta','moto','camion','otro','no_aplica'));
ALTER TABLE productos ADD COLUMN IF NOT EXISTS ubicacion TEXT NOT NULL DEFAULT 'almacen';

-- 2. Config global (margen de ganancia por defecto para productos nuevos)
CREATE TABLE IF NOT EXISTS app_config (
  clave TEXT PRIMARY KEY,
  valor TEXT NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
INSERT INTO app_config (clave, valor) VALUES ('margen_global_pct', '30')
ON CONFLICT (clave) DO NOTHING;

-- 3. Inventario físico: sesión + conteos por producto/ubicación
CREATE TABLE IF NOT EXISTS inventarios (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  fecha DATE NOT NULL DEFAULT CURRENT_DATE,
  estado TEXT NOT NULL DEFAULT 'abierto' CHECK (estado IN ('abierto','cerrado')),
  observacion TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS inventario_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  inventario_id UUID NOT NULL REFERENCES inventarios(id) ON DELETE CASCADE,
  producto_id UUID NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
  ubicacion TEXT NOT NULL DEFAULT 'almacen',
  cant_sistema INT NOT NULL DEFAULT 0,
  cant_contada INT NOT NULL DEFAULT 0,
  diferencia INT NOT NULL DEFAULT 0,
  estado TEXT NOT NULL DEFAULT 'pendiente'
    CHECK (estado IN ('pendiente','ok','faltante','sobrante','resuelto')),
  resolucion_tipo TEXT CHECK (resolucion_tipo IS NULL OR resolucion_tipo IN ('venta','salida','ajuste','ingreso')),
  resolucion_motivo TEXT,
  UNIQUE (inventario_id, producto_id, ubicacion)
);
CREATE INDEX IF NOT EXISTS ix_inv_items_inv ON inventario_items(inventario_id, estado);
CREATE INDEX IF NOT EXISTS ix_inv_items_prod ON inventario_items(producto_id);

-- 4. OS: snapshot sin FK directo a servicios (screenshot de nombre+precio al momento de la OS)
-- os_items.producto_id ya es nullable (insumo opcional); se agregan columnas snapshot:
ALTER TABLE os_items ADD COLUMN IF NOT EXISTS servicio_nombre TEXT;
ALTER TABLE os_items ADD COLUMN IF NOT EXISTS origen TEXT NOT NULL DEFAULT 'libre';
ALTER TABLE os_items DROP CONSTRAINT IF EXISTS ck_os_items_origen;
ALTER TABLE os_items ADD CONSTRAINT ck_os_items_origen
  CHECK (origen IN ('producto','servicio','libre'));
