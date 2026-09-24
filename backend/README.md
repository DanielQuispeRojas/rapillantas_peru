# Backend Rapillantas (Spring Boot + PostgreSQL)
Desacoplado de `vue/` (SPA). Ver `docs/09-Arquitectura.md`, `db/V1__schema.sql`, `docs/11-API-OpenAPI.yaml`.

## Estructura por módulo (RF ↔ paquete)
`inventory(RF-01..03) purchasing(RF-04/05) pricing(RF-06/07) sales(RF-08/09) consignment(RF-10) analytics(RF-11/12) customers(RF-20) workshop(RF-21..23) loyalty(RF-24) catalog(RF-25) social(RF-26) auth-audit(RNF-02/04)`

Patrón por módulo: `Controller → Service (@Transactional) → Repository (JPA) → Entity`. Validación: `telefono NOT NULL` (400), placa UNIQUE (409), OS cerrada inmutable (RN-04), consignación fuera de valorizado (RN-01).

## Arranque
1. `createdb rapillantas && psql -f ../db/V1__schema.sql`
2. `mvn spring-boot:run` → `http://localhost:8080/api/v1/catalogo`
3. Frontend `vue/`: `VITE_API_URL=http://localhost:8080/api/v1`
