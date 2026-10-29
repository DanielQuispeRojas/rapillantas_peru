-- V28 — Número correlativo visible de OS (formato OS-00000000001)
ALTER TABLE os ADD COLUMN IF NOT EXISTS numero TEXT;
-- Rellena las OS históricas por orden de creación
WITH n AS (
  SELECT id, ROW_NUMBER() OVER (ORDER BY created_at, id) AS rn
  FROM os WHERE numero IS NULL
)
UPDATE os SET numero = 'OS-' || LPAD(n.rn::TEXT, 11, '0') FROM n WHERE os.id = n.id;
ALTER TABLE os DROP CONSTRAINT IF EXISTS uq_os_numero;
ALTER TABLE os ADD CONSTRAINT uq_os_numero UNIQUE (numero);
