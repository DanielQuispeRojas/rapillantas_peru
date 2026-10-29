-- V27 - venta_pagos.venta_id nullable: el OneToMany unidireccional inserta
-- el pago con FK nula y la actualiza despues; con NOT NULL fallaba toda venta.
ALTER TABLE venta_pagos ALTER COLUMN venta_id DROP NOT NULL;
