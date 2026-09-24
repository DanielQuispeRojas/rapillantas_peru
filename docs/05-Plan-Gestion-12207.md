# 05 — Plan de Gestión de Proyecto (ISO/IEC 12207:2017)
**Versión:** 1.0 | **Estado:** PROPUESTO

## 1. Ciclo de vida elegido: Iterativo-incremental (4 iteraciones)
* **I0 Fundaciones (2 sem):** definir misión/visión (PEND-01), aprobar BRS/StRS, reglas RF-12/RF-24, diseño datos + roles.
* **I1 Núcleo operativo (3 sem):** RF-01/02/04/05/08/09/20/21/22 + RNF-02/04. Entregable: stock+OC+ventas+OS usables, adiós Excel.
* **I2 Gerencia y taller (3 sem):** RF-03/06/07/10/11/23 + RNF-01. Entregable: comparativas, consignación, recordatorios, dashboard v1.
* **I3 Fidelización y público (2 sem):** RF-12/24/25/26 + RNF-03/06. Entregable: recomendación explicable, puntos, catálogo.
* Cada iteración: requisitos→diseño→implementación→pruebas→revisión (procesos 12207: adquisición, suministro, desarrollo, operación, mantenimiento + soporte: CM, QA, verificación/validación, auditoría).

## 2. Roles (mapeo ACT)
Administrador Lima = Product Owner (aprueba). Supervisora/Supervisor = QA/validador. Administradora Hyo + Mecánico = usuarios clave. Equipo dev: FE Vue / BE+DB / Tester (puede ser 1-2 personas polivalentes).

## 3. Hitos y criterios de salida
H-01 BRS/StRS APROBADO (requiere misión/visión). H-02 SRS APROBADO. H-03 I1 VALIDADO (cierre mensual paralelo Excel=0 descuadres no explicados). H-04 Dashboard en móvil validado por Lima. H-05 Puesta en producción + capacitación + migración Excel inicial.

## 4. Gestión de cambios y configuración
Cambio a NEC/RF vía solicitud con impacto (alcance/tiempo/riesgo); actualiza Matriz 04 y versión (1.x). Repo: `vue/` para código; `docs/` versionado; ramas `main/develop/feature/RF-xx`.

## 5. Supuestos/dependencias
Datos Excel iniciales deben entregarse limpios; definición de fórmula de puntos y de recomendación antes de I2/I3; un responsable disponible por sede para validar cada iteración.
