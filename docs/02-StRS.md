# 02 — StRS: Especificación de Requisitos de Interesados (Stakeholder Requirements)
**Norma:** ISO/IEC/IEEE 29148:2018 | **Versión:** 1.0 | **Estado:** PROPUESTO

## 1. Identificación de stakeholders

| ID | Stakeholder | Rol | Interés principal |
|---|---|---|---|
| STK-01 | Administrador Lima (ACT-001) | Gerente/decisor | Comparar físico vs ingresos, costo vs margen, marketing, dashboard móvil/laptop |
| STK-02 | Supervisor (ACT-002) | Control operativo | Validar OS, stock, ventas |
| STK-03 | Administradora Huancayo (ACT-003) | Operación diaria | Stock mensual, compras/ventas, consignación, redes, teléfono cliente |
| STK-04 | Mecánico (ACT-004) | Taller | Expediente técnico, OS, orden/limpieza, sin historial hoy |
| STK-05 | Cliente vehículo | Beneficiario | Recordatorio 10k km, puntos, historial de su vehículo |
| STK-06 | Visitante Huancayo | Público | Ver catálogo |

## 2. Necesidades por stakeholder → Requisitos de stakeholder (StR)

| StR | Stakeholder | Necesidad origen | Descripción | Prioridad |
|---|---|---|---|---|
| StR-01 | STK-01 | NEC-01 | Ver comparativa ingresos vs stock físico en un panel | Alta |
| StR-02 | STK-01 | NEC-03 | Ver comparativa costo adquisición vs margen | Alta |
| StR-03 | STK-01 | NEC-05 | Recibir análisis y recomendación qué comprar / qué promocionar | Media |
| StR-04 | STK-01 | NEC-06 | Consultar lo anterior en móvil y laptop | Alta |
| StR-05 | STK-05/STK-01 | NEC-08 | Seguimiento de servicio/venta con recordatorio cada 10.000 km | Alta |
| StR-06 | STK-03 | NEC-09 | Teléfono obligatorio para poder hacer seguimiento | Alta |
| StR-07 | STK-05/STK-01 | NEC-10 | Puntos canjeables por revisión suspensión/frenos y rotación | Media |
| StR-08 | STK-03 | NEC-01 | Cierre de stock mensual simple (adiós Excel disperso) | Alta |
| StR-09 | STK-03 | NEC-04 | Control de compras (OC) y ventas en un solo lugar | Alta |
| StR-10 | STK-03 | NEC-11 | Control de mercadería en consignación separada del stock propio | Alta |
| StR-11 | STK-03 | NEC-12 | Gestión básica de redes (atención, publicación, pauta) | Baja (fase 2 automatizar) |
| StR-12 | STK-04 | NEC-07 | Expediente técnico + historial + OS por vehículo | Alta |
| StR-13 | STK-06 | NEC-13 | Catálogo público de productos/servicios Huancayo | Media |

## 3. Contexto, restricciones y escenarios
* **Contexto:** taller presencial, 4 personas, todo en Excel, sin órdenes de trabajo, comunicación por WhatsApp/llamadas.
* **Restricciones:** responsive obligatorio; 4 roles; teléfono con consentimiento; consignación no debe mezclarse con stock propio en valorización.
* **Escenarios clave:**
  1. Cierre mensual: administradora cuenta físico → sistema compara con ingresos → descuadres listados → supervisor valida.
  2. Venta + OS: se registra venta/OS, descuenta stock, asocia vehículo/cliente (teléfono obligatorio), suma puntos.
  3. 10.000 km: sistema alerta (por km registrado o tiempo estimado) → se contacta por WhatsApp/teléfono → se agenda OS.
  4. Decisión gerencial: Lima abre dashboard en teléfono → ve margen, tendencia, recomendación → aprueba compra/promo.

## 4. Condiciones y criterios de aceptación relevantes
* Sin teléfono no se puede crear ficha de seguimiento (StR-06 bloqueante).
* Consignación siempre etiquetada y excluida del margen propio salvo comisión definida.
* Recomendación de compra/promo debe mostrar base de cálculo (no "caja negra").
* Dashboard legible en pantalla 360px y en laptop sin pérdida de indicadores.

**Verificación:** Demostración + Revisión por pares. **Trazabilidad:** cada StR → 1..n requisitos SRS (ver Matriz 04).
