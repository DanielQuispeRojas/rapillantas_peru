-- V23 - RUC + redes sociales de la empresa (variables dinámicas para la tienda)
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS ruc TEXT NOT NULL DEFAULT '';
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS facebook TEXT NOT NULL DEFAULT '';
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS instagram TEXT NOT NULL DEFAULT '';
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS tiktok TEXT NOT NULL DEFAULT '';
