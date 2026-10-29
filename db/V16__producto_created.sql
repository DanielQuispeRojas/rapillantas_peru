-- V16 — Fecha de creación de productos (novedades del inicio)
ALTER TABLE productos ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT now();
