# Contrato de esquema: Truck (camiones de socios)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `truck_table`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE truck_table`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 212-231) **sin diferencias** (PK, UNIQUEs, índice, FK compuesta, CHECK, DEFAULTs).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `partner_table` (FK compuesta `fk_truck_owner`). Requiere `partner-schema-contract.md`.

## Objetivo de negocio

Registra los **camiones habituales** de una empresa de transporte. Reglas de dominio innegociables:

- La empresa **no es propietaria** de camiones: `owner_partner_id` es **obligatorio** (`NOT NULL`).
- **Todos** los camiones habituales pertenecen a **socios**; un socio puede ser propietario de **múltiples** camiones.
- Los camiones de **auxilio externo** no pertenecen a Partner y se gestionan en la **fase 6** (no se registran aquí).
- La asignación de conductores a camiones (`truck_driver_assignment`) queda **fuera de esta iteración**.

`company_id` = empresa que **administra** el registro del camión (tenant SaaS), no la titularidad.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `truck_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK simple |
| `company_id` | `BIGINT UNSIGNED` | NO | — | **tenant**; componente de la FK del dueño |
| `owner_partner_id` | `BIGINT UNSIGNED` | **NO** | — | socio propietario; **nunca la empresa** |
| `truck_board` | `VARCHAR(20)` | NO | — | placa; **UNIQUE por tenant** |
| `truck_brand` | `VARCHAR(50)` | SÍ | NULL | |
| `truck_model` | `VARCHAR(50)` | SÍ | NULL | |
| `truck_chassis` | `VARCHAR(50)` | SÍ | NULL | |
| `truck_color` | `VARCHAR(30)` | SÍ | NULL | |
| `truck_year` | `SMALLINT UNSIGNED` | SÍ | NULL | → `Integer` en Java |
| `truck_capacity` | `DECIMAL(12,3)` | SÍ | NULL | → `BigDecimal(12,3)`; **>= 0** (CHECK) |
| `truck_state` | `ENUM('ACTIVE','MAINTENANCE','INACTIVE')` | NO | `ACTIVE` | soft delete permitido por esquema |
| `truck_create_date` | `DATETIME` | NO | `CURRENT_TIMESTAMP` | solo lectura en JPA |

- **PK:** simple `(truck_id)` con AUTO_INCREMENT.
- **UNIQUE:**
  - `uq_truck_tenant_id (company_id, truck_id)`.
  - `uq_truck_company_board (company_id, truck_board)` — **una placa no puede repetirse en el mismo tenant** (placa de otra empresa con igual texto sí está permitida).
- **Índices:** `idx_truck_owner (company_id, owner_partner_id)` (justifica: listado de camiones por socio para el filtrado PARTNER).
- **FK:** `fk_truck_owner (company_id, owner_partner_id) → partner_table(company_id, partner_id)` **ON DELETE RESTRICT** — **FK compuesta**: fisicamente imposible vincular un camión a un socio de **otra empresa**.
- **CHECK:** `chk_truck_capacity CHECK (truck_capacity IS NULL OR truck_capacity >= 0)` — operativo (verificado: ERROR 3819).
- **Enums:** `truck_state = ACTIVE | MAINTENANCE | INACTIVE`.
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` es el tenant; llega **solo por la ruta** `/api/companies/{companyId}/trucks`, nunca por el body.
- Roles y accesos:
  - **SECRETARY** (o SUPER_ADMIN): CRUD completo sobre los camiones **de su empresa**.
  - **PARTNER:** consulta **solo de sus propios camiones** (`owner_partner_id` = su `partner_id` resuelto vía `user_id`); sin escritura.
  - **DRIVER:** consulta de camiones autorizados (su perfil/vínculo); sin escritura; por defecto sin acceso a camiones de otros socios.
  - **SUPER_ADMIN:** administración global sobre tenant explícito de la ruta.
- Reglas para SUPER_ADMIN: solo por rutas con `{companyId}` explícito; nunca se concede por estos endpoints.

## Mapeo Java/JPA

- Entidad: `domain/Truck.java`, PK simple `@Id @GeneratedValue(IDENTITY) Long truckId`.
- `truck_state` → enum `TruckStatus` (`@Enumerated(EnumType.STRING)`); `truck_capacity` → `BigDecimal` con `@Column(precision=12, scale=3)`; `truck_year` → `Integer`; `truck_create_date` → `LocalDateTime` con `insertable=false, updatable=false` (DEFAULT de BD).
- Asociación al dueño: **sin `@ManyToOne`**; se manejan escalares `companyId` y `ownerPartnerId` (`Long`), patrón de toda la BD `truck_sass` (la validación de que el socio pertenece al tenant se hace en la capa de servicio con `findByCompanyIdAndPartnerId` y la FK compuesta es la defensa del motor). Así se evita *repeated column* entre el escalar y la asociación.
- DTOs seguros: `TruckResponse(s)` (truckId, companyId, ownerPartnerId, truckBoard, marca, modelo, chasis, color, year, capacity, state, createDate).

## Reglas no representables en SQL

- **No registrar un camión sin socio propietario:** el `NOT NULL` lo garantiza a nivel de columna, pero la validación de API previa da `400` con mensaje claro si `ownerPartnerId` no llega.
- **Socio de otra empresa:** el pre-check de aplicación busca el partner con `findByCompanyIdAndPartnerId(companyId, ownerPartnerId)` → `404` si no existe **en ese tenant** (y la FK compuesta rechaza con 1452 como defensa extra, que se mapea a `409`).
- **Socio `INACTIVE`:** no puede recibir camiones nuevos → `409` (SQL no lo impide).
- **Placa duplicada en el tenant:** UNIQUE físico (1062 → `409`) con pre-check para mensaje claro.
- **`truck_capacity >= 0`:** CHECK físico (3819) + Bean Validation `@DecimalMin("0")` previo → `400`.
- **Filtrado PARTNER:** un partner solo lista `owner_partner_id` propio; la ruta no concede acceso (anti-IDOR).
- Camiones de auxilio externo: **no existen en esta tabla** hasta la fase 6.

## Plan de migración

- Archivos: **ninguno** (sin DDL; sin Flyway/Liquibase — decisión mantenida de fases 1-2).
- Datos preexistentes: **0 filas** (verificado en `truck_sass`).
- Riesgo de bloqueo/downtime: no aplica.
- Plan de reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- Camión con `owner_partner_id` inexistente → **ERROR 1452** FK `fk_truck_owner`.
- Camión de empresa A con `owner_partner_id` de empresa B (cross-tenant) → **ERROR 1452** FK compuesta `fk_truck_owner`.
- Camión sin `owner_partner_id` → **ERROR 1048** `Column 'owner_partner_id' cannot be null`.
- Placa duplicada en el mismo tenant → **ERROR 1062** `uq_truck_company_board`.
- `truck_capacity = -1` → **ERROR 3819** `Check constraint 'chk_truck_capacity' is violated`.
- Inserción válida con socio del mismo tenant → aceptada.
- Verificación: tras `ROLLBACK`, `COUNT(*) truck_table = 0`.

## Entrega al generador de módulo

- Casos de uso y rutas tentativas (bajo `/api/companies/{companyId}/trucks`):
  - `GET /` — listar paginado (SECRETARY/SUPER_ADMIN: todos; PARTNER: solo suyos).
  - `GET /{truckId}` — detalle por id+tenant + verificación de rol/pertenencia.
  - `POST /` — crear (SECRETARY/SUPER_ADMIN), `201`.
  - `PUT /{truckId}` — actualizar escalares sin cambiar FKs.
  - `PUT /{truckId}/status` — cambiar `truck_state`.
- Métodos de repository tenant-scoped requeridos: `findByCompanyIdAndTruckId`, `findByCompanyId(Pageable)`, `findByCompanyIdAndOwnerPartnerId`, `existsByCompanyIdAndTruckBoard`.
- Decisiones todavía pendientes: asignación de conductores (`truck_driver_assignment`) y viajes → fuera de alcance de la fase 3 (fase 6).
