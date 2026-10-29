-- V12 — Número/código de comprobante en ventas y gastos
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS numero_comprobante TEXT;
ALTER TABLE gastos ADD COLUMN IF NOT EXISTS numero_comprobante TEXT;
