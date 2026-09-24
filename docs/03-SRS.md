# 03 — SRS: Especificación de Requisitos de Sistema / Software
**Norma:** ISO/IEC/IEEE 29148:2018 | **Versión:** 1.0 | **Estado:** PROPUESTO | **Riesgo base:** Medio
**Formato por requisito:** ID | Tipo F/NF | Prioridad | Fuente | Estado | Versión | Riesgo | Verificación | Trazabilidad

## 3.1 Funcionales — Inventario, compras y ventas

| ID | Requisito | Pri. | Fuente | Verif. | Traza |
|---|---|---|---|---|---|
| RF-01 | Registrar, editar y anular movimientos de stock (ingreso, salida, ajuste, merma) con motivo y responsable. | Alta | NEC-01 | Prueba | StR-08→NEC-01 |
| RF-02 | Cierre mensual de stock: conteo físico vs saldo sistema, reporte de descuadres valorizado. | Alta | NEC-01 | Prueba | StR-08, StR-01 |
| RF-03 | Comparativa ingresos vs stock físico en tabla y gráfico por periodo/producto. | Alta | NEC-01 | Demostración | StR-01 |
| RF-04 | Registrar OC (proveedor, ítems, costo unit., fecha, estado: borrador/confirmada/recibida/cancelada) y recepcionar parcial/total actualizando stock y costo promedio. | Alta | NEC-02 | Prueba | StR-09 |
| RF-05 | Consultar historial de OC con filtros (fecha, proveedor, estado). | Alta | NEC-02 | Prueba | StR-09 |
| RF-06 | Asignar margen (%) o precio por producto y recalcular precio sugerido; registrar historial de cambios. | Alta | NEC-03 | Prueba | StR-02 |
| RF-07 | Comparativa costo adquisición vs margen/ganancia por producto y periodo. | Alta | NEC-03 | Demostración | StR-02 |
| RF-08 | Registrar ventas (cliente, ítems, precio, descuento autorizado, forma de pago) con descuento automático de stock y bloqueo si no hay existencias (salvo autorización supervisor). | Alta | NEC-04 | Prueba | StR-09 |
| RF-09 | Consultar registro de ventas con filtros y anulación con reverso de stock. | Alta | NEC-04 | Prueba | StR-09 |
| RF-10 | Control de consignación: proveedor consignador, stock separado, ventas liquidadas y reporte de liquidación; excluida de valorizado propio. | Alta | NEC-11 | Prueba | StR-10 |
| RF-11 | Tendencias de ventas (top productos, rotación, serie temporal) por periodo/categoría. | Media | NEC-05 | Demostración | StR-03 |
| RF-12 | Recomendación qué comprar / qué promocionar con base explicable (días de cobertura, rotación, margen): p.ej. "Comprar X: cobertura < 15 días y rotación alta". | Media | NEC-05 | Análisis+Prueba | StR-03 |

## 3.2 Funcionales — Taller, clientes y fidelización

| ID | Requisito | Pri. | Fuente | Verif. | Traza |
|---|---|---|---|---|---|
| RF-20 | Ficha cliente + vehículo(s): placa (única), marca/modelo/año, km actual, **teléfono obligatorio y validado**, consentimiento de contacto. Sin teléfono no se crea ficha de seguimiento. | Alta | NEC-07/09 | Prueba | StR-06, StR-12 |
| RF-21 | Expediente técnico por vehículo: historial de OS, servicios, repuestos, km y observaciones del mecánico. | Alta | NEC-07 | Prueba | StR-12 |
| RF-22 | Registrar OS (vehículo, diagnóstico, tareas, repuestos, mecánico, estados: abierta/en proceso/cerrada/entregada) vinculada a venta cuando aplique. | Alta | NEC-07 | Prueba | StR-12 |
| RF-23 | Recordatorio cada 10.000 km: regla configurable (km o tiempo estimado), bandeja de vencidos/próximos, marca de contactado/agendado. Notificación inicial por listado + enlace WhatsApp (SMS/WhatsApp API en fase 2). | Alta | NEC-08 | Prueba | StR-05 |
| RF-24 | Programa de puntos: acumulación por consumo (regla configurable, p.ej. 1 pt por S/10), saldo por cliente, canje solo por servicios definidos (revisión suspensión/frenos, rotación) con autorización y tope anti-pérdida. | Media | NEC-10 | Prueba | StR-07 |
| RF-25 | Catálogo público (Huancayo): productos/servicios con precio, disponibilidad y contacto; gestión interna de publicaciones. | Media | NEC-13 | Demostración | StR-13 |
| RF-26 | Módulo básico de redes (v1.0): registro de consultas de clientes, publicaciones realizadas y gasto de pauta; publicación simultánea automática queda fase 2. | Baja | NEC-12 | Inspección | StR-11 |

## 3.3 No funcionales (ISO 25010 entre paréntesis)

| ID | Requisito | Pri. | Verif. |
|---|---|---|---|
| RNF-01 (Usabilidad) | Responsive 360px–1440px; dashboard gerencial usable en móvil y laptop sin pérdida de KPIs. | Alta | Prueba |
| RNF-02 (Seguridad) | Roles ACT-001..004 + permisos; contraseñas hash; auditoría de movimientos stock/ventas/OC/OS. Teléfono solo visible a roles autorizados. | Alta | Prueba+Inspección |
| RNF-03 (Rendimiento) | Listados < 3 s con 10k registros; dashboard < 5 s. | Media | Prueba |
| RNF-04 (Fiabilidad/Integridad) | Ninguna venta/OS descuenta sin transacción atómica; anulaciones con reverso; backup diario. | Alta | Prueba+Análisis |
| RNF-05 (Mantenibilidad/Portabilidad) | Stack Vue existente; código tipado, lint y pruebas (vitest/playwright ya en `vue/`). Despliegue web estándar. | Media | Inspección |
| RNF-06 (Compatibilidad) | Chrome/Edge/Firefox/Safari últimas 2 versiones. | Media | Prueba |
| RNF-07 (Privacidad) | Consentimiento de contacto registrado; borrado/anonymización a pedido. | Alta | Inspección |

## 3.4 Reglas de negocio clave
RN-01 Consignación nunca suma al valorizado propio. RN-02 Precio = costo × (1+margen) salvo precio manual autorizado. RN-03 Canje de puntos nunca por dinero ni por productos con margen negativo. RN-04 OS cerrada bloquea edición (solo nota complementaria).

Todos: **Tipo F salvo RNF, Estado PROPUESTO, v1.0, Riesgo Medio** salvo RF-12/RF-24/RF-26 (Riesgo Alto por definición de reglas pendientes).
