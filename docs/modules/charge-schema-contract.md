# Contrato de esquema: Charge (cargas de un contrato)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `charge_table`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE charge_table`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 128-140) **sin diferencias** (PK, UNIQUE, índice, FK compuesta).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `contract_table` (`fk_charge_contract` compuesta). Requiere `contract-schema-contract.md`.

## Objetivo de negocio

Registrar las **cargas** (envíos/órdenes de transporte) incluidas en un contrato. Reglas de dominio:

- Una carga pertenece a **un contrato**; el **cliente se obtiene vía contrato** — el esquema **NO** incluye `client_id` en esta tabla y **no debe duplicarse** (decisión explícita: la jerarquía es `client → contract → charge`).
- Un contrato puede incluir **múltiples cargas**; una carga puede contener **múltiples contenedores** (`container_table.charge_id`).
- **Conservar el historial comercial**: sin DELETE; ciclo de vida por `charge_state`.
- `company_id` = tenant SaaS; integridad cross-tenant por **FK compuesta** `(company_id, contract_id)`.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `charge_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK simple |
| `company_id` | `BIGINT UNSIGNED` | NO | — | **tenant**; componente de la FK |
| `contract_id` | `BIGINT UNSIGNED` | NO | — | contrato dueño (FK compuesta) |
| `charge_description` | `VARCHAR(255)` | SÍ | NULL | descripción de la carga |
| `charge_date` | `DATETIME` | NO | `CURRENT_TIMESTAMP` | fecha/hora de registro; lo gestiona el motor |
| `charge_type` | `VARCHAR(100)` | SÍ | NULL | tipo libre de carga |
| `charge_state` | `ENUM('PENDING','IN_TRANSIT','DELIVERED','CANCELLED')` | NO | `PENDING` | estado de la carga |

- **PK:** simple `(charge_id)` con AUTO_INCREMENT.
- **UNIQUE:** `uq_charge_tenant_id (company_id, charge_id)` — defensa de tenant.
- **Índices:** `idx_charge_contract (company_id, contract_id)` — justifica: listar cargas de un contrato y resolución FK.
- **FK (compuesta, mismo tenant):** `fk_charge_contract (company_id, contract_id) → contract_table(company_id, contract_id)` `ON DELETE RESTRICT`.
- **CHECK:** ninguno (no hay columnas numéricas en esta tabla).
- **Enums:** `charge_state` con valores exactos `PENDING | IN_TRANSIT | DELIVERED | CANCELLED`.
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` llega **solo por la ruta** `/api/companies/{companyId}/charges`, nunca por el body.
- Roles y accesos:
  - **SECRETARY** (o SUPER_ADMIN): CRUD completo de cargas **de su empresa**.
  - **PARTNER / DRIVER:** **sin acceso** (403 en URL) — decisión aprobada en esta fase (consultas de viaje vendrán en fase 6).
  - **SUPER_ADMIN:** tenant explícito de la ruta; nunca concedido por estos endpoints.
- Contra IDOR: toda lectura es `findByCompanyIdAndChargeId`; el `contractId` del body se resuelve **dentro del tenant** antes de persistir.

## Mapeo Java/JPA

- Entidad: `domain/Charge.java`, PK simple `@Id @GeneratedValue(IDENTITY) Long chargeId`.
- `charge_date DATETIME` → `LocalDateTime chargeDate` con `insertable = false, updatable = false` (DEFAULT del motor, patrón `Truck.truckCreateDate`).
- `charge_state` → `enum ChargeState` + `@Enumerated(EnumType.STRING)` (valores exactos SQL).
- **Sin `@ManyToOne`** y **sin `clientId`**: escalares `companyId`, `contractId` (`Long`); el cliente se deriva del contrato en consultas (join `(company_id, contract_id)` si hace falta).
- DTOs seguros: `ChargeResponse` / `ChargesResponse` (chargeId, companyId, contractId, chargeDescription, chargeDate, chargeType, chargeState).

## Reglas no representables en SQL

- **Transiciones estrictas de `charge_state`** (decisión aprobada; el SQL acepta cualquier valor del enum):
  - `PENDING → IN_TRANSIT | CANCELLED`; `IN_TRANSIT → DELIVERED | CANCELLED`; `DELIVERED` y `CANCELLED` son **terminales**.
  - Retrocesos o saltos ilegales → **409**.
- **Contrato de otra empresa:** pre-check `findByCompanyIdAndContractId` → **404**; FK compuesta 1452 → 409 como defensa.
- **Contrato no ACTIVE** (`DRAFT/COMPLETED/CANCELLED`): no admite cargas nuevas → **409** (regla de cadena aprobada).
- **Empresa inactiva:** sin altas de cargas → **409**.
- **Contenedor nuevo sobre carga:** la carga **no puede estar `CANCELLED`** → **409** (regla de cadena aprobada).
- **Sin `client_id` duplicado:** cualquier payload que intente llevar clientId se ignora/omite; la relación es contract → client.

## Plan de migración

- Archivos: **ninguno** (tabla ya existente sin diferencias vs baseline; sin Flyway/Liquibase).
- Datos preexistentes: **0 filas** (verificado).
- Riesgo/downtime: no aplica. Reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- Carga de empresa A con `contract_id` de empresa B → **ERROR 1452** `fk_charge_contract`.
- Cadena válida del mismo tenant → aceptada.
- Tras `ROLLBACK`: sin residuos (`COUNT(*) charge_table = 0`).

## Entrega al generador de módulo

- Casos de uso y rutas tentativas (bajo `/api/companies/{companyId}/charges`):
  - `POST /` — crear carga (`contractId`, `chargeDescription`, `chargeType`) → `201`; valida contrato ACTIVE en el tenant.
  - `GET /` — paginado, filtros opcionales `?contractId=` y `?state=`.
  - `GET /{chargeId}` — detalle por id+tenant (404 cross-tenant).
  - `PUT /{chargeId}` — escalares sin cambiar FKs ni estado.
  - `PUT /{chargeId}/status` — transición con máquina estricta → 409 si ilegal.
- Métodos de repository tenant-scoped requeridos: `findByCompanyIdAndChargeId`, `findByCompanyId(Pageable)`, `findByCompanyIdAndContractId(...)`, `findByCompanyIdAndChargeState(...)`, `findByCompanyIdAndContractIdAndChargeState(...)`.
- Decisiones aprobadas: PARTNER/DRIVER **403**; transiciones estrictas; contrato debe estar ACTIVE para crear carga; **no** restablecer `charge.client_id`.
- Fuera de alcance: viajes (`journey_table`).
