-- V7 — OC ítems "nuevo" con descripción + medida (modo existente usa datos del producto)
ALTER TABLE oc_items ADD COLUMN IF NOT EXISTS descripcion TEXT NOT NULL DEFAULT '';
ALTER TABLE oc_items ADD COLUMN IF NOT EXISTS medida TEXT;
