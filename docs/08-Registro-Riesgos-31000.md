# 08 — Registro de Riesgos (ISO 31000:2018)
**Versión:** 1.0 | **Estado:** PROPUESTO | Escala: P×I (1-3) → Nivel

| ID | Riesgo | Causa | P×I | Plan (mitigar/contingencia) | Dueño |
|---|---|---|---|---|---|
| R-01 | Resistencia a dejar Excel / doble registro | Hábito presencial | 3×2=6 Alto | Paralelo corto + capacitación en taller + cierre I1 como hito obligatorio | Supervisor |
| R-02 | Datos Excel sucios (duplicados, sin teléfono) | Sin historial previo | 3×2=6 Alto | Plantilla de migración + validación telefónica obligatoria antes de importar | Adm. Hyo |
| R-03 | Reglas de puntos/recomendación indefinidas generan pérdida o desconfianza | PEND de negocio | 2×3=6 Alto | Congelar fórmula antes de I2/I3; topes anti-pérdida; mostrar base de cálculo | Adm. Lima |
| R-04 | Alcance redes (NEC-12) se desborda a community manager automático | Expectativa ambigua | 2×2=4 Medio | V1.0 solo registro básico; fase 2 la automatización (dejar por escrito) | Adm. Lima |
| R-05 | Privacidad teléfono (acceso indebido) | Dato sensible | 2×3=6 Alto | Roles + auditoría + consentimiento (RNF-02/07, TC-SEC) | Equipo dev |
| R-06 | Solo 4 personas, sin tiempo para validar | Operación diaria | 3×2=6 Alto | Validaciones cortas por iteración + un responsable por sede | Supervisor |
| R-07 | Dashboard lento en móvil con datos reales | Rendimiento | 2×2=4 Medio | Paginación, agregados, prueba RNF-03 con 10k registros | Equipo dev |
| R-08 | Sin misión/visión se retrasa aprobación BRS | PEND-01 | 2×2=4 Medio | Taller de 1h para definirlas en I0; sin esto no se aprueba H-01 | Adm. Lima |

Revisión mensual del registro; todo riesgo Alto con acción antes de la siguiente iteración.
