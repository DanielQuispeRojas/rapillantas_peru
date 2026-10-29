-- RAPILLANTAS V1 — PostgreSQL 15+ — Diseño detallado (traza a SRS RF-01..26)
-- Orden de creación por dependencias. Todo bloque operativo en transacción desde backend (@Transactional).

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Catálogos base
CREATE TABLE IF NOT EXISTS proveedores (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre TEXT NOT NULL, ruc TEXT, telefono TEXT, es_consignador BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS productos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  sku TEXT UNIQUE NOT NULL, nombre TEXT NOT NULL, categoria TEXT NOT NULL DEFAULT 'general',
  costo_promedio NUMERIC(12,2) NOT NULL DEFAULT 0, margen_pct NUMERIC(5,2) NOT NULL DEFAULT 0,
  precio NUMERIC(12,2) NOT NULL DEFAULT 0, stock_actual INT NOT NULL DEFAULT 0,
  es_consignacion BOOLEAN NOT NULL DEFAULT FALSE, activo BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT ck_precio_no_neg CHECK (precio >= 0 AND costo_promedio >= 0)
);
CREATE TABLE IF NOT EXISTS usuarios (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre TEXT NOT NULL, login TEXT UNIQUE NOT NULL, pass_hash TEXT NOT NULL,
  rol TEXT NOT NULL CHECK (rol IN ('ADM_LIMA','SUPERVISOR','ADM_HYO','MECANICO')),
  activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Compras / stock
CREATE TABLE IF NOT EXISTS ocs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  proveedor_id UUID NOT NULL REFERENCES proveedores(id),
  estado TEXT NOT NULL DEFAULT 'borrador' CHECK (estado IN ('borrador','confirmada','recibida','cancelada')),
  fecha DATE NOT NULL DEFAULT CURRENT_DATE, created_by UUID REFERENCES usuarios(id)
);
CREATE TABLE IF NOT EXISTS oc_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), oc_id UUID NOT NULL REFERENCES ocs(id) ON DELETE CASCADE,
  producto_id UUID NOT NULL REFERENCES productos(id), cantidad INT NOT NULL CHECK (cantidad > 0),
  costo_unit NUMERIC(12,2) NOT NULL CHECK (costo_unit >= 0)
);
CREATE TABLE IF NOT EXISTS stock_movimientos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  producto_id UUID NOT NULL REFERENCES productos(id), tipo TEXT NOT NULL
    CHECK (tipo IN ('ingreso','salida','ajuste','merma','recepcion_oc','venta','anulacion')),
  cantidad INT NOT NULL, motivo TEXT, ref_id UUID, created_by UUID REFERENCES usuarios(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS cierres_stock (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), periodo TEXT NOT NULL UNIQUE,
  total_sistema NUMERIC(12,2) NOT NULL, total_fisico NUMERIC(12,2) NOT NULL,
  descuadre NUMERIC(12,2) NOT NULL, validado_por UUID REFERENCES usuarios(id), created_at TIMESTAMPTZ DEFAULT now()
);

-- Clientes / vehículos / taller (RF-20/21/22/23 — teléfono obligatorio)
CREATE TABLE IF NOT EXISTS clientes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nombres TEXT NOT NULL, telefono TEXT NOT NULL CHECK (telefono <> ''),
  consentimiento BOOLEAN NOT NULL DEFAULT TRUE, puntos INT NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS vehiculos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), cliente_id UUID NOT NULL REFERENCES clientes(id),
  placa TEXT UNIQUE NOT NULL, marca TEXT, modelo TEXT, anio INT, km_actual INT NOT NULL DEFAULT 0,
  ultimo_servicio_km INT, ultimo_servicio_fecha DATE
);
CREATE TABLE IF NOT EXISTS os (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), vehiculo_id UUID NOT NULL REFERENCES vehiculos(id),
  diagnostico TEXT, mecanico_id UUID REFERENCES usuarios(id),
  estado TEXT NOT NULL DEFAULT 'abierta' CHECK (estado IN ('abierta','en_proceso','cerrada','entregada')),
  km_atencion INT NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), closed_at TIMESTAMPTZ
);
CREATE TABLE IF NOT EXISTS os_items (id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  os_id UUID NOT NULL REFERENCES os(id) ON DELETE CASCADE,
  producto_id UUID REFERENCES productos(id), descripcion TEXT NOT NULL, cantidad INT NOT NULL DEFAULT 1,
  precio NUMERIC(12,2) NOT NULL DEFAULT 0);
CREATE TABLE IF NOT EXISTS ventas (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), cliente_id UUID REFERENCES clientes(id),
  os_id UUID REFERENCES os(id), total NUMERIC(12,2) NOT NULL DEFAULT 0,
  forma_pago TEXT, anulada BOOLEAN NOT NULL DEFAULT FALSE, created_at TIMESTAMPTZ DEFAULT now()
);
CREATE TABLE IF NOT EXISTS venta_items (id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  venta_id UUID NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
  producto_id UUID NOT NULL REFERENCES productos(id), cantidad INT NOT NULL CHECK (cantidad>0),
  precio_unit NUMERIC(12,2) NOT NULL);

-- Consignación separada RN-01 (RF-10)
CREATE TABLE IF NOT EXISTS consignacion_stock (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), proveedor_id UUID NOT NULL REFERENCES proveedores(id),
  producto_nombre TEXT NOT NULL, cantidad INT NOT NULL DEFAULT 0, costo_ref NUMERIC(12,2) DEFAULT 0
);
CREATE TABLE IF NOT EXISTS consignacion_liquidaciones (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), proveedor_id UUID NOT NULL REFERENCES proveedores(id),
  periodo TEXT NOT NULL, cantidad_vendida INT NOT NULL, monto NUMERIC(12,2) NOT NULL, created_at TIMESTAMPTZ DEFAULT now()
);

-- Fidelización / recordatorios / social / auditoría
CREATE TABLE IF NOT EXISTS puntos_movimientos (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), cliente_id UUID NOT NULL REFERENCES clientes(id),
  tipo TEXT NOT NULL CHECK (tipo IN ('acumulacion','canje')), puntos INT NOT NULL,
  servicio_canje TEXT CHECK (servicio_canje IS NULL OR servicio_canje IN ('revision_suspension','revision_frenos','rotacion')),
  ref_venta UUID REFERENCES ventas(id), created_at TIMESTAMPTZ DEFAULT now()
);
CREATE TABLE IF NOT EXISTS recordatorios (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), vehiculo_id UUID NOT NULL REFERENCES vehiculos(id),
  km_objetivo INT NOT NULL, estado TEXT NOT NULL DEFAULT 'pendiente'
    CHECK (estado IN ('pendiente','contactado','agendado','cerrado')), created_at TIMESTAMPTZ DEFAULT now()
);
CREATE TABLE IF NOT EXISTS social_registros (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), canal TEXT NOT NULL, tipo TEXT NOT NULL
    CHECK (tipo IN ('consulta','publicacion','pauta')), detalle TEXT, gasto NUMERIC(12,2) DEFAULT 0,
  fecha DATE NOT NULL DEFAULT CURRENT_DATE);
CREATE TABLE IF NOT EXISTS aud_log (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), actor UUID REFERENCES usuarios(id),
  accion TEXT NOT NULL, entidad TEXT NOT NULL, entidad_id UUID, detalle JSONB, created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX IF NOT EXISTS ix_mov_prod_fecha ON stock_movimientos(producto_id, created_at);
CREATE INDEX IF NOT EXISTS ix_veh_placa ON vehiculos(placa);
CREATE INDEX IF NOT EXISTS ix_os_veh ON os(vehiculo_id, estado);
CREATE INDEX IF NOT EXISTS ix_ventas_fecha ON ventas(created_at);
