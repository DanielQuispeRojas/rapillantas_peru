-- V21 — Etiquetas reemplazan tipo_vehiculo; servicios también llevan etiquetas
ALTER TABLE productos DROP COLUMN IF EXISTS tipo_vehiculo;
ALTER TABLE servicios ADD COLUMN IF NOT EXISTS etiquetas TEXT NOT NULL DEFAULT '';
