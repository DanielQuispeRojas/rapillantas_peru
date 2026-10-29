-- V10 — Descuentos programados + categorías producto + km inicial para 10K
-- 1. Categorías de producto (generico = default protegido)
CREATE TABLE IF NOT EXISTS producto_categorias (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre TEXT UNIQUE NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
INSERT INTO producto_categorias (nombre)
SELECT DISTINCT categoria FROM productos WHERE categoria IS NOT NULL
ON CONFLICT (nombre) DO NOTHING;
INSERT INTO producto_categorias (nombre) VALUES ('generico')
ON CONFLICT (nombre) DO NOTHING;
-- las categorías ahora son libres (gestionadas); se retira el CHECK cerrado
ALTER TABLE productos DROP CONSTRAINT IF EXISTS ck_productos_categoria;
ALTER TABLE productos ALTER COLUMN categoria SET DEFAULT 'generico';
UPDATE productos SET categoria = 'generico' WHERE categoria IS NULL OR categoria = '';

-- 2. Descuentos programados por catálogo + categoría (sin solape en la misma)
CREATE TABLE IF NOT EXISTS descuentos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  catalogo TEXT NOT NULL CHECK (catalogo IN ('productos','servicios')),
  pct NUMERIC(5,2) NOT NULL CHECK (pct > 0 AND pct < 100),
  inicio DATE NOT NULL,
  fin DATE NOT NULL CHECK (fin >= inicio),
  categoria TEXT NOT NULL DEFAULT 'todos',
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_dscto_cat ON descuentos(catalogo, categoria, inicio, fin, activo);

-- 3. Km inicial del expediente: base del control 10K (NULL = sin medidor → se obvia)
ALTER TABLE vehiculos ADD COLUMN IF NOT EXISTS km_inicial INT;
