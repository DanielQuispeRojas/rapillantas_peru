-- V11 — Gastos operativos + reporte mensual (ingresos vs gastos)
CREATE TABLE IF NOT EXISTS gasto_categorias (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre TEXT UNIQUE NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
INSERT INTO gasto_categorias (nombre) VALUES
  ('pasajes'), ('compras'), ('personal'), ('luz'), ('agua'), ('publicidad'), ('otros')
ON CONFLICT (nombre) DO NOTHING;

CREATE TABLE IF NOT EXISTS gastos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  fecha DATE NOT NULL DEFAULT CURRENT_DATE,
  categoria TEXT NOT NULL DEFAULT 'otros',
  descripcion TEXT NOT NULL DEFAULT '',
  monto NUMERIC(12,2) NOT NULL CHECK (monto > 0),
  comprobante TEXT NOT NULL DEFAULT 'ninguno'
    CHECK (comprobante IN ('boleta','factura','ninguno')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_gastos_fecha ON gastos(fecha);
CREATE INDEX IF NOT EXISTS ix_gastos_cat ON gastos(categoria, fecha);
