# Contrato de esquema: Contract (contratos comerciales de un cliente)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `contract_table`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE contract_table`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 111-125) **sin diferencias** (PK, UNIQUE, índice, FK compuesta, CHECK).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `client` (`fk_contract_client` compuesta). Requiere `client-schema-contract.md`.

## Objetivo de negocio

Registrar los **contratos** que una empresa gestora celebra con sus clientes. Reglas de dominio:

- Un contrato pertenece **directamente a un cliente** (`client_id`), de la **misma empresa** (`company_id` = tenant).
- Un cliente puede tener **múltiples contratos**; un contrato puede incluir **múltiples cargas** (`charge_table.contract_id`).
- `contract_amount` = monto comercial en `DECIMAL(15,2)` (**BigDecimal** en Java, nunca `Double`); `CHECK >= 0`.
- **Conservar el historial comercial**: sin DELETE; el ciclo de vida se expresa con `contract_state`.
- `company_id` = tenant SaaS; la integridad cross-tenant la garantiza la **FK compuesta** `(company_id, client_id)`.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `contract_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK simple |
| `company_id` | `BIGINT UNSIGNED` | NO | — | **tenant**; componente de la FK |
| `client_id` | `BIGINT UNSIGNED` | NO | — | cliente dueño del contrato (FK compuesta) |
| `contract_amount` | `DECIMAL(15,2)` | NO | `0` | monto comercial; `>= 0` por CHECK |
| `contract_date` | `DATE` | NO | — | fecha del contrato (**no** tiene DEFAULT; el cliente la envía) |
| `contract_description` | `VARCHAR(255)` | SÍ | NULL | objeto del contrato |
| `contract_type` | `VARCHAR(50)` | SÍ | NULL | tipo libre de contrato |
| `contract_state` | `ENUM('DRAFT','ACTIVE','COMPLETED','CANCELLED')` | NO | `DRAFT` | estado del contrato |

- **PK:** simple `(contract_id)` con AUTO_INCREMENT.
- **UNIQUE:** `uq_contract_tenant_id (company_id, contract_id)` — defensa de tenant.
- **Índices:** `idx_contract_client (company_id, client_id)` — justifica: listar contratos de un cliente y resolución FK.
- **FK (compuesta, mismo tenant):** `fk_contract_client (company_id, client_id) → client(company_id, client_id)` `ON DELETE RESTRICT` — físicamente imposible relacionar un cliente de **otra empresa**.
- **CHECK:** `chk_contract_amount CHECK (contract_amount >= 0)` — operativo (verificado: ERROR 3819).
- **Enums:** `contract_state` con valores exactos `DRAFT | ACTIVE | COMPLETED | CANCELLED`.
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` llega **solo por la ruta** `/api/companies/{companyId}/contracts`, nunca por el body.
- Roles y accesos:
  - **SECRETARY** (o SUPER_ADMIN): CRUD completo de contratos **de su empresa**.
  - **PARTNER / DRIVER:** **sin acceso** (403 en URL) — decisión aprobada en esta fase.
  - **SUPER_ADMIN:** tenant explícito de la ruta; nunca concedido por estos endpoints.
- Contra IDOR: toda lectura es `findByCompanyIdAndContractId`; el `client_id` del body se resuelve **dentro del tenant** antes de persistir.

## Mapeo Java/JPA

- Entidad: `domain/Contract.java`, PK simple `@Id @GeneratedValue(IDENTITY) Long contractId`.
- `contract_amount DECIMAL(15,2)` → `BigDecimal contractAmount` con `@Column(precision = 15, scale = 2)`; **nunca Double**.
- `contract_date DATE` → `LocalDate contractDate`.
- `contract_state` → `enum ContractState` + `@Enumerated(EnumType.STRING)` (valores exactos SQL).
- **Sin `@ManyToOne`**: escalares `companyId`, `clientId` (`Long`), patrón de la BD; la FK compuesta es la defensa del motor.
- DTOs seguros: `ContractResponse` / `ContractsResponse` (contractId, companyId, clientId, contractAmount, contractDate, contractDescription, contractType, contractState).

## Reglas no representables en SQL

- **Transiciones estrictas de `contract_state`** (decisión aprobada; el SQL acepta cualquier valor del enum):
  - `DRAFT → ACTIVE | CANCELLED`; `ACTIVE → COMPLETED | CANCELLED`; `COMPLETED` y `CANCELLED` son **terminales**.
  - Retrocesos o saltos ilegales → **409**.
- **Cliente de otra empresa:** pre-check `clientRepository.findByCompanyIdAndClientId` → **404**; FK compuesta rechaza con 1452 → 409 como defensa.
- **Cliente inactivo** (`client_state = 0`): no admite contratos nuevos → **409**.
- **Empresa inactiva:** sin altas de contratos → **409**.
- **Monto negativo:** Bean Validation (`@DecimalMin("0")`) → **400**; CHECK físico = defensa (3819).
- **Carga nueva sobre contrato:** requiere `contract_state = ACTIVE` → **409** si `DRAFT/COMPLETED/CANCELLED` (regla de cadena aprobada).

## Plan de migración

- Archivos: **ninguno** (tabla ya existente sin diferencias vs baseline; sin Flyway/Liquibase).
- Datos preexistentes: **0 filas** (verificado).
- Riesgo/downtime: no aplica. Reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- Contrato de empresa A con `client_id` de empresa B → **ERROR 1452** `fk_contract_client`.
- `contract_amount = -0.01` → **ERROR 3819** `chk_contract_amount`.
- Cadena válida del mismo tenant → aceptada.
- Tras `ROLLBACK`: sin residuos (`COUNT(*) contract_table = 0`).

## Entrega al generador de módulo

- Casos de uso y rutas tentativas (bajo `/api/companies/{companyId}/contracts`):
  - `POST /` — crear contrato (`clientId`, `contractAmount`, `contractDate`, …) → `201`; valida cliente ACTIVE en el tenant.
  - `GET /` — paginado, filtros opcionales `?clientId=` y `?state=`.
  - `GET /{contractId}` — detalle por id+tenant (404 cross-tenant).
  - `PUT /{contractId}` — escalares sin cambiar FKs ni estado.
  - `PUT /{contractId}/status` — transición de estado con máquina estricta → 409 si ilegal.
- Métodos de repository tenant-scoped requeridos: `findByCompanyIdAndContractId`, `findByCompanyId(Pageable)`, `findByCompanyIdAndClientId(...)`, `findByCompanyIdAndContractState(...)`, `findByCompanyIdAndClientIdAndContractState(...)`.
- Decisiones aprobadas: PARTNER/DRIVER **403**; transiciones estrictas; cliente debe estar ACTIVE para crear contrato.
- Fuera de alcance: viajes (`journey_table`).
