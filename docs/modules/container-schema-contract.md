# Contrato de esquema: Container (contenedores de una carga)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `container_table`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE container_table`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 143-159) **sin diferencias** (PK, 2 UNIQUE, índice, FK compuesta, CHECK).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `charge_table` (`fk_container_charge` compuesta). Requiere `charge-schema-contract.md`.

## Objetivo de negocio

Registrar los **contenedores** que integran una carga (físicos, con código, peso y tipo). Reglas de dominio:

- Un contenedor pertenece a **una carga**; la carga aporta contrato y cliente (jerarquía `client → contract → charge → container`).
- Una carga puede contener **múltiples contenedores**.
- `container_weight DECIMAL(12,3)` (**BigDecimal** en Java), **nullable**, `>= 0` por CHECK; el **código es único por empresa**.
- **Conservar el historial comercial**: sin DELETE; ciclo de vida por `container_state`.
- `company_id` = tenant SaaS; integridad cross-tenant por **FK compuesta** `(company_id, charge_id)`.
- Los viajes (`journey_table`) referenciarán `(company_id, container_id)` en una fase posterior — esta fase **no** implementa viajes.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `container_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK simple |
| `company_id` | `BIGINT UNSIGNED` | NO | — | **tenant**; componente de la FK |
| `charge_id` | `BIGINT UNSIGNED` | NO | — | carga dueña (FK compuesta) |
| `container_code` | `VARCHAR(30)` | NO | — | código del contenedor; **único por empresa** |
| `container_weight` | `DECIMAL(12,3)` | SÍ | NULL | peso; `NULL` o `>= 0` (CHECK) |
| `container_description` | `VARCHAR(255)` | SÍ | NULL | descripción |
| `container_type` | `VARCHAR(50)` | SÍ | NULL | tipo (DRY, REEFER, …) |
| `container_state` | `ENUM('AVAILABLE','LOADED','IN_TRANSIT','DELIVERED')` | NO | `AVAILABLE` | estado del contenedor |
| `container_date` | `DATETIME` | NO | `CURRENT_TIMESTAMP` | fecha/hora de registro; lo gestiona el motor |

- **PK:** simple `(container_id)` con AUTO_INCREMENT.
- **UNIQUE:**
  - `uq_container_tenant_id (company_id, container_id)` — defensa de tenant.
  - `uq_container_company_code (company_id, container_code)` — **un código no puede repetirse en la misma empresa** (distintas empresas sí pueden usar el mismo código).
- **Índices:** `idx_container_charge (company_id, charge_id)` — justifica: contenedores de una carga y resolución FK.
- **FK (compuesta, mismo tenant):** `fk_container_charge (company_id, charge_id) → charge_table(company_id, charge_id)` `ON DELETE RESTRICT`.
- **CHECK:** `chk_container_weight CHECK (container_weight IS NULL OR container_weight >= 0)` — operativo (verificado: ERROR 3819).
- **Enums:** `container_state` con valores exactos `AVAILABLE | LOADED | IN_TRANSIT | DELIVERED`.
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` llega **solo por la ruta** `/api/companies/{companyId}/containers`, nunca por el body.
- Roles y accesos:
  - **SECRETARY** (o SUPER_ADMIN): CRUD completo de contenedores **de su empresa**.
  - **PARTNER / DRIVER:** **sin acceso** (403 en URL) — decisión aprobada en esta fase (el DRIVER consultará sus viajes/contenedores en fase 6).
  - **SUPER_ADMIN:** tenant explícito de la ruta; nunca concedido por estos endpoints.
- Contra IDOR: toda lectura es `findByCompanyIdAndContainerId`; el `chargeId` del body se resuelve **dentro del tenant** antes de persistir.

## Mapeo Java/JPA

- Entidad: `domain/Container.java`, PK simple `@Id @GeneratedValue(IDENTITY) Long containerId`.
- `container_weight DECIMAL(12,3)` → `BigDecimal containerWeight` con `@Column(precision = 12, scale = 3)`; **nunca Double**.
- `container_date DATETIME` → `LocalDateTime containerDate` con `insertable = false, updatable = false`.
- `container_state` → `enum ContainerState` + `@Enumerated(EnumType.STRING)` (valores exactos SQL).
- **Sin `@ManyToOne`**: escalares `companyId`, `chargeId` (`Long`), patrón de la BD; la FK compuesta es la defensa del motor.
- DTOs seguros: `ContainerResponse` / `ContainersResponse` (containerId, companyId, chargeId, containerCode, containerWeight, containerDescription, containerType, containerState, containerDate).

## Reglas no representables en SQL

- **Transiciones estrictas de `container_state`** (decisión aprobada; el SQL acepta cualquier valor del enum):
  - `AVAILABLE → LOADED`; `LOADED → IN_TRANSIT`; `IN_TRANSIT → DELIVERED`; **`DELIVERED` terminal**.
  - Retrocesos o saltos ilegales → **409**.
- **Carga de otra empresa:** pre-check `findByCompanyIdAndChargeId` → **404**; FK compuesta 1452 → 409 como defensa.
- **Carga `CANCELLED`:** no admite contenedores nuevos → **409** (regla de cadena aprobada).
- **Código duplicado en la empresa:** pre-check → **409** con mensaje claro; UNIQUE físico → **1062** como defensa.
- **Peso negativo:** Bean Validation (`@DecimalMin("0")`, `@Digits(integer=9, fraction=3)`) → **400**; CHECK físico → 3819. `null` está permitido.
- **Empresa inactiva:** sin altas de contenedores → **409**.

## Plan de migración

- Archivos: **ninguno** (tabla ya existente sin diferencias vs baseline; sin Flyway/Liquibase).
- Datos preexistentes: **0 filas** (verificado).
- Riesgo/downtime: no aplica. Reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- Contenedor de empresa A con `charge_id` de empresa B → **ERROR 1452** `fk_container_charge`.
- `container_weight = -0.500` → **ERROR 3819** `chk_container_weight`.
- Mismo `container_code` dos veces en la misma empresa → **ERROR 1062** `uq_container_company_code`.
- Contenedor válido con peso y carga del mismo tenant → aceptada.
- Tras `ROLLBACK`: sin residuos (`COUNT(*) container_table = 0`).

## Entrega al generador de módulo

- Casos de uso y rutas tentativas (bajo `/api/companies/{companyId}/containers`):
  - `POST /` — crear contenedor (`chargeId`, `containerCode`, `containerWeight` opcional, `containerType`, …) → `201`; valida carga no cancelada y unicidad de código en el tenant.
  - `GET /` — paginado, filtros opcionales `?chargeId=` y `?state=`.
  - `GET /{containerId}` — detalle por id+tenant (404 cross-tenant).
  - `PUT /{containerId}` — escalares sin cambiar FKs ni estado (código incluido → 409 si duplicado).
  - `PUT /{containerId}/status` — transición con máquina estricta → 409 si ilegal.
- Métodos de repository tenant-scoped requeridos: `findByCompanyIdAndContainerId`, `findByCompanyId(Pageable)`, `findByCompanyIdAndChargeId(...)`, `findByCompanyIdAndContainerState(...)`, `findByCompanyIdAndContainerCode(...)` y `existsByCompanyIdAndContainerCode(...)` / `existsByCompanyIdAndContainerCodeNot(...)`.
- Decisiones aprobadas: PARTNER/DRIVER **403**; transiciones estrictas; carga no cancelada para crear contenedor.
- Fuera de alcance: viajes (`journey_table`) — los viajes consumirán esta tabla en fase posterior.
