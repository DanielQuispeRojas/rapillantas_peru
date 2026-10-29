-- V14 — Portada de la tienda configurable (flyer + título + mensaje WhatsApp)
CREATE TABLE IF NOT EXISTS portada_config (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  imagen_url TEXT NOT NULL DEFAULT '/banners/banner-1.jpg',
  titulo TEXT NOT NULL DEFAULT 'Promo de la semana',
  subtitulo TEXT NOT NULL DEFAULT 'Consúltanos por WhatsApp',
  mensaje_wsp TEXT NOT NULL DEFAULT 'Hola, me interesa la oferta',
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
INSERT INTO portada_config (imagen_url, titulo, subtitulo, mensaje_wsp)
SELECT '/banners/banner-1.jpg', 'Promo de la semana', 'Consúltanos por WhatsApp', 'Hola, me interesa la oferta'
WHERE NOT EXISTS (SELECT 1 FROM portada_config);
