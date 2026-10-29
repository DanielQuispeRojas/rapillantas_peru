-- V29 — Puntos de fidelización: puntos por producto/servicio, snapshot en venta_items,
-- catálogo de canjeables e historial de canjes.
ALTER TABLE productos ADD COLUMN IF NOT EXISTS puntos INT NOT NULL DEFAULT 0;
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS puntos INT NOT NULL DEFAULT 0;
ALTER TABLE venta_items ADD COLUMN IF NOT EXISTS puntos INT NOT NULL DEFAULT 0;
ALTER TABLE venta_items ADD COLUMN IF NOT EXISTS servicio_nombre TEXT;

-- Ítems canjeables con puntos: servicio/producto existente u otro (libre), con imágenes.
CREATE TABLE IF NOT EXISTS canjeables (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre TEXT NOT NULL,
  descripcion TEXT NOT NULL DEFAULT '',
  tipo TEXT NOT NULL DEFAULT 'otro' CHECK (tipo IN ('servicio','producto','otro')),
  servicio_id UUID REFERENCES servicios(id) ON DELETE SET NULL,
  producto_id UUID REFERENCES productos(id) ON DELETE SET NULL,
  puntos_costo INT NOT NULL DEFAULT 0 CHECK (puntos_costo >= 0),
  stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
  imagen_url TEXT, imagen_url2 TEXT, imagen_url3 TEXT,
  publicado BOOLEAN NOT NULL DEFAULT TRUE,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT ck_canjeable_target CHECK (
    (CASE WHEN servicio_id IS NOT NULL THEN 1 ELSE 0 END
     + CASE WHEN producto_id IS NOT NULL THEN 1 ELSE 0 END) <= 1)
);
CREATE INDEX IF NOT EXISTS ix_canjeables_pub ON canjeables(publicado, activo);

-- Canjes del cliente: por_recoger hasta que administración confirma el despacho.
CREATE TABLE IF NOT EXISTS canjes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  cliente_id UUID NOT NULL REFERENCES clientes(id),
  canjeable_id UUID REFERENCES canjeables(id) ON DELETE SET NULL,
  canjeable_nombre TEXT NOT NULL DEFAULT '',
  puntos_usados INT NOT NULL DEFAULT 0,
  estado TEXT NOT NULL DEFAULT 'por_recoger'
    CHECK (estado IN ('por_recoger','despachado','cancelado')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  despachado_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS ix_canjes_cli ON canjes(cliente_id, estado);

-- Los canjes también dejan movimiento de puntos (delta con signo).
ALTER TABLE puntos_movimientos ADD COLUMN IF NOT EXISTS ref_canje UUID REFERENCES canjes(id);
