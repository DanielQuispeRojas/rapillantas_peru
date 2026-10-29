-- V13 — Roles finales: ADMIN (todo), MECANICO (taller, sin caja), CLIENTE (perfil privado).
-- VISITANTE es anónimo (sin cuenta): solo tienda pública.
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_rol_check;
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_usuarios_rol') THEN
    ALTER TABLE usuarios ADD CONSTRAINT ck_usuarios_rol
      CHECK (rol IN ('ADMIN','MECANICO','CLIENTE'));
  END IF;
END $$;
-- login en minúsculas para el enlace Cliente.cuenta
UPDATE usuarios SET login = LOWER(login) WHERE login <> LOWER(login);
