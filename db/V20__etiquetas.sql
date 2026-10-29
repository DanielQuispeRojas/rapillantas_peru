-- V20 — Etiquetas libres por producto (reemplazan compatibilidad fina)
ALTER TABLE productos ADD COLUMN IF NOT EXISTS etiquetas TEXT NOT NULL DEFAULT '';
