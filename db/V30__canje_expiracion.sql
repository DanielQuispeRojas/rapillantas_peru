-- V30 — Canjes con plazo de recojo (3 días): expiración automática con devolución de puntos.
-- Además se agregan los estados usados por el backend ('rechazado','expirado') que faltaban en el CHECK de V29.
ALTER TABLE canjes ADD COLUMN IF NOT EXISTS expira_at TIMESTAMPTZ;
ALTER TABLE canjes ADD COLUMN IF NOT EXISTS motivo_nota TEXT;
ALTER TABLE canjes DROP CONSTRAINT IF EXISTS canjes_estado_check;
ALTER TABLE canjes ADD CONSTRAINT canjes_estado_check
  CHECK (estado IN ('por_recoger','despachado','cancelado','rechazado','expirado'));
-- Los por_recoger existentes sin expiración vencen 3 días después de su creación.
UPDATE canjes SET expira_at = created_at + INTERVAL '3 days'
  WHERE estado = 'por_recoger' AND expira_at IS NULL;
CREATE INDEX IF NOT EXISTS ix_canjes_expira ON canjes(estado, expira_at);
