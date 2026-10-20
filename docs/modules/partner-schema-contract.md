# Contrato de esquema: Partner (socios/propietarios)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `partner_table`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE partner_table`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 163-178) **sin diferencias** (PK, UNIQUEs, índices, FKs, DEFAULTs).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `company` (FK `fk_partner_company`), `company_user` (FK compuesta `fk_partner_membership`). Requiere `company-schema-contract.md` y `company-user-schema-contract.md`.

## Objetivo de negocio

Registra a los **socios propietarios de camiones** de una empresa de transporte. La empresa **no** es propietaria de camiones: todo camión habitual debe apuntar a un socio de esta tabla. Un socio puede ser persona física (`PERSON`) o negocio (`BUSINESS`), puede ser propietario de múltiples camiones y **puede ser simultáneamente conductor** (`driver_table.person_partner_id`). La **cuenta de acceso es opcional** (`user_id NULL`): Partner es un **perfil del negocio**, no una cuenta de usuario independiente. Los auxilios externos (fase 6) **no** se registran aquí.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `partner_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK simple; ID de dominio |
| `company_id` | `BIGINT UNSIGNED` | NO | — | **tenant**; no implica titularidad |
| `user_id` | `BIGINT UNSIGNED` | SÍ | NULL | cuenta opcional; FK compuesta a membresía |
| `partner_type` | `ENUM('PERSON','BUSINESS')` | NO | — | tipo de socio |
| `partner_name` | `VARCHAR(150)` | NO | — | nombre/razón social |
| `partner_document` | `VARCHAR(30)` | SÍ | NULL | cédula/NIT |
| `partner_phone` | `VARCHAR(25)` | SÍ | NULL | |
| `partner_state` | `ENUM('ACTIVE','INACTIVE')` | NO | `ACTIVE` | soft delete permitido por esquema |

- **PK:** simple `(partner_id)` con AUTO_INCREMENT.
- **UNIQUE:** `uq_partner_tenant_id (company_id, partner_id)` — unicidad por tenant del par (empresa, socio).
- **Índices:** `idx_partner_membership (company_id, user_id)` (justifica: resolver el socio del usuario actual para filtrado PARTNER).
- **FKs:**
  - `fk_partner_company (company_id) → company(company_id)` **ON DELETE RESTRICT**.
  - `fk_partner_membership (company_id, user_id) → company_user(company_id, user_id)` **ON DELETE RESTRICT** — el vínculo de cuenta exige membresía **en la misma empresa**.
- **CHECK:** ninguno.
- **Enums:** `partner_type = PERSON | BUSINESS`; `partner_state = ACTIVE | INACTIVE`.
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` es el tenant; llega **solo por la ruta** `/api/companies/{companyId}/partners`, nunca por el body.
- Roles y accesos:
  - **SECRETARY** (o SUPER_ADMIN): CRUD completo sobre los partners **de su empresa** (`GET/POST/PUT`, `PUT /{partnerId}/status`).
  - **PARTNER:** lectura de **su propio perfil** (`user_id` = cuenta autenticada) y de sus datos autorizados; sin escritura sobre otros socios.
  - **DRIVER:** lectura de su propio perfil si está vinculado como partner; sin escritura.
  - **SUPER_ADMIN:** administración global sobre tenant explícito de la ruta.
- Reglas para SUPER_ADMIN: solo por rutas con `{companyId}` explícito; nunca se concede por estos endpoints.

## Mapeo Java/JPA

- Entidad: `domain/Partner.java`, PK simple `@Id @GeneratedValue(IDENTITY) Long partnerId`.
- `partner_type`/`partner_state` → enums `PartnerType`/`PartnerStatus` con `@Enumerated(EnumType.STRING)` (valores exactos SQL).
- `companyId` como columna escalar (`@Column(name="company_id")`) — patrón de `CompanyUser.id.companyId` en escritura directa; **sin** `@ManyToOne` a Company (consistente con el resto del código).
- `userId` nullable como `Long` (sin asociación obligatoria).
- DTOs seguros: `PartnerResponse(s)` (partnerId, companyId, userId, partnerType, partnerName, partnerDocument, partnerPhone, partnerState); jamás exponer datos de otras empresas.

## Reglas no representables en SQL

- **No duplicar cuentas de usuario para una misma persona/empresa:** el SQL **no** tiene UNIQUE sobre `(company_id, user_id)` (probado: N8 permitió dos partners con el mismo `user_id` en el tenant). La aplicación debe impedirlo → `409 ConflictException` vía `existsByCompanyIdAndUserId`.
- Vincular `user_id` exige que exista **membresía `ACTIVE`** en `(company_id, user_id)`; el SQL solo garantiza que la fila exista (FK), no que esté activa → validar en backend (`409` si INACTIVE, `404` si no existe).
- `user_id` debe ser validado contra `user_table` vía la membresía (el FK lo impone físicamente).
- Estado `INACTIVE`: soft delete lógico; un partner INACTIVE **no puede ser dueño de camiones nuevos** → `409` en `SaveTruckUseCase`.
- Permisos de lectura filtrados por identidad de perfil (PARTNER solo lo suyo), no solo por rol.

## Plan de migración

- Archivos: **ninguno** (sin DDL; sin Flyway/Liquibase — decisión mantenida de fases 1-2).
- Datos preexistentes: **0 filas** (verificado en `truck_sass`).
- Riesgo de bloqueo/downtime: no aplica.
- Plan de reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- Partner con `company_id` inexistente → **ERROR 1452** FK `fk_partner_company`.
- Partner con `user_id` sin membresía en la empresa → **ERROR 1452** FK `fk_partner_membership` (compuesta).
- Dos partners con el mismo `user_id` en el mismo tenant → **ACEPTADO por SQL** (N8) → regla de backend obligatoria.
- Inserción válida con y sin `user_id` → aceptada.
- Verificación: tras `ROLLBACK`, `COUNT(*) partner_table = 0`.

## Entrega al generador de módulo

- Casos de uso y rutas tentativas (bajo `/api/companies/{companyId}/partners`):
  - `GET /` — listar (SECRETARY/SUPER_ADMIN: todos; PARTNER/DRIVER: solo los suyos), paginado.
  - `GET /{partnerId}` — detalle por id+tenant.
  - `POST /` — crear (SECRETARY/SUPER_ADMIN), `201`.
  - `PUT /{partnerId}` — actualizar escalares.
  - `PUT /{partnerId}/status` — cambiar `partner_state`.
- Métodos de repository tenant-scoped requeridos: `findByCompanyIdAndPartnerId`, `findByCompanyId(Pageable)`, `findByCompanyIdAndUserId`, `existsByCompanyIdAndUserId`.
- Decisiones pendientes: **ninguna** (licencia estricta resuelta para driver; filtrado por rol resuelto para partner/truck).
