-- V4 — Separa catálogo de servicios del stock de productos
-- Productos = bienes físicos (stock, Kardex, SKU, imágenes, compatibilidad).
-- Servicios = mano de obra (sin stock ni Kardex, con duración y precio ref).

-- 1. Servicios: duración + costo de referencia (para margen del servicio)
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS duracion_min INT NOT NULL DEFAULT 30;
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS costo_ref NUMERIC(12,2) NOT NULL DEFAULT 0;

-- 2. Reubica lo mezclado: todo producto con categoria='servicio' pasa a 'general'
-- (los servicios viven en su propia tabla, no consumen stock ni Kardex)
UPDATE productos SET categoria = 'general' WHERE categoria = 'servicio';

-- 3. A partir de aquí productos solo acepta bienes físicos
ALTER TABLE productos DROP CONSTRAINT IF EXISTS ck_productos_categoria;
ALTER TABLE productos ADD CONSTRAINT ck_productos_categoria
  CHECK (categoria IN ('neumatico','refaccion','general','consignacion'));

CREATE INDEX IF NOT EXISTS ix_servicios_visibles ON servicios(activo, visible);
