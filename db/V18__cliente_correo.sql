-- V18 — Correo del cliente (registro + avisos por email vía n8n a futuro)
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS correo TEXT UNIQUE;
