-- V15 — Datos de la empresa editables (los botones de WhatsApp consumen este número)
CREATE TABLE IF NOT EXISTS empresa_config (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  whatsapp TEXT NOT NULL DEFAULT '51999999999',
  direccion TEXT NOT NULL DEFAULT 'Huancayo, Perú',
  correo TEXT NOT NULL DEFAULT 'contacto@rapillantas.pe',
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
INSERT INTO empresa_config (whatsapp, direccion, correo)
SELECT '51999999999', 'Huancayo, Perú', 'contacto@rapillantas.pe'
WHERE NOT EXISTS (SELECT 1 FROM empresa_config);
