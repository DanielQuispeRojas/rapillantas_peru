# 01 — BRS: Especificación de Requisitos de Negocio (Business Requirements Specification)
**Norma:** ISO/IEC/IEEE 29148:2018 | **Versión:** 1.1 | **Estado:** APROBADO | **Fecha:** 2026-09-24

## 0. Misión y Visión provisionales (PEND-01 cerrado condicionalmente)
> La empresa no cuenta con misión/visión formal. Por decisión del cliente se aprueba seguir con versiones provisionales basadas en el único lineamiento dado: **posicionarse en la ciudad de Huancayo**. Deberán ratificarse en I0.
* **Misión (provisional):** Brindar en Huancayo venta de repuestos y servicios automotrices preventivos/correctivos confiables, con seguimiento por vehículo y atención cercana por WhatsApp/teléfono.
* **Visión (provisional):** Ser el taller de referencia en Huancayo por confianza, historial técnico por cliente y recordatorios oportunos de mantenimiento.

## 1. Propósito y alcance
Dotar a Rapillantas (Lima + Huancayo, 4 personas) de un sistema web que elimine el control en Excel disperso, dé trazabilidad stock↔compras↔ventas, expediente técnico por vehículo y recordatorios de mantenimiento, con dashboard gerencial móvil/laptop y catálogo público para Huancayo.

## 2. Problema (situación actual según encuesta)
* Stock, compras, ventas, inventario y consignación se llevan en Excel; la comparación físico vs ingresos y costo vs margen es manual y tediosa.
* No hay historial de clientes ni órdenes de trabajo (todo "a memoria").
* Sin misión/visión formal. Comunicación con clientes solo por WhatsApp/llamadas. Redes sociales sin gestión centralizada.
* Se trabaja todo presencial en taller; inventario mensual.

## 3. Oportunidad
* Recordatorios cada 10.000 km + expediente técnico + programa de puntos (canje: revisión suspensión/frenos, rotación) como valor agregado sin pérdida económica.
* Dashboard con tendencias y recomendación qué comprar / qué promocionar (marketing).
* Catálogo público para captar demanda en Huancayo.

## 4. Objetivos de negocio (medibles)

| ID | Objetivo | Meta | Fuente |
|---|---|---|---|
| OBJ-01 | Reducir tiempo de cierre mensual de stock | -50% vs Excel en 3 meses | NEC-01 |
| OBJ-02 | Trazabilidad costo→margen por producto | 100% productos con margen asignado | NEC-03 |
| OBJ-03 | Aumentar retorno por recordatorios | +15% servicios recurrentes en 6 meses | NEC-08 |
| OBJ-04 | Decisión gerencial móvil | Dashboard disponible en teléfono + laptop | NEC-06 |
| OBJ-05 | Fidelización sin pérdida | Puntos solo canjeables por servicios de bajo costo marginal | NEC-10 |

## 5. Contexto y alcance
**Dentro:** stock, OC, ventas, margen, consignación, OS, expediente vehículo, recordatorios, puntos, dashboard, catálogo público.
**Fuera (v1.0):** facturación electrónica/SUNAT, contabilidad general, nómina, gestión avanzada de pauta publicitaria (NEC-12 solo registro básico + enlace; la publicación simultánea automática es fase 2), app móvil nativa (se cubre con web responsive).
**Supuestos:** inventario mensual se mantiene; teléfono del cliente será dato obligatorio; alineamiento computarizado sigue en herramientas externas.

## 6. Capacidades necesarias (mapeo a NEC unificadas)
C-01 Control stock y comparativa · C-02 OC completa · C-03 Margen y comparativa · C-04 Ventas · C-05 Analítica y recomendación · C-06 Dashboard responsive · C-07 Expediente + OS · C-08 Recordatorios 10k km · C-09 Teléfono obligatorio · C-10 Puntos · C-11 Consignación · C-12 Redes (básico) · C-13 Catálogo público.

## 7. Restricciones
R-01 Web responsive obligatoria (teléfono + laptop). R-02 Stack aprobado: Frontend Vue existente (SPA) + Backend Java Spring Boot (API REST) + PostgreSQL, arquitectura desacoplada. R-03 4 roles con permisos diferenciados. R-04 Datos personales (teléfono) con consentimiento y acceso restringido. R-05 Operación presencial en taller, con consulta remota solo para gerencia.

## 8. Concepto de operación (alto nivel)
Administradora Huancayo registra OC/ventas/OS y movimientos de stock y consignación → Mecánico consulta/actualiza expediente y OS → Supervisor valida → Administrador Lima ve dashboard, tendencias y recomendaciones y decide compra/promo → Sistema dispara recordatorios 10k km y acumula/canjea puntos → Visitante ve catálogo.

## 9. Criterios de éxito y aprobación
APROBADO el 2026-09-24 por decisión del cliente (sigas). PEND-01 se cierra como provisional sujeto a ratificación en I0. Verificación: Revisión por pares + Inspección. StRS/SRS se dan por APROBADOS en consecuencia para pasar a Diseño.
