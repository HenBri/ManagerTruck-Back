# Contrato de esquema: Journey (viajes A → B)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `journey_table`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE journey_table`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 278-327) **sin diferencias** (PK, UNIQUE, 4 índices, 3 FKs compuestas, 3 CHECK).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `container_table`, `truck_driver_assignment`, `auxiliary_provider`. Requiere sus contratos (`container-schema-contract.md`, `truck-driver-assignment-schema-contract.md`, `auxiliary-provider-schema-contract.md`).

## Objetivo de negocio

Gestionar los **viajes** de la empresa: cada viaje es **un recorrido completo de un punto A a un punto B**, sin tramos ni relevos, ejecutado por **un único camión y conductor**. La empresa del SaaS administra el viaje aunque lo ejecute un tercero. Reglas de dominio:

- **Dos modalidades exclusivas** (`execution_type`):
  - `PARTNER`: usa una asignación vigente `TruckDriverAssignment` (camión habitual de un socio + conductor). **Todos los campos auxiliares deben ser NULL.**
  - `AUXILIARY`: usa un proveedor externo (`auxiliary_provider`) con **foto histórica** de placa, nombre y licencia del conductor externo, motivo y monto acordado. `assignment_id` debe ser NULL.
- Un viaje pertenece a la **empresa que administra la carga y el contrato**; el cliente/contrato se derivan por `container → charge → contract → client`.
- Un viaje transporta **un contenedor** (FK obligatoria).
- **No se convierte auxilios en socios**; el proveedor es tercero ocasional.
- Estados `journey_state`: `PLANNED → IN_PROGRESS → COMPLETED` con `CANCELLED` accesible desde `PLANNED` e `IN_PROGRESS`; `COMPLETED` y `CANCELLED` son terminales (máquina validada en backend).
- `agreed_amount DECIMAL(15,2)` (**BigDecimal**) solo para AUXILIARY, `>= 0` por CHECK.
- Sin DELETE: la historia se conserva con estados.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `journey_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK simple |
| `company_id` | `BIGINT UNSIGNED` | NO | — | **tenant** |
| `container_id` | `BIGINT UNSIGNED` | NO | — | contenedor transportado (FK compuesta) |
| `journey_origin` | `VARCHAR(255)` | NO | — | punto A |
| `journey_destination` | `VARCHAR(255)` | NO | — | punto B |
| `journey_start_date` | `DATETIME` | SÍ | NULL | se fija al pasar a `IN_PROGRESS` |
| `journey_end_date` | `DATETIME` | SÍ | NULL | se fija al pasar a `COMPLETED` |
| `journey_description` | `VARCHAR(255)` | SÍ | NULL | descripción |
| `journey_type` | `VARCHAR(50)` | SÍ | NULL | tipo libre (etiqueta) |
| `journey_state` | `ENUM('PLANNED','IN_PROGRESS','COMPLETED','CANCELLED')` | NO | `PLANNED` | estado |
| `execution_type` | `ENUM('PARTNER','AUXILIARY')` | NO | — | modalidad exclusiva |
| `assignment_id` | `BIGINT UNSIGNED` | SÍ | NULL | **solo PARTNER** (FK compuesta) |
| `auxiliary_provider_id` | `BIGINT UNSIGNED` | SÍ | NULL | **solo AUXILIARY** (FK compuesta) |
| `auxiliary_truck_plate` | `VARCHAR(20)` | SÍ | NULL | solo AUXILIARY; foto histórica |
| `auxiliary_driver_name` | `VARCHAR(150)` | SÍ | NULL | solo AUXILIARY; foto histórica |
| `auxiliary_driver_license` | `VARCHAR(40)` | SÍ | NULL | solo AUXILIARY; foto histórica |
| `auxiliary_reason` | `ENUM('TRUCK_SHORTAGE','DRIVER_SHORTAGE','OTHER')` | SÍ | NULL | solo AUXILIARY |
| `agreed_amount` | `DECIMAL(15,2)` | SÍ | NULL | solo AUXILIARY; `NULL` o `>= 0` |
| `created_at` | `DATETIME` | NO | `CURRENT_TIMESTAMP` | lo gestiona el motor |

- **PK:** simple `(journey_id)` con AUTO_INCREMENT.
- **UNIQUE:** `uq_journey_tenant_id (company_id, journey_id)` — defensa de tenant.
- **Índices:**
  - `idx_journey_container (company_id, container_id)` — viajes de un contenedor + FK.
  - `idx_journey_start (company_id, journey_start_date)` — consulta por fecha de inicio.
  - `idx_journey_assignment (company_id, assignment_id)` — viajes de una asignación (PARTNER).
  - `idx_journey_provider (company_id, auxiliary_provider_id)` — viajes de un proveedor (AUXILIARY).
- **FKs (compuestas, mismo tenant):**
  - `fk_journey_container (company_id, container_id) → container_table(company_id, container_id)` `ON DELETE RESTRICT`.
  - `fk_journey_assignment (company_id, assignment_id) → truck_driver_assignment(company_id, assignment_id)` `ON DELETE RESTRICT`.
  - `fk_journey_provider (company_id, auxiliary_provider_id) → auxiliary_provider(company_id, provider_id)` `ON DELETE RESTRICT`.
- **CHECK (operativos, verificados con ERROR 3819):**
  - `chk_journey_execution` — exclusividad de modalidad: `PARTNER` exige `assignment_id` y TODOS los campos auxiliares (incluido `agreed_amount`) NULL; `AUXILIARY` exige `assignment_id` NULL, proveedor, placa y nombre no vacíos y motivo.
  - `chk_journey_amount` — `agreed_amount IS NULL OR >= 0`.
  - `chk_journey_dates` — `journey_end_date >= journey_start_date` cuando ambas existen.
- **Enums:** `journey_state`, `execution_type`, `auxiliary_reason` con valores exactos.
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` llega **solo por la ruta** `/api/companies/{companyId}/journeys`, nunca por el body.
- Roles y accesos (matriz aprobada):
  - **SECRETARY** (o SUPER_ADMIN): administra viajes de su empresa — crear, editar escalares PLANNED, cualquier transición de estado, listar/ver todos.
  - **PARTNER:** **consulta** (GET) solo los viajes cuya asignación use **un camión de su propiedad** (`truck_table.owner_partner_id` = partner del usuario). Sin acceso a AUXILIARY. Sin escritura.
  - **DRIVER:** **consulta** (GET) solo sus viajes PARTNER asignados (`driver_table.user_id` = usuario) y **actualiza el progreso**: `→IN_PROGRESS` y `→COMPLETED` exclusivamente, solo en esos viajes. Sin acceso a AUXILIARY. Sin cancelar, sin crear, sin editar.
  - **SUPER_ADMIN:** operación global con tenant explícito de la ruta; nunca concedido por estos endpoints.
- Contra IDOR: toda lectura es `findByCompanyIdAndJourneyId`; `containerId`/`assignmentId`/`providerId` del body se resuelven **dentro del tenant** (404 si es de otra empresa).

## Mapeo Java/JPA

- Entidad: `domain/Journey.java`, PK simple `@Id @GeneratedValue(IDENTITY) Long journeyId`.
- `journey_start_date` / `journey_end_date` / `created_at` → `LocalDateTime` (`created_at` con `insertable = false, updatable = false`).
- `agreed_amount DECIMAL(15,2)` → `BigDecimal agreedAmount` con `@Column(precision = 15, scale = 2)`; **nunca Double**.
- `journey_state` / `execution_type` / `auxiliary_reason` → enums Java (`JourneyState`, `JourneyExecutionType`, `AuxiliaryReason`) + `@Enumerated(EnumType.STRING)`.
- **Sin `@ManyToOne`**: escalares `companyId`, `containerId`, `assignmentId`, `auxiliaryProviderId` (`Long`), patrón del repo; las FKs compuestas son la defensa del motor.
- DTOs seguros: `JourneyResponse` / `JourneysResponse` (todos los campos de negocio; sin entidades JPA).

## Reglas no representables en SQL

- **Máquina de estados estricta** (decisión aprobada; el SQL acepta cualquier valor del enum):
  - `PLANNED → IN_PROGRESS` (fija `journey_start_date` si es NULL).
  - `PLANNED → CANCELLED`; `IN_PROGRESS → CANCELLED`.
  - `IN_PROGRESS → COMPLETED` (fija `journey_end_date`; exige `end >= start`).
  - `COMPLETED` / `CANCELLED` terminales. Retrocesos/saltos → **409**; valor inválido → **400**.
- **Exclusividad de modalidad en creación** (refuerzo backend del CHECK): campos PARTNER y AUXILIARY mezclados → **400**.
- **Asignación PARTNER válida:** misma empresa (404), vigencia (`assigned_from <= inicio` y `assigned_until` null o futuro → 409 si expirada), camión y conductor `ACTIVE` (409).
- **Proveedor AUXILIARY válido:** misma empresa (404) y `ACTIVE` (409); placa y nombre obligatorios no vacíos (400); motivo obligatorio (400).
- **Contenedor:** mismo tenant (404); su carga no `CANCELLED` (409); contenedor no `DELIVERED` (409). El viaje **no muta** `container_state` (decisión aprobada: sin acople).
- **Solapamiento** (decisión aprobada, sin constraint SQL posible): máx. **1 viaje activo** (`PLANNED`/`IN_PROGRESS`) por `assignment_id` (PARTNER) y por **placa de auxilio normalizada** (AUXILIARY) dentro de la empresa → **409**. Chequeo transaccional en backend.
- **Empresa inactiva:** sin altas de viajes → **409**.
- **Viaje cross-tenant:** pre-check `findByCompanyIdAndJourneyId` → **404**.

## Plan de migración

- Archivos: **ninguno** (tabla ya existente sin diferencias vs baseline; sin Flyway/Liquibase).
- Datos preexistentes: **0 filas** (verificado).
- Riesgo/downtime: no aplica. Reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`) — 9/9

- N1 PARTNER con placa/nombre auxiliares → **3819** `chk_journey_execution`.
- N2 AUXILIARY con `assignment_id` → **3819** `chk_journey_execution`.
- N3 `agreed_amount = -0.01` → **3819** `chk_journey_amount`.
- N4 `end_date < start_date` → **3819** `chk_journey_dates`.
- N5 `container_id` de otra empresa → **1452** `fk_journey_container`.
- N6 `assignment_id` de otra empresa → **1452** `fk_journey_assignment`.
- N7 `auxiliary_provider_id` de otra empresa → **1452** `fk_journey_provider`.
- N8 viajes PARTNER y AUXILIARY válidos → aceptados.
- N9 AUXILIARY sin placa → **3819** `chk_journey_execution`.
- Tras `ROLLBACK`: sin residuos.

## Entrega al generador de módulo

- Casos de uso y rutas tentativas (bajo `/api/companies/{companyId}/journeys`):
  - `POST /` — crear viaje PLANNED (`containerId`, origen, destino, y según modalidad: `assignmentId` **o** datos auxiliares) → `201`; valida exclusividad, FKs tenant, vigencia, solapamiento.
  - `GET /` — paginado, filtros `?containerId=`, `?state=`, `?executionType=`; alcance filtrado por rol (PARTNER por camiones propios, DRIVER por sus asignaciones).
  - `GET /{journeyId}` — detalle con el mismo alcance por rol (404 cross-tenant / 403 fuera de alcance).
  - `PUT /{journeyId}` — escalares (origen, destino, descripción, `journeyType`) solo en PLANNED; no cambia modalidad ni FKs.
  - `PUT /{journeyId}/status` — transición con máquina; SECRETARY/SUPER_ADMIN cualquier transición legal; DRIVER solo `→IN_PROGRESS`/`→COMPLETED` en sus viajes PARTNER.
- Métodos de repository tenant-scoped requeridos: `findByCompanyIdAndJourneyId`, `findByCompanyId(Pageable)` (+ filtros), `existsActiveByCompanyIdAndAssignmentId(...)`, `existsActiveByCompanyIdAndAuxiliaryTruckPlate(...)`, consultas JPQL con subconsultas para alcance PARTNER (por owner) y DRIVER (por driver user).
- Decisiones aprobadas: máquina de estados; DRIVER start/complete propios; cancelación desde IN_PROGRESS; sin acople de `container_state`; solapamiento por recurso activo único.
