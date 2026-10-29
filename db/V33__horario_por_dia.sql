-- V33 — Horario por día (no todos los días tienen horario fijo, ej. sábado hasta la 1pm).
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS horario_lun TEXT NOT NULL DEFAULT '8:00–18:00';
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS horario_mar TEXT NOT NULL DEFAULT '8:00–18:00';
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS horario_mie TEXT NOT NULL DEFAULT '8:00–18:00';
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS horario_jue TEXT NOT NULL DEFAULT '8:00–18:00';
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS horario_vie TEXT NOT NULL DEFAULT '8:00–18:00';
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS horario_sab TEXT NOT NULL DEFAULT '8:00–13:00';
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS horario_dom TEXT NOT NULL DEFAULT 'cerrado';
ALTER TABLE empresa_config DROP COLUMN IF EXISTS horario;
