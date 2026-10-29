-- V31 — Estado del taller (banner de la tienda) + aviso (ej. "sábado sin atención").
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS taller_abierto BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS aviso_taller TEXT NOT NULL DEFAULT '';
