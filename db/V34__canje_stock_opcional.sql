-- V34 — Canjeables simplificados: sin tipo, stock opcional (tope de oferta).
-- NULL = ilimitado hasta despublicar (ej. aromatizantes por cientos).
ALTER TABLE canjeables ALTER COLUMN stock DROP NOT NULL;
