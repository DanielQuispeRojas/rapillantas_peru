# 06 — Plan de Calidad y Aceptación (ISO/IEC 25010:2023 / 25023)
**Versión:** 1.0 | **Estado:** PROPUESTO

## 1. Atributos priorizados
1. **Funcionalidad/Idoneidad:** cobertura 100% RF Alta con prueba. 2. **Seguridad/Confidencialidad:** roles + auditoría + teléfono restringido. 3. **Usabilidad:** responsive + cierre mensual ≤ 5 pasos. 4. **Fiabilidad/Integridad:** atomicidad stock/ventas/OS. 5. **Rendimiento:** listados <3s, dashboard <5s.

## 2. Criterios de aceptación por objetivo (BRS)
* OBJ-01: cierre mensual en la mitad de tiempo (medido 2 cierres seguidos).
* OBJ-02: 100% productos con costo+margen; comparativa sin errores vs cálculo manual (muestra 50).
* OBJ-03: bandeja 10k km operativa, 0 clientesContactables sin teléfono.
* OBJ-04: Lima valida dashboard en su teléfono y laptop (checklist firmado).
* OBJ-05: canje de puntos bloqueado si supera tope/permite pérdida; prueba de intento de abuso superada.

## 3. Actividades QA por iteración
Revisión por pares de cada RF, checklist RNF-02/RNF-04, pruebas en 360px y laptop, validación con datos reales de Excel (paralelo en I1).

## 4. No conformidades
Defecto Alta (pérdida stock/dinero, bypass de rol/teléfono) bloquea entrega. Media/Baja con plan de corrección.
