-- V22 — Permisos por panel: rol COLABORADOR + paneles (dashboard,reporte,catalogos,
-- adquisiciones,consignacion,ventas,gastos,taller,portada,empresa,usuarios)
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS paneles TEXT NOT NULL DEFAULT '';
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS ck_usuarios_rol;
ALTER TABLE usuarios ADD CONSTRAINT ck_usuarios_rol
  CHECK (rol IN ('ADMIN','MECANICO','CLIENTE','COLABORADOR'));
UPDATE usuarios SET paneles = 'taller' WHERE rol = 'MECANICO' AND (paneles IS NULL OR paneles = '');
