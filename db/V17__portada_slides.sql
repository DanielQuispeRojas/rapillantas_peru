-- V17 — Portada multi-slide (varias imágenes + descripciones con slider)
CREATE TABLE IF NOT EXISTS portada_slides (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  imagen_url TEXT NOT NULL DEFAULT '/banners/banner-1.jpg',
  titulo TEXT NOT NULL DEFAULT 'Promo de la semana',
  subtitulo TEXT NOT NULL DEFAULT 'Consúltanos por WhatsApp',
  mensaje_wsp TEXT NOT NULL DEFAULT 'Hola, me interesa la oferta',
  orden INT NOT NULL DEFAULT 0,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
-- migra la config única existente como primer slide
INSERT INTO portada_slides (imagen_url, titulo, subtitulo, mensaje_wsp, orden, activo)
SELECT imagen_url, titulo, subtitulo, mensaje_wsp, 0, TRUE FROM portada_config
WHERE NOT EXISTS (SELECT 1 FROM portada_slides);
CREATE INDEX IF NOT EXISTS ix_pslides_orden ON portada_slides(orden);
