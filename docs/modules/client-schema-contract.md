# Contrato de esquema: Client (clientes de la empresa gestora)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `client`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE client`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 94-108) **sin diferencias** (PK, UNIQUE, índice, FK, nulabilidad).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `company` (`fk_client_company`). Requiere `company-schema-contract.md`.

## Objetivo de negocio

Registrar los **clientes** de cada empresa de transporte (tenant) para soportar la gestión comercial: contratos, cargas y contenedores se derivan de este cliente. Reglas de dominio:

- Cada cliente **pertenece a una única empresa gestora** (`company_id` = tenant); los clientes **no se comparten** entre empresas.
- Un cliente puede tener **múltiples contratos** (`contract_table.client_id`).
- **Conservar el historial comercial**: no existe DELETE; `client_state` enciende/apaga la vigencia del cliente.
- `company_id` representa al **tenant SaaS** (empresa que administra), no propiedad de activos.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `client_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK simple |
| `company_id` | `BIGINT UNSIGNED` | NO | — | **tenant**; FK a `company` |
| `client_name` | `VARCHAR(150)` | NO | — | nombre comercial |
| `client_ci` | `VARCHAR(30)` | SÍ | NULL | cédula/identificación |
| `client_email` | `VARCHAR(255)` | SÍ | NULL | correo de contacto |
| `client_phone` | `VARCHAR(25)` | SÍ | NULL | teléfono |
| `client_address` | `VARCHAR(255)` | SÍ | NULL | dirección |
| `client_state` | `TINYINT(1)` | NO | `1` | **1 = activo, 0 = inactivo** (binario) |
| `client_create_date` | `DATETIME` | NO | `CURRENT_TIMESTAMP` | alta; lo gestiona el motor |

- **PK:** simple `(client_id)` con AUTO_INCREMENT.
- **UNIQUE:** `uq_client_tenant_id (company_id, client_id)` — defensa de tenant.
- **Índices:** `idx_client_company_name (company_id, client_name)` — justifica el listado paginado por empresa ordenado/filtrado por nombre. **No es UNIQUE**: nombres duplicados en la misma empresa están permitidos por SQL (decisión de diseño: no se añade unicidad de nombre).
- **FK:** `fk_client_company (company_id) → company(company_id)` `ON DELETE RESTRICT`.
- **CHECK:** ninguno.
- **Enums:** ninguno (`client_state` es booleano físico).
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` es el tenant; llega **solo por la ruta** `/api/companies/{companyId}/clients`, nunca por el body.
- Roles y accesos:
  - **SECRETARY** (o SUPER_ADMIN): CRUD completo de clientes **de su empresa**.
  - **PARTNER / DRIVER:** **sin acceso** (403 en URL) — ninguna consulta comercial autorizada en esta fase (decisión aprobada).
  - **SUPER_ADMIN:** administración global sobre tenant explícito de la ruta; nunca se concede por estos endpoints.
- Contra IDOR: lectura por `findByIdAndCompanyId`; la ruta no concede acceso.

## Mapeo Java/JPA

- Entidad: `domain/Client.java`, PK simple `@Id @GeneratedValue(IDENTITY) Long clientId`.
- `client_state TINYINT(1)` → `Boolean clientState` (semántica binaria exacta).
- `client_create_date` → `LocalDateTime clientCreateDate` con `insertable = false, updatable = false` (patrón `Truck.truckCreateDate`).
- **Sin `@ManyToOne`** a Company: escalares `companyId` (`Long`), patrón de toda la BD.
- DTOs seguros: `ClientResponse` / `ClientsResponse` (clientId, companyId, clientName, clientCi, clientEmail, clientPhone, clientAddress, clientState, clientCreateDate); nunca se serializa la entidad.

## Reglas no representables en SQL

- **Cliente inactivo:** no puede ser padre de un contrato nuevo → **409** en `POST /contracts` si `client_state = 0` (el SQL no lo impide).
- **Empresa inactiva:** sin altas de clientes → **409**.
- **Alta/actualización:** nombres/longitudes validados por Bean Validation (400); `client_state` solo cambia por `PUT .../status` (no por `PUT /{id}`).
- **Sin DELETE:** desactivar conserva el historial comercial.
- **Filtro de listado:** `?active=true|false` opcional sobre `client_state`.

## Plan de migración

- Archivos: **ninguno** (la tabla ya existe en `truck_sass` sin diferencias vs baseline; sin Flyway/Liquibase — decisión mantenida de fases 1-4).
- Datos preexistentes: **0 filas** (verificado con `COUNT(*)`).
- Riesgo de bloqueo/downtime: no aplica. Plan de reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- Contrato con `client_id` de otra empresa → **ERROR 1452** `fk_contract_client` (protege a `client` desde su hijo).
- Cadena válida del mismo tenant (client → contract → charge → container) → aceptada.
- Verificación: tras `ROLLBACK`, `COUNT(*) company/client = 0` sin residuos de las empresas de prueba.

## Entrega al generador de módulo

- Casos de uso y rutas tentativas (bajo `/api/companies/{companyId}/clients`):
  - `POST /` — crear cliente (SECRETARY/SUPER_ADMIN), `201`.
  - `GET /` — paginado por empresa, filtro opcional `?active=`.
  - `GET /{clientId}` — detalle por id+tenant (404 si es de otra empresa).
  - `PUT /{clientId}` — actualiza escalares (sin estado).
  - `PUT /{clientId}/status` — activa/desactiva (`clientState` en body).
- Métodos de repository tenant-scoped requeridos: `findByCompanyIdAndClientId`, `findByCompanyId(Pageable)`, `findByCompanyIdAndClientState(...)`, `existsByCompanyIdAndClientId`.
- Decisiones aprobadas: PARTNER/DRIVER **sin acceso (403)**; sin DELETE (historial).
- Fuera de alcance de esta fase: viajes (`journey_table`).
