# Contrato de esquema: TruckDriverAssignment (asignación de conductores a camiones)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `truck_driver_assignment`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE truck_driver_assignment`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 235-250) **sin diferencias** (PK, UNIQUEs, índice, FKs compuestas, CHECK).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `truck_table` y `driver_table` (ambas FKs compuestas). Requiere `truck-schema-contract.md` y `driver-schema-contract.md`.

## Objetivo de negocio

Permite asignar un **conductor** a un **camión** de un socio, con vigencia temporal, y conservar el historial de asignaciones. Reglas de dominio innegociables:

- Un camión **pertenece a un socio** (`truck_table.owner_partner_id`), pero puede ser **conducido por otra persona**: la asignación **no modifica la propiedad** del camión.
- Un conductor puede manejar **distintos camiones** a lo largo del tiempo (y el mismo camión puede cambiar de conductor entre vigencias).
- Toda asignación tiene `assigned_from` (inicio, obligatorio) y `assigned_until` (fin, opcional → **NULL = vigente/abierta**).
- **No se permiten asignaciones temporales incompatibles**: ni dos vigencias solapadas para el **mismo camión**, ni para el **mismo conductor**. El solape es una regla **cross-row** no exigible por CHECK/UNIQUE (el UNIQUE solo cubre el duplicado exacto del inicio) → validación transaccional con **lock pesimista** en el backend.
- El conductor y el camión deben pertenecer a la **misma empresa gestora**: lo garantizan las FKs compuestas `(company_id, ...)`.
- **Conservar el historial**: finalizar una asignación solo llena `assigned_until`; **no se borra** la fila (sin DELETE).
- Los camiones de **auxilio externo** no participan en esta tabla; los viajes (`journey_table`) consumirán `(company_id, assignment_id)` en una fase posterior.

`company_id` = empresa que **administra** la asignación (tenant SaaS), no la titularidad del camión.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `assignment_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK simple |
| `company_id` | `BIGINT UNSIGNED` | NO | — | **tenant**; componente de ambas FKs |
| `truck_id` | `BIGINT UNSIGNED` | NO | — | camión asignado (FK compuesta) |
| `driver_id` | `BIGINT UNSIGNED` | NO | — | conductor asignado (FK compuesta) |
| `assigned_from` | `DATETIME` | NO | — | inicio de la vigencia |
| `assigned_until` | `DATETIME` | **SÍ** | NULL | fin de la vigencia; **NULL = abierta** |

- **PK:** simple `(assignment_id)` con AUTO_INCREMENT.
- **UNIQUE:**
  - `uq_assignment_tenant_id (company_id, assignment_id)` — defensa de tenant.
  - `uq_assignment_start (company_id, truck_id, driver_id, assigned_from)` — impide el duplicado **exacto** de una vigencia; **no** cubre solapes parciales.
- **Índices:** `idx_assignment_driver (company_id, driver_id)` (justifica: historial/listado de asignaciones por conductor para el filtrado DRIVER).
- **FKs (compuestas, mismo tenant, ON DELETE RESTRICT):**
  - `fk_tda_truck (company_id, truck_id) → truck_table(company_id, truck_id)` — físicamente imposible asignar un camión de **otra empresa**.
  - `fk_tda_driver (company_id, driver_id) → driver_table(company_id, driver_id)` — ídem para el conductor.
- **CHECK:** `chk_assignment_dates CHECK (assigned_until IS NULL OR assigned_until > assigned_from)` — operativo (verificado: ERROR 3819).
- **Enums:** ninguno en esta tabla.
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` es el tenant; llega **solo por la ruta** `/api/companies/{companyId}/assignments`, nunca por el body.
- Roles y accesos:
  - **SECRETARY** (o SUPER_ADMIN): administra asignaciones **de su empresa** (crear, finalizar, consultar todo el historial).
  - **PARTNER:** consulta **solo de sus propios camiones** (`truck_table.owner_partner_id` = su `partner_id` resuelto vía `user_id`); sin escritura.
  - **DRIVER:** consulta **solo sus asignaciones** (`driver_id` = su perfil resuelto vía `user_id`); sin escritura; no expone datos de otros conductores.
  - **SUPER_ADMIN:** administración global sobre tenant explícito de la ruta.
- Reglas para SUPER_ADMIN: solo por rutas con `{companyId}` explícito; nunca se concede por estos endpoints.

## Mapeo Java/JPA

- Entidad: `domain/TruckDriverAssignment.java`, PK simple `@Id @GeneratedValue(IDENTITY) Long assignmentId`.
- Fechas → `LocalDateTime` (`assignedFrom` NOT NULL; `assignedUntil` nullable = abierta).
- **Sin `@ManyToOne`** a Truck/Driver: escalares `companyId`, `truckId`, `driverId` (`Long`), patrón de toda la BD `truck_sass` (la pertenencia al tenant se pre-valida en servicio y la FK compuesta es la defensa del motor).
- DTOs seguros: `TruckDriverAssignmentResponse(s)` (assignmentId, companyId, truckId, driverId, assignedFrom, assignedUntil).
- Lock pesimista **sobre las filas de `truck_table` y `driver_table`** (no sobre la asignación): métodos `@Lock(PESSIMISTIC_WRITE)` en los repositories de Truck y Driver, invocados en orden fijo **truck → driver** dentro de la transacción de asignación (evita deadlocks).

## Reglas no representables en SQL

- **Solape temporal (camión y conductor):** dos vigencias son incompatibles si `existing.assigned_from < new_until AND (existing.until IS NULL OR existing.until > new_from)`. Se valida en transacción con lock pesimista de las filas de camión y conductor; sin lock, dos transacciones concurrentes podrían pasar ambas el pre-check (race condition). Decidido: **rechazar con 409** si el camión o el conductor ya tienen una asignación **abierta**; no se auto-cierra la anterior.
- **Camión o conductor de otra empresa:** pre-check con `findByCompanyIdAnd...` → `404` si no existe en el tenant; FK compuesta rechaza con 1452 como defensa extra (→ `409`).
- **Conductor `INACTIVE` o camión `INACTIVE`/`MAINTENANCE`:** no pueden recibir asignaciones nuevas → `409` (SQL no lo impide).
- **Fechas:** `assigned_until <= assigned_from` → `400` (CHECK físico es la defensa, ERROR 3819).
- **Duplicado exacto:** UNIQUE físico `uq_assignment_start` (1062 → `409`) con pre-check para mensaje claro.
- **Filtrado por rol:** PARTNER solo ve asignaciones de sus camiones; DRIVER solo las suyas; la ruta no concede acceso (anti-IDOR).
- **Historial:** no existe DELETE; finalizar solo actualiza `assigned_until`.

## Plan de migración

- Archivos: **ninguno** (la tabla ya existe en `truck_sass` sin diferencias vs baseline; sin Flyway/Liquibase — decisión mantenida de fases 1-3).
- Datos preexistentes: **0 filas** (verificado).
- Riesgo de bloqueo/downtime: no aplica.
- Plan de reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- Asignación con `truck_id` de otra empresa (FK truck) → **ERROR 1452** `fk_tda_truck`.
- Asignación con `driver_id` de otra empresa (FK driver) → **ERROR 1452** `fk_tda_driver`.
- `assigned_until <= assigned_from` → **ERROR 3819** `chk_assignment_dates`.
- Duplicado exacto `(company_id, truck_id, driver_id, assigned_from)` → **ERROR 1062** `uq_assignment_start`.
- Inserción válida con truck y driver del mismo tenant → aceptada.
- Verificación: tras `ROLLBACK`, `COUNT(*) truck_driver_assignment = 0` y sin residuos de datos de prueba.

## Entrega al generador de módulo

- Casos de uso y rutas tentativas (bajo `/api/companies/{companyId}/assignments`):
  - `POST /` — asignar conductor a camión (SECRETARY/SUPER_ADMIN), `201`; valida solapes con lock.
  - `GET /` — historial paginado, con filtros opcionales `?truckId=` y `?driverId=` (SECRETARY/SUPER_ADMIN: todo; PARTNER: solo sus camiones; DRIVER: solo suyas).
  - `GET /{assignmentId}` — detalle por id+tenant + verificación de rol/pertenencia.
  - `PUT /{assignmentId}/end` — finalizar asignación abierta (`assignedUntil` en body), conserva la fila.
- Métodos de repository tenant-scoped requeridos:
  - `TruckDriverAssignmentRepository`: `findByCompanyIdAndAssignmentId`, `findByCompanyId(Pageable)`, `findByCompanyIdAndTruckId(...)`, `findByCompanyIdAndDriverId(...)`, `existsOpenByCompanyIdAndTruckId`, `existsOpenByCompanyIdAndDriverId`, `existsOverlappingByCompanyIdAndTruckId` / `...AndDriverId` (solape con NULL = infinito).
  - `TruckRepository` / `DriverRepository`: `findByCompanyIdAndTruckIdForUpdate` / `findByCompanyIdAndDriverIdForUpdate` con `@Lock(LockModeType.PESSIMISTIC_WRITE)`.
- Decisiones aprobadas: asignación con vigencia abierta existente → **409** (sin auto-cierre); concurrencia → **lock pesimista** truck→driver.
- Fuera de alcance de esta fase: viajes (`journey_table`) y auxiliares.
