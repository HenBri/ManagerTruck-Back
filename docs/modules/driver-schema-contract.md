# Contrato de esquema: Driver (conductores)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `driver_table`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE driver_table`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 186-207) **sin diferencias** (PK, 3 UNIQUEs, FKs, DEFAULTs).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `company` (FK `fk_driver_company`), `company_user` (FK compuesta `fk_driver_membership`), `partner_table` (FK compuesta `fk_driver_partner_person`). Requiere `company-schema-contract.md`, `company-user-schema-contract.md` y `partner-schema-contract.md`.

## Objetivo de negocio

Registra a los **conductores** de una empresa de transporte. Un conductor **no necesariamente** es propietario de un camión y **no requiere** cuenta de usuario (`user_id` opcional). Driver es un **perfil del negocio**, no una cuenta independiente. `person_partner_id` se informa **solo cuando el conductor es la misma persona física** que un socio tipo `PERSON` (no es el campo de empleador/proveedor): permite que **un socio sea simultáneamente conductor** sin duplicar la persona. Los conductores de auxilio externo (fase 6) **no** se registran aquí. La asignación conductor↔camión (`truck_driver_assignment`) queda **fuera de esta iteración**.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `driver_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK simple |
| `company_id` | `BIGINT UNSIGNED` | NO | — | **tenant** |
| `user_id` | `BIGINT UNSIGNED` | SÍ | NULL | cuenta opcional; **UNIQUE por tenant** |
| `person_partner_id` | `BIGINT UNSIGNED` | SÍ | NULL | solo si conductor = socio `PERSON`; **UNIQUE por tenant** |
| `driver_name` | `VARCHAR(150)` | NO | — | nombre completo |
| `driver_ci` | `VARCHAR(30)` | SÍ | NULL | cédula de identidad |
| `driver_phone` | `VARCHAR(25)` | SÍ | NULL | |
| `driver_license` | `VARCHAR(40)` | **NO** | — | licencia de conducir |
| `license_category` | `VARCHAR(20)` | SÍ | NULL | categoría de licencia |
| `license_expiration` | `DATE` | SÍ | NULL | fecha de vencimiento |
| `driver_state` | `ENUM('ACTIVE','INACTIVE')` | NO | `ACTIVE` | soft delete permitido por esquema |

- **PK:** simple `(driver_id)` con AUTO_INCREMENT.
- **UNIQUE:**
  - `uq_driver_tenant_id (company_id, driver_id)`.
  - `uq_driver_company_user (company_id, user_id)` — **una sola cuenta de usuario puede ser conductor en un tenant** (clave para la regla "no duplicar cuentas").
  - `uq_driver_partner_person (company_id, person_partner_id)` — un socio/persona puede estar vinculado como conductor **una sola vez** por tenant.
- **Índices:** los UNIQUE anteriores sirven de índice para los accesos por `(company_id, ...)`.
- **FKs:**
  - `fk_driver_company (company_id) → company(company_id)` **ON DELETE RESTRICT**.
  - `fk_driver_membership (company_id, user_id) → company_user(company_id, user_id)` **ON DELETE RESTRICT**.
  - `fk_driver_partner_person (company_id, person_partner_id) → partner_table(company_id, partner_id)` **ON DELETE RESTRICT** — obliga a que el socio referido sea del **mismo tenant**.
- **CHECK:** ninguno.
- **Enums:** `driver_state = ACTIVE | INACTIVE`.
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` es el tenant; llega **solo por la ruta** `/api/companies/{companyId}/drivers`, nunca por el body.
- Roles y accesos:
  - **SECRETARY** (o SUPER_ADMIN): CRUD completo sobre los drivers **de su empresa**.
  - **DRIVER:** lectura de **su propio registro** (`user_id` = cuenta autenticada); sin escritura.
  - **PARTNER:** lectura de su propio perfil de conductor si está vinculado (`person_partner_id`/`user_id` = suyo); sin escritura sobre otros conductores.
  - **SUPER_ADMIN:** administración global sobre tenant explícito de la ruta.
- Reglas para SUPER_ADMIN: solo por rutas con `{companyId}` explícito.

## Mapeo Java/JPA

- Entidad: `domain/Driver.java`, PK simple `@Id @GeneratedValue(IDENTITY) Long driverId`.
- `driver_state` → enum `DriverStatus` con `@Enumerated(EnumType.STRING)`.
- `license_expiration` → `LocalDate`; `companyId`, `userId`, `personPartnerId` como `Long` escalares.
- Sin asociaciones JPA obligatorias a Partner (la validación de `person_partner_id` es de aplicación); el motor DB mantiene la integridad vía FK compuesta.
- DTOs seguros: `DriverResponse(s)` (driverId, companyId, userId, personPartnerId, driverName, driverCi, driverPhone, driverLicense, licenseCategory, licenseExpiration, driverState).

## Reglas no representables en SQL

- **`person_partner_id` debe apuntar a un partner `partner_type='PERSON'` del mismo tenant** — el SQL no puede consultarlo (N12 probó que acepta un partner `BUSINESS`) → validar en `Save/UpdateDriverUseCase` → `400/404`.
- **Licencia estricta (decisión aprobada):**
  - `driver_license`: obligatorio, sin blancos, `<= 40` caracteres → `400` (Bean Validation + trim).
  - `license_category`: opcional, `<= 20`; si se informa, debe pertenecer al catálogo conocido `{A1,A2,B1,B2,C1,C2,D1,D2}` → `400`.
  - `license_expiration`: opcional, pero si se informa **no puede ser fecha anterior a hoy** al registrar/actualizar → `400`.
- **No duplicar cuentas:** el UNIQUE `(company_id, user_id)` da defensa física (ERROR 1062 → mapear a `409`), con pre-check para dar mensaje claro. Igual para `person_partner_id`.
- Vincular `user_id` exige membresía `ACTIVE` en la empresa → `409` si INACTIVE.
- Un socio que también es conductor: crear el partner y el driver referenciando `person_partner_id`; **la misma `user_id` puede estar en `partner_table` y en `driver_table`** (tablas distintas) — permitido explícitamente.
- Un conductor INACTIVE no puede recibir nuevas asignaciones (fase 6); aquí solo soft delete.

## Plan de migración

- Archivos: **ninguno** (sin DDL; sin Flyway/Liquibase — decisión mantenida de fases 1-2).
- Datos preexistentes: **0 filas** (verificado en `truck_sass`).
- Riesgo de bloqueo/downtime: no aplica.
- Plan de reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- Driver sin `driver_license` → **ERROR 1364** `Field 'driver_license' doesn't have a default value`.
- Dos drivers con el mismo `user_id` en el tenant → **ERROR 1062** `uq_driver_company_user`.
- `person_partner_id` de otra empresa → **ERROR 1452** FK compuesta `fk_driver_partner_person`.
- `person_partner_id` apuntando a partner `BUSINESS` del **mismo** tenant → **ACEPTADO por SQL** (N12) → regla de backend obligatoria.
- `user_id` sin membresía → ERROR 1452 FK `fk_driver_membership`.
- Inserción válida con/sin `user_id` y con/sin `person_partner_id` → aceptada.
- Verificación: tras `ROLLBACK`, `COUNT(*) driver_table = 0`.

## Entrega al generador de módulo

- Casos de uso y rutas tentativas (bajo `/api/companies/{companyId}/drivers`):
  - `GET /` — listar paginado (SECRETARY/SUPER_ADMIN: todos; DRIVER/PARTNER: solo los suyos).
  - `GET /{driverId}` — detalle por id+tenant.
  - `POST /` — crear (SECRETARY/SUPER_ADMIN), `201`.
  - `PUT /{driverId}` — actualizar escalares (incluye campos de licencia con validación estricta).
  - `PUT /{driverId}/status` — cambiar `driver_state`.
- Métodos de repository tenant-scoped requeridos: `findByCompanyIdAndDriverId`, `findByCompanyId(Pageable)`, `findByCompanyIdAndUserId`, `existsByCompanyIdAndUserId`.
- Decisiones todavía pendientes: asignación conductor↔camión (`truck_driver_assignment`) → fuera de alcance de la fase 3.
