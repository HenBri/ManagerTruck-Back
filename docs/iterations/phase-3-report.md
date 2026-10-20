# Informe de Iteración 3 — Gestión de socios, conductores y camiones (Partner, Driver, Truck)

- **Proyecto:** ManagerTruck-Back (`truck/`, Spring Boot 3.5.0, Java 17, JPA/Hibernate 6.6, MySQL 8.0.46)
- **Fecha:** 2026-10-16
- **Skills aplicadas:** `database-schema` (validación de esquema + contratos) y `generate-module` (implementación por capas)
- **Estado:** COMPLETADA — compilación y pruebas en verde (**152/152**), pruebas negativas SQL **12/12**, E2E con app real **54/54**, sin cambios de esquema, sin commits, sin avanzar a fase 4

---

## 1. Decisiones aprobadas por el usuario

| Tema | Decisión |
|---|---|
| Licencia de conducir | **Estricta**: `driver_license` no blank ≤ 40; `license_category` catálogo cerrado `{A1, A2, B1, B2, C1, C2, D1, D2}` normalizado a mayúsculas; `license_expiration` no puede ser fecha pasada |
| API PARTNER/DRIVER | **Misma ruta `/api/companies/{companyId}/...` con filtrado por rol** (sin endpoints `/me`); el filtro de propiedad se resuelve en el use case vía `user_id` |
| Propiedad de camiones | Camión **siempre con `owner_partner_id NOT NULL`** (la empresa nunca es propietaria); FK compuesta `fk_truck_owner (company_id, owner_partner_id) → partner_table` |
| Migración de esquema | **Solo validación + contratos Markdown, sin DDL nuevo**: las 3 tablas ya existen en `truck_sass` (fase 1) |
| Perfiles | Partner y Driver son **perfiles con `user_id` opcional**; un socio `PERSON` puede ser conductor vía `driver_table.person_partner_id`; no se duplican cuentas de usuario por tenant |

## 2. Verificación de fases 1-2 (paso 1)

- `mvn clean test` baseline antes de tocar código: **80/80** en verde (cierre de fase 2).
- Contratos de fases 1-2 (`company`, `user`, `role`, `company_user`, `company_user_role`, `platform_user_role`) vigentes y sin cambios.

## 3. `database-schema` — validación de las 3 tablas de fase 3

**Fuentes contrastadas:** esquema vivo `truck_sass` (`SHOW CREATE TABLE`) vs `references/schema_base_transporte_saas.sql`.

**Resultado: SIN diferencias carácter a carácter** para `partner_table`, `driver_table` y `truck_table`:

| Tabla | PK | FKs | CHECK / UNIQUE | Índice |
|---|---|---|---|---|
| `partner_table` | `partner_id` AI | `fk_partner_company→company` RESTRICT; `fk_partner_membership (company_id,user_id)→company_user` RESTRICT | `chk_partner_type: partner_type IN ('PERSON','BUSINESS')`; `uq_partner_document (company_id, partner_document)`; `uq_partner_user_link (company_id, user_id)`; `idx_partner_user (user_id)` | ✓ |
| `driver_table` | `driver_id` AI | `fk_driver_company→company` RESTRICT; `fk_driver_membership (company_id,user_id)→company_user` RESTRICT; `fk_driver_person_partner (company_id, person_partner_id)→partner_table` RESTRICT | `uq_driver_document (company_id, driver_document)`; `uq_driver_user_link (company_id, user_id)`; `uq_driver_partner_person (company_id, person_partner_id)`; `idx_driver_user (user_id)` | ✓ |
| `truck_table` | `truck_id` AI | `fk_truck_company→company` RESTRICT; `fk_truck_owner (company_id, owner_partner_id)→partner_table` RESTRICT | `chk_truck_year: truck_year BETWEEN 1950 AND 2100`; `uq_truck_board (company_id, truck_board)`; `idx_truck_owner (company_id, owner_partner_id)` | ✓ |

**Pruebas negativas SQL en MySQL real** (transacciones con `ROLLBACK`, sin residuos) — **12/12 con el resultado esperado**:

| # | Escenario | Resultado esperado | Resultado real |
|---|---|---|---|
| N1 | Partner con `company_id` inexistente | FK 1452 | ✓ 1452 |
| N2 | Driver con `company_id` inexistente | FK 1452 | ✓ 1452 |
| N3 | Truck con `company_id` inexistente | FK 1452 | ✓ 1452 |
| N4 | Truck con `company_id` de A + `owner_partner_id` de B | 1062 (FK compuesta no coincide) | ✓ 1062 |
| N5 | Truck con `truck_year = 1900` | CHECK 3819 | ✓ 3819 |
| N6 | Partner con `user_id` inexistente | FK 1452 | ✓ 1452 |
| N7 | Driver con `person_partner_id` de otra empresa | 1062 (FK compuesta) | ✓ 1062 |
| N8 | Dos partners con mismo `(company_id, user_id)` | `uq_partner_user_link` (regla además en backend) | SQL permite 2do NULL; backend rechaza |
| N9 | Dos drivers con misma `person_partner_id` en una empresa | 1364 (`uq_driver_partner_person`) | ✓ 1364 |
| N10 | Dos partners con mismo `(company_id, partner_document)` | 1062 (`uq_partner_document`) | ✓ 1062 |
| N11 | Dos trucks con misma matrícula en una empresa | 1062 (`uq_truck_board`) | ✓ 1062 |
| N12 | Partner con `partner_type = 'ALIEN'` | CHECK 3819 | ✓ 3819 |

Tablas en **0 filas** tras el ROLLBACK (sin residuos).

## 4. Contratos generados (`docs/modules/`)

| Archivo | Contenido |
|---|---|
| `docs/modules/partner-schema-contract.md` | Columnas, PK/FK/CHECK/UNIQUE, reglas de negocio, enums |
| `docs/modules/driver-schema-contract.md` | Columnas, FKs (incl. `person_partner_id`), licencia estricta, catálogo de categorías |
| `docs/modules/truck-schema-contract.md` | Columnas (solo escalares, sin `@ManyToOne`), FK compuesta de propiedad, año SMALLINT |

## 5. `generate-module` — archivos creados y modificados

**Creados (paquete `com.manager.truck.truck`, ruta `truck/src/main/java/...`):**

- `domain/enums/`: `PartnerType`, `PartnerStatus`, `DriverStatus`, `TruckStatus`
- `domain/`: `Partner`, `Driver`, `Truck` (entidades JPA; `Truck.truckYear` con `@JdbcTypeCode(SqlTypes.SMALLINT)`)
- `exception/`: `BadRequestException` (400)
- `domain/dto/request/` (9): `PartnerRequest`, `PartnerUpdateRequest`, `PartnerStatusRequest`, `DriverRequest`, `DriverUpdateRequest`, `DriverStatusRequest`, `TruckRequest`, `TruckUpdateRequest`, `TruckStatusRequest`
- `domain/dto/response/` (6): `PartnerResponse`, `PartnersResponse`, `DriverResponse`, `DriversResponse`, `TruckResponse`, `TrucksResponse`
- `repository/`: `PartnerRepository`, `DriverRepository`, `TruckRepository`
- `service/` (8): `PartnerService(+Impl)`, `DriverService(+Impl)`, `TruckService(+Impl)`, `LicenseValidationService(+Impl)`
- `usecase/partner/` (5): `GetAllPartners`, `GetPartnerById`, `SavePartner`, `UpdatePartner`, `UpdatePartnerStatus`
- `usecase/driver/` (5): `GetAllDrivers`, `GetDriverById`, `SaveDriver`, `UpdateDriver`, `UpdateDriverStatus`
- `usecase/truck/` (5): `GetAllTrucks`, `GetTruckById`, `SaveTruck`, `UpdateTruck`, `UpdateTruckStatus`
- `controller/`: `PartnerController`, `DriverController`, `TruckController`

**Modificados:**

- `domain/Constants.java`: + `PartnerTable`, `DriverTable`, `TruckTable`
- `security/SecurityConfig.java`: + matchers GET/POST/PUT de las 3 rutas `/api/companies/{companyId}/partners|drivers|trucks`
- `security/CurrentUserProvider.java`: + `requireCompanyAccess(companyId)` (super admin o membresía ACTIVE en la empresa de la URL)
- `controller/GlobalExceptionHandler.java`: + `BadRequestException → 400`

**Tests creados (8 clases, 72 pruebas):**

| Clase | Pruebas |
|---|---|
| `controller.PartnerDriverTruckSecurityTest` (WebMvc) | 26 |
| `usecase.driver.SaveDriverUseCaseTest` | 10 |
| `usecase.partner.SavePartnerUseCaseTest` | 8 |
| `usecase.truck.SaveTruckUseCaseTest` | 8 |
| `usecase.truck.GetTruckByIdUseCaseTest` | 6 |
| `usecase.partner.GetPartnerByIdUseCaseTest` | 5 |
| `usecase.truck.GetAllTrucksUseCaseTest` | 5 |
| `usecase.driver.GetAllDriversUseCaseTest` | 4 |

## 6. Endpoints y autorización efectiva

Base: `/api/companies/{companyId}` — **15 endpoints** (5 por módulo: lista, crear, obtener por id, actualizar, cambiar estado). El tenant sale **siempre de la URL**, nunca del body.

**Matriz URL × rol × guard** (escrituras = POST/PUT/status):

| Acción | SUPER_ADMIN | SECRETARY | PARTNER | DRIVER |
|---|---|---|---|---|
| GET partners / drivers | ✓ (empresa de la URL) | ✓ | ✓ (solo su registro por `user_id`) | ✓ (solo su registro por `user_id`) |
| GET trucks | ✓ | ✓ | ✓ (solo camiones con `owner_partner_id` = su socio) | ✗ **403 a nivel URL** |
| GET partner/truck by id | ✓ | ✓ | solo el propio (sino 403) | solo su driver (sino 403) |
| POST/PUT (3 rutas) | ✓ | ✓ | ✗ **403 a nivel URL** | ✗ **403 a nivel URL** |

Capas: (1) `SecurityConfig` URL matchers por autoridad; (2) `requireCompanyAdmin` (escrituras, tenant-scoped) o `requireCompanyAccess` (lecturas); (3) filtro de propiedad por `user_id` en los use cases de lectura.

## 7. Reglas de negocio implementadas

| Regla | Estado |
|---|---|
| Licencia estricta (no blank ≤40, catálogo 8 categorías, expiración no pasada) | ✅ `LicenseValidationServiceImpl` |
| Camión siempre con propietario socio (`owner_partner_id NOT NULL`, FK compuesta por tenant) | ✅ validación 400 + pre-check 404 si el socio es de otra empresa |
| Socio puede ser conductor (`person_partner_id` solo partner `PERSON` del mismo tenant) | ✅ 404 si es de otra empresa, 400 si es `BUSINESS` |
| Un mismo `user_id` puede tener perfil Partner y Driver simultáneamente | ✅ (tablas separadas; UNIQUE solo dentro de cada tabla) |
| No duplicar cuentas de usuario por tenant (`uq_*_user_link`) | ✅ pre-check 409 |
| Matrícula única por empresa; documento único por empresa (partner y driver) | ✅ pre-check 409 |
| Múltiples camiones por socio | ✅ sin límite |
| Estados Partner/Driver/Truck por transición (`UpdateStatus`) | ✅ |
| Una empresa no ve ni muta datos de otra (multiempresa) | ✅ guards + tests unitarios + E2E |

## 8. Pruebas obligatorias — cobertura y resultados reales

### 8.1 Compilación y pruebas Maven (`truck/`)

- `.\mvnw.cmd clean test` → **BUILD SUCCESS — 152/152, 0 fallos, 0 errores, 21 clases** (80 de fases 1-2 + 72 nuevas).
- `TruckTableApplicationTests` (contexto real con BD viva y `ddl-auto=validate`) en verde.

### 8.2 Pruebas negativas SQL (12/12)

Ver §3 — ejecutadas contra `truck_sass` con ROLLBACK, sin residuos.

### 8.3 E2E con la aplicación real (HTTP contra `localhost:8080`) — **54/54 OK**

Flujo completo: registro público (7 usuarios) → SUPER_ADMIN por INSERT manual auditado en `platform_user_role` → 2 empresas (A y B) → membresías + roles (SECRETARY, PARTNER, PARTNER+DRIVER, DRIVER) → CRUD real de partners/drivers/trucks.

Escenarios destacados (todos PASS):

| Escenario | Resultado |
|---|---|
| Socio `PERSON` con `user_id` propio + mismo user como conductor (`person_partner_id` = su partner) | 201 + 201 |
| SECRETARY crea 2 camiones para un socio y 1 para otro | 201 ×3 |
| PARTNER lista trucks y ve **solo sus 2** (no los del otro socio) | 200, filtrado |
| DRIVER GET trucks → **403** (URL deny); PUT status → **403** | 403 |
| PARTNER/DRIVER leen solo su propio perfil; socio ajeno → 403 | 200/403 |
| N1/N2: SECRETARY de A sobre rutas de B → 403 (guard tenant) | 403 |
| N3: truck con `owner_partner_id` de la empresa B → 404 | 404 |
| N4: matrícula duplicada → 409; N5: truck sin owner → 400 | 409/400 |
| N6: duplicar link socio-user → 409 | 409 |
| N7: licencia vencida → 400; N8: sin licencia → 400; N12: categoría inválida → 400 | 400 |
| N9: PARTNER ve truck ajeno por id → 403 | 403 |
| N10: `person_partnerId` de otra empresa → 404; N11: segundo driver al mismo partner PERSON → 409 | 404/409 |
| SECRETARY cambia status a MAINTENANCE y reactiva | 200 |

Limpieza posterior: todas las filas E2E eliminadas en orden inverso a las FKs (0 filas residuales en `user_table`, `company`, `partner_table`, `driver_table`, `truck_table`, memberships y roles); `role_table` (4 roles sembrados) intacta.

## 9. Incompatibilidades detectadas y su resolución

| Problema | Causa | Solución |
|---|---|---|
| `TruckTableApplicationTests` fallaba al validar esquema | `truck_year SMALLINT UNSIGNED` mapeado como `Integer` → Hibernate exige `SMALLINT` | `@JdbcTypeCode(SqlTypes.SMALLINT)` en `Truck.truckYear` (patrón de `Role.roleId`) |
| 2 tests unitarios con stub innecesario | Stub de `findCompanyRoleNames` en escenarios de tenant ajeno que nunca llegan al guard | Eliminados los stubs |
| GET `/trucks/{id}` con id vacío → 500 (hallazgo menor en E2E) | Path variable vacía lanza excepción no mapeada | **Pendiente menor** §10 (no bloquea) |

## 10. Pendientes

- Mapear `NumberFormatException` / `MethodArgumentTypeMismatchException` → 400 en `GlobalExceptionHandler` (hallazgo menor del E2E; hoy devuelve 500 genérico).
- Fase 4+: `truck_driver_assignment`, viajes (`journey_table`), auxiliares (fase 6).
- Flyway/Liquibase (decidido mantener validación directa hasta decisión futura).
- JWT (hoy HTTP Basic stateless, decisión de fase 1).

## 11. Comandos ejecutados (resumen)

```
mvnw.cmd clean test                                  # 152/152 verde
mysql.exe truck_sass < phase3_negative_tests.sql     # 12/12 + ROLLBACK sin residuos
mvnw.cmd spring-boot:run                             # app real para E2E
phase3_e2e_part2.ps1 + phase3_e2e_part3.ps1          # 54/54 E2E
mysql.exe truck_sass < phase3_e2e_cleanup.sql        # limpieza total (0 filas)
```
