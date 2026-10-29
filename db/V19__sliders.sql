-- V19 — Sliders por clave (portada, login, los que se creen): bloque reutilizable
CREATE TABLE IF NOT EXISTS sliders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  clave TEXT UNIQUE NOT NULL,
  nombre TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
INSERT INTO sliders (clave, nombre) VALUES ('portada', 'Portada inicio'), ('login', 'Inicio de sesión')
ON CONFLICT (clave) DO NOTHING;

ALTER TABLE portada_slides ADD COLUMN IF NOT EXISTS slider_id UUID REFERENCES sliders(id) ON DELETE CASCADE;
-- los slides existentes pertenecen a portada
UPDATE portada_slides SET slider_id = (SELECT id FROM sliders WHERE clave = 'portada')
WHERE slider_id IS NULL;
CREATE INDEX IF NOT EXISTS ix_pslides_slider ON portada_slides(slider_id, orden);
