-- V25 - Ofertas tipo promo: descripcion larga + hasta 3 imagenes
ALTER TABLE ofertas ADD COLUMN IF NOT EXISTS descripcion TEXT NOT NULL DEFAULT '';
ALTER TABLE ofertas ADD COLUMN IF NOT EXISTS imagen_url2 TEXT;
ALTER TABLE ofertas ADD COLUMN IF NOT EXISTS imagen_url3 TEXT;
