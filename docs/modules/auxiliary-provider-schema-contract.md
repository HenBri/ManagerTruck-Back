# Contrato de esquema: AuxiliaryProvider (proveedores de auxilio)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `auxiliary_provider`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE auxiliary_provider`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 256-270) **sin diferencias** (PK, UNIQUE, índice, FK).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `company` (`fk_aux_provider_company`). Requiere `company-schema-contract.md`.

## Objetivo de negocio

Registrar los **proveedores ocasionales de auxilio** que la empresa contrata cuando no puede cubrir un viaje con sus recursos habituales (camión/conductor de socio). Reglas de dominio:

- Un proveedor de auxilio es un **tercero ocasional**: `BUSINESS` (otra empresa de transporte) o `INDEPENDENT` (transportista autónomo).
- **Nunca se convierte automáticamente en `partner_table`** ni adquiere cuenta/rol: el auxilio es puntual, no una alianza comercial.
- La identidad y contacto son **del tenant** (`company_id`): no se comparten entre empresas del SaaS.
- Ciclo de vida por `provider_status` (**ACTIVE/INACTIVE**); **sin DELETE** (historial).
- `company_id` = tenant SaaS; FK simple a `company` con defensa de tenant `uq_aux_provider_tenant_id`.
- `journey_table` referenciará `(company_id, provider_id)` en viajes de modalidad `AUXILIARY`.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `provider_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK simple |
| `company_id` | `BIGINT UNSIGNED` | NO | — | **tenant**; componente del UNIQUE de defensa |
| `provider_kind` | `ENUM('BUSINESS','INDEPENDENT')` | NO | — | tipo de proveedor |
| `provider_name` | `VARCHAR(150)` | NO | — | nombre/razón social; lógicamente único por empresa (pre-check backend) |
| `provider_document` | `VARCHAR(30)` | SÍ | NULL | NIT/cédula del tercero |
| `provider_phone` | `VARCHAR(25)` | SÍ | NULL | teléfono |
| `provider_email` | `VARCHAR(255)` | SÍ | NULL | correo |
| `provider_status` | `ENUM('ACTIVE','INACTIVE')` | NO | `ACTIVE` | estado; **no existe columna `state` genérica** |
| `created_at` | `DATETIME` | NO | `CURRENT_TIMESTAMP` | lo gestiona el motor |

- **PK:** simple `(provider_id)` con AUTO_INCREMENT.
- **UNIQUE:** `uq_aux_provider_tenant_id (company_id, provider_id)` — defensa de tenant.
- **Índices:** `idx_aux_provider_name (company_id, provider_name)` — justifica: búsqueda por nombre dentro de la empresa.
- **FK:** `fk_aux_provider_company (company_id) → company(company_id)` `ON DELETE RESTRICT` (simple: `company` no es tenant-compuesta).
- **CHECK:** ninguno.
- **Enums:** `provider_kind` = `BUSINESS | INDEPENDENT`; `provider_status` = `ACTIVE | INACTIVE`.
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` llega **solo por la ruta** `/api/companies/{companyId}/auxiliary-providers`, nunca por el body.
- Roles y accesos:
  - **SECRETARY** (o SUPER_ADMIN): CRUD completo (sin DELETE físico) de proveedores de **su empresa**.
  - **PARTNER / DRIVER:** **sin acceso** (403 en URL) — el catálogo de auxiliares es información administrativa.
  - **SUPER_ADMIN:** tenant explícito de la ruta; nunca concedido por estos endpoints.
- Contra IDOR: toda lectura es `findByCompanyIdAndProviderId`.

## Mapeo Java/JPA

- Entidad: `domain/AuxiliaryProvider.java`, PK simple `@Id @GeneratedValue(IDENTITY) Long providerId`.
- `created_at DATETIME` → `LocalDateTime createdAt` con `insertable = false, updatable = false`.
- `provider_kind` / `provider_status` → enums Java `AuxiliaryProviderKind` / `AuxiliaryProviderStatus` + `@Enumerated(EnumType.STRING)` (valores exactos SQL).
- **Sin `@ManyToOne`**: escalar `companyId` (`Long`), patrón del repo.
- DTOs seguros: `AuxiliaryProviderResponse` / `AuxiliaryProvidersResponse` (providerId, companyId, providerKind, providerName, providerDocument, providerPhone, providerEmail, providerStatus, createdAt).

## Reglas no representables en SQL

- **Nombre duplicado en la empresa:** pre-check backend `existsByCompanyIdAndProviderName` → **409**; no hay UNIQUE físico (varios tercios pueden compartir nombre entre empresas).
- **Proveedor no es socio:** ningún código crea `partner_table`, `user_table` ni roles a partir de un auxiliar.
- **Empresa inactiva:** sin altas de proveedores → **409**.
- **Proveedor INACTIVE:** no admite ser referenciado por viajes AUXILIARY nuevos → **409** (regla de journey).
- **Proveedor cross-tenant:** pre-check `findByCompanyIdAndProviderId` → **404**; FK simple no distingue tenant (defensa real: pre-check + `uq_aux_provider_tenant_id`).

## Plan de migración

- Archivos: **ninguno** (tabla ya existente sin diferencias vs baseline; sin Flyway/Liquibase).
- Datos preexistentes: **0 filas** (verificado).
- Riesgo/downtime: no aplica. Reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- Viaje AUXILIARY con `auxiliary_provider_id` de otra empresa → **ERROR 1452** `fk_journey_provider`.
- Proveedor válido del mismo tenant con viaje AUXILIARY completo → aceptado.
- Tras `ROLLBACK`: sin residuos (`COUNT(*) auxiliary_provider` de prueba = 0).

## Entrega al generador de módulo

- Casos de uso y rutas tentativas (bajo `/api/companies/{companyId}/auxiliary-providers`):
  - `POST /` — crear proveedor (`providerKind`, `providerName` obligatorio, document/phone/email opcionales) → `201`; valida unicidad de nombre en el tenant.
  - `GET /` — paginado, filtros opcionales `?kind=`, `?status=`, `?name=`.
  - `GET /{providerId}` — detalle por id+tenant (404 cross-tenant).
  - `PUT /{providerId}` — escalares sin cambiar estado.
  - `PUT /{providerId}/status` — ACTIVE ↔ INACTIVE.
- Métodos de repository tenant-scoped requeridos: `findByCompanyIdAndProviderId`, `findByCompanyId(Pageable)` (+ filtros kind/status/name), `findByCompanyIdAndProviderName(...)`, `existsByCompanyIdAndProviderName(...)` / `...Not(...)`.
- Decisiones aprobadas: PARTNER/DRIVER **403**; sin DELETE; proveedor nunca socio.
