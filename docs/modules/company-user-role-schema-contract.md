# Contrato de esquema: CompanyUserRole (roles por empresa)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `company_user_role`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE company_user_role`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 73-86) **sin diferencias** (PK compuesta, FKs compuestas, CHECK, DEFAULT).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `company_user` (FK `fk_cur_membership`), `role_table` (FK compuesta `fk_cur_role_scope`). Requiere los contratos `company-user-schema-contract.md` y `role-schema-contract.md`.

## Objetivo de negocio

Asigna **roles de ámbito `COMPANY` (`SECRETARY`, `PARTNER`, `DRIVER`) a un miembro de una empresa**. La PK de 3 componentes permite que un mismo usuario tenga **varios roles en la misma empresa** (p. ej. `PARTNER` + `DRIVER` simultáneamente) y que el mismo rol exista en empresas distintas. El `CHECK chk_cur_scope` impide a nivel de BD que un rol `PLATFORM` (`SUPER_ADMIN`) se inserte aquí: **la tabla es físicamente incapaz de conceder privilegios de plataforma** (defensa de la regla anti-elevación de privilegios).

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `company_id` | `BIGINT UNSIGNED` | NO | — | 1º componente PK; tenant (heredado de la membresía) |
| `user_id` | `BIGINT UNSIGNED` | NO | — | 2º componente PK |
| `role_id` | `SMALLINT UNSIGNED` | NO | — | 3º componente PK; **`Integer` en Java** |
| `role_scope` | `ENUM('PLATFORM','COMPANY')` | NO | `COMPANY` | debe ser `COMPANY` (CHECK) |

- **PK:** **compuesta** `(company_id, user_id, role_id)` — sin surrogate, sin AUTO_INCREMENT.
- **UNIQUE:** ninguno adicional.
- **Índices:** `PRIMARY (company_id,user_id,role_id)`, `idx_cur_role (role_id, role_scope)` (justifica: joins con `role_table` por la FK compuesta).
- **FKs:**
  - `fk_cur_membership (company_id, user_id) → company_user(company_id, user_id)` **ON DELETE CASCADE** (baja física de la membresía arrasaría la asignación; la API no borra membresías).
  - `fk_cur_role_scope (role_id, role_scope) → role_table(role_id, role_scope)` **ON DELETE RESTRICT**.
- **CHECK:** `chk_cur_scope CHECK (role_scope = 'COMPANY')` — **operativo** (MySQL 8.0.46 ≥ 8.0.16; verificado: ERROR 3819).
- **Enums:** `role_scope = PLATFORM | COMPANY` (solo `COMPANY` permitido).
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` es el tenant; llega **solo por la ruta** `POST /api/companies/{companyId}/memberships/{userId}/roles`, nunca por el body. Los componentes `(company_id, user_id)` deben coincidir con una membresía existente (FK compuesta), lo cual también impide "inventar" miembros no pertenecientes a la empresa.
- Roles permitidos sobre `/api/companies/{companyId}/memberships/{userId}/roles`:
  - `POST` (asignación de 1..N roles): **SUPER_ADMIN** o **SECRETARY con membresía `ACTIVE` y rol `SECRETARY` en ese `companyId`**.
  - `GET` (consulta): **SUPER_ADMIN**, **SECRETARY de esa empresa** o **el propio usuario** (`userId` = autenticado).
- Reglas para SUPER_ADMIN: puede asignar roles en cualquier tenant explícito; **nunca** puede insertar un rol `PLATFORM` por esta vía (lo rechaza el CHECK + validación de aplicación → `403`).
- La autorización efectiva (authorities en Spring Security) se deriva de esta tabla cruzada con `membership_status='ACTIVE'` — consultar `UserRepository.findCompanyRoleNames/findAllCompanyRoleNames`.

## Mapeo Java/JPA

- Entidad: `domain/CompanyUserRole.java` con **`@EmbeddedId CompanyUserRoleId`** (`@Embeddable`: `Long companyId`, `Long userId`, `Integer roleId`); `Repository` → `JpaRepository<CompanyUserRole, CompanyUserRoleId>`.
- `role_scope` → enum `RoleScope` (`@Enumerated(EnumType.STRING)`); columna escriturable con valor `COMPANY` (DEFAULT de BD, se persiste explícitamente para respetar `NOT NULL DEFAULT`).
- `role_id` dentro del ID: `Integer` (SMALLINT UNSIGNED; `@JdbcTypeCode(SqlTypes.SMALLINT)` si Hibernate lo requiere en la validación de esquema).
- Sin asociaciones JPA; sin estado adicional (**no se agregó columna de estado** — decisión del usuario).
- DTOs seguros: `CompanyUserRolesResponse` (companyId, userId, roleNames); jamás datos de otras empresas ni `password_hash`.

## Reglas no representables en SQL

- **Anti-elevación de privilegios:** la aplicación valida además que cada `role_name` solicitado exista y su `role_scope` sea `COMPANY` **antes** de persistir → `403 ForbiddenOperationException` con mensaje claro (el CHECK sería un error 3819 crudo). Intentar asignar `SUPER_ADMIN` rechaza con `403` aunque el CHECK también lo haría.
- Exigir **membresía `ACTIVE`** del `(company_id,user_id)` antes de asignar roles → `409` si no existe membresía o está `INACTIVE` (el SQL solo exige que la fila de membresía *exista*, no que esté activa — comprobación de negocio).
- Asignación idempotente: un rol ya asignado no duplica (PK 1062 evitado con pre-check → no-op o el conjunto final).
- Asignación múltiple en una sola operación (`["PARTNER","DRIVER"]`) con `@Transactional` y rollback ante fallo parcial.
- **Sin endpoint DELETE ni columna de estado** (decisión aprobada): la revocación de roles queda como pendiente documentado.
- Cuentas `INACTIVE` y membresías `INACTIVE` pierden el efecto de autorización sin borrar filas (la consulta exige `membership_status='ACTIVE'`).

## Plan de migración

- Archivos: **ninguno** (sin DDL; sin Flyway/Liquibase — decisión del usuario).
- Datos preexistentes: 0 filas (verificado tras `ROLLBACK`).
- Riesgo de bloqueo/downtime: no aplica.
- Plan de reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- `role_scope='PLATFORM'` (asignar SUPER_ADMIN vía SQL) → **ERROR 3819** `Check constraint 'chk_cur_scope' is violated`.
- `role_id` de SUPER_ADMIN (`role_id=1, scope=PLATFORM`) con scope `PLATFORM` → **ERROR 3819** `chk_cur_scope`.
- `role_id` inexistente → **ERROR 1452** FK `fk_cur_role_scope`.
- Sin membresía previa → **ERROR 1452** FK `fk_cur_membership`.
- PK duplicada → ERROR 1062.
- Inserción válida con **dos roles** (`PARTNER` id=3 + `DRIVER` id=4) en la misma membresía → **aceptada** (`VALID_INSERT_OK`).
- Verificación: tras `ROLLBACK`, `COUNT(*) company_user_role = 0`.

## Entrega al generador de módulo

- Casos de uso y rutas tentativas:
  - `POST /api/companies/{companyId}/memberships/{userId}/roles` — asignar N roles (SUPER_ADMIN o SECRETARY del tenant).
  - `GET /api/companies/{companyId}/memberships/{userId}/roles` — consultar roles (SUPER_ADMIN, SECRETARY del tenant o el propio usuario).
  - **Sin DELETE** (decisión aprobada).
- Métodos de repository tenant-scoped requeridos: `findById(CompanyUserRoleId)`, `existsById`, `saveAll` (asignación múltiple), consulta de roles del usuario filtrando membresía `ACTIVE` (reutilizar la query nativa existente en `UserRepository` como fuente de authorities).
- Decisiones todavía pendientes: revocación de roles (bloqueada por diseño en esta iteración: sin columna de estado, sin DELETE).
