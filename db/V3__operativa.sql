-- RAPILLANTAS V3 — Operativa: OC completa, productos vistosos, compatibilidad vehículo, ofertas flyer
-- Trazas: NEC-02 (OC), NEC-01/03/05 (dashboard/proyección vía ventas+stock), NEC-13 (catálogo), NEC-07 (OS por mes vía índices)

-- 1. Productos: modelo + compatibilidad + multi-imagen
ALTER TABLE productos ADD COLUMN IF NOT EXISTS modelo TEXT;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS cantidad INT NOT NULL DEFAULT 0;
-- stock_actual es la cantidad operativa; cantidad queda como alias para compatibilidad con UI (sincronizar por trigger)
-- costo_promedio = costo adquisición promedio (RN margen), precio = precio venta (RN-02)
-- sku ya UNIQUE; la generación automática va en backend (InventarioService)

CREATE TABLE IF NOT EXISTS producto_imagenes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  producto_id UUID NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
  url TEXT NOT NULL,
  orden INT NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_pimg_prod ON producto_imagenes(producto_id, orden);

-- Compatibilidad vehículo: qué marca/modelo recomienda cada producto (filtrable en catálogo)
CREATE TABLE IF NOT EXISTS producto_compatibilidad (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  producto_id UUID NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
  marca TEXT NOT NULL,           -- ej. Toyota
  modelo TEXT NOT NULL,          -- ej. Hilux
  anio_desde INT,
  anio_hasta INT,
  nota TEXT
);
CREATE INDEX IF NOT EXISTS ix_pcomp_prod ON producto_compatibilidad(producto_id);
CREATE INDEX IF NOT EXISTS ix_pcomp_marca_modelo ON producto_compatibilidad(marca, modelo);

-- Sincroniza cantidad <-> stock_actual (la UI usa "cantidad", el motor usa "stock_actual")
CREATE OR REPLACE FUNCTION sync_producto_cantidad() RETURNS TRIGGER AS $$
BEGIN
  IF TG_OP = 'INSERT' THEN NEW.cantidad := NEW.stock_actual; RETURN NEW; END IF;
  IF NEW.stock_actual IS DISTINCT FROM OLD.stock_actual AND NEW.cantidad = OLD.cantidad THEN
    NEW.cantidad := NEW.stock_actual;
  ELSIF NEW.cantidad IS DISTINCT FROM OLD.cantidad AND NEW.stock_actual = OLD.stock_actual THEN
    NEW.stock_actual := NEW.cantidad;
  ELSE NEW.cantidad := NEW.stock_actual; END IF;
  RETURN NEW;
END; $$ LANGUAGE plpgsql;
DROP TRIGGER IF EXISTS trg_sync_producto_cantidad ON productos;
CREATE TRIGGER trg_sync_producto_cantidad BEFORE INSERT OR UPDATE ON productos
  FOR EACH ROW EXECUTE FUNCTION sync_producto_cantidad();

-- 2. OC completa: llegada estimada, recepción, transporte, IGV, otros costos
ALTER TABLE ocs ADD COLUMN IF NOT EXISTS fecha_llegada_estimada DATE;
ALTER TABLE ocs ADD COLUMN IF NOT EXISTS fecha_recepcion DATE;
ALTER TABLE ocs ADD COLUMN IF NOT EXISTS costo_transporte NUMERIC(12,2) NOT NULL DEFAULT 0;
ALTER TABLE ocs ADD COLUMN IF NOT EXISTS igv NUMERIC(12,2) NOT NULL DEFAULT 0;
ALTER TABLE ocs ADD COLUMN IF NOT EXISTS observacion TEXT;
ALTER TABLE ocs ALTER COLUMN estado SET DEFAULT 'programada';
-- estados válidos: programada (=borrador+confirmada unificados), recibida, cancelada
-- (se conserva compatibilidad con 'borrador','confirmada' ya existentes)

-- items: permiten producto libre (nombre/marca/modelo) y registro de lo recibido + adicionales
ALTER TABLE oc_items ADD COLUMN IF NOT EXISTS producto_id UUID;
ALTER TABLE oc_items DROP CONSTRAINT IF EXISTS oc_items_producto_id_fkey;
ALTER TABLE oc_items ADD CONSTRAINT oc_items_producto_id_fkey FOREIGN KEY (producto_id) REFERENCES productos(id);
ALTER TABLE oc_items ADD COLUMN IF NOT EXISTS nombre TEXT;
ALTER TABLE oc_items ADD COLUMN IF NOT EXISTS marca TEXT;
ALTER TABLE oc_items ADD COLUMN IF NOT EXISTS modelo TEXT;
ALTER TABLE oc_items ADD COLUMN IF NOT EXISTS cantidad_recibida INT NOT NULL DEFAULT 0;
ALTER TABLE oc_items ADD COLUMN IF NOT EXISTS es_adicional BOOLEAN NOT NULL DEFAULT FALSE;

-- otros costos de la OC (flete extra, comisión, almacenaje, etc.)
CREATE TABLE IF NOT EXISTS oc_costos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  oc_id UUID NOT NULL REFERENCES ocs(id) ON DELETE CASCADE,
  concepto TEXT NOT NULL,
  monto NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (monto >= 0)
);
CREATE INDEX IF NOT EXISTS ix_oc_costos_oc ON oc_costos(oc_id);

-- 3. Ofertas flyer para carrusel del dashboard tienda
ALTER TABLE ofertas ADD COLUMN IF NOT EXISTS imagen_url TEXT;
ALTER TABLE ofertas ADD COLUMN IF NOT EXISTS orden INT NOT NULL DEFAULT 0;
-- banner (legacy) ~= imagen_url; la UI admin usa imagen_url, el endpoint público devuelve ambas

-- 4. Índices para filtros por mes (OC del mes, OS del mes, ventas para proyección)
CREATE INDEX IF NOT EXISTS ix_ocs_fecha ON ocs(fecha);
CREATE INDEX IF NOT EXISTS ix_os_created ON os(created_at);
CREATE INDEX IF NOT EXISTS ix_ventas_created ON ventas(created_at);
