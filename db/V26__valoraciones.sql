-- V26 - Valoraciones de la tienda (una por cliente, máx 3 ediciones/día)
CREATE TABLE IF NOT EXISTS valoraciones (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  cliente_id UUID NOT NULL UNIQUE,
  nombres TEXT NOT NULL DEFAULT '',
  estrellas INT NOT NULL DEFAULT 5 CHECK (estrellas BETWEEN 1 AND 5),
  comentario TEXT NOT NULL DEFAULT '',
  veces_editado_hoy INT NOT NULL DEFAULT 0,
  fecha_ultima_edicion DATE,
  visto_admin BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_valoraciones_updated ON valoraciones(updated_at DESC);
