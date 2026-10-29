-- V32 — Horario de atención (se muestra al pasar el mouse sobre el estado de la tienda).
ALTER TABLE empresa_config ADD COLUMN IF NOT EXISTS horario TEXT NOT NULL DEFAULT 'Lun–Sáb 8:00–18:00';
