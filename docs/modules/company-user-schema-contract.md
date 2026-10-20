# Contrato de esquema: CompanyUser (membresía de empresa)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `company_user`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE company_user`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 62-71) **sin diferencias** (tipos, PK, índices, FKs, DEFAULT y CHARSET).
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `company` (FK `fk_cu_company`), `user_table` (FK `fk_cu_user`). Requiere los contratos `company-schema-contract.md` y `user-schema-contract.md` en estado Migrado.

## Objetivo de negocio

Define la **pertenencia de una cuenta global (`user_table`) a una empresa (tenant `company`)**. Una cuenta puede pertenecer a varias empresas (PK compuesta sin surrogate). `membership_status` es la palanca de activación: una membresía `INACTIVE` anula de forma efectiva los roles `COMPANY` que el usuario tuviera en esa empresa (las consultas de autorización exigen membresía `ACTIVE`). Es la base de la regla "un usuario puede pertenecer a varias empresas".

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `company_id` | `BIGINT UNSIGNED` | NO | — | 1º componente de la PK; tenant |
| `user_id` | `BIGINT UNSIGNED` | NO | — | 2º componente de la PK; cuenta global |
| `membership_status` | `ENUM('ACTIVE','INACTIVE')` | NO | `ACTIVE` | estado de la membresía (soft-state, sin DELETE) |
| `joined_at` | `DATETIME` | NO | `CURRENT_TIMESTAMP` | gestionado por la BD (`DEFAULT_GENERATED`) |

- **PK:** **compuesta** `(company_id, user_id)` — sin `AUTO_INCREMENT`, sin ID surrogate (respetado, no inventar).
- **UNIQUE:** ninguno adicional (la PK ya cubre `(company_id,user_id)`).
- **Índices:** `PRIMARY (company_id, user_id)`, `idx_membership_user (user_id)` (justifica: "¿en qué empresas está este usuario?" — listado propio de membresías y búsqueda inversa).
- **FKs:**
  - `fk_cu_company (company_id) → company(company_id)` **ON DELETE RESTRICT** / NO ACTION.
  - `fk_cu_user (user_id) → user_table(user_id)` **ON DELETE RESTRICT** / NO ACTION.
- **CHECK:** ninguno local.
- **Enums:** `membership_status = ACTIVE | INACTIVE`.
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Por empresa:** `company_id` **es** el tenant. El origen de `company_id` en escritura es **la ruta** `POST /api/companies/{companyId}/memberships` validada contra la identidad autenticada — nunca el body del frontend.
- Roles permitidos sobre `/api/companies/{companyId}/memberships`:
  - `POST` (alta de membresía) y `PUT /{userId}/status` (cambio de estado): **SUPER_ADMIN** o **SECRETARY con membresía `ACTIVE` y rol `SECRETARY` en ese mismo `companyId`**.
  - `GET /` (listado paginado): **SUPER_ADMIN** o **SECRETARY de esa empresa**.
  - `GET /api/users/me/memberships`: cualquier autenticado, **solo sus propias** membresías.
- Reglas para SUPER_ADMIN: acceso global sin pertenecer a la empresa (la membresía no es obligatoria para administrar); operaciones limitadas a tenant explícito en la ruta.
- Denegación por defecto: cualquier ruta no listada exige autenticación (`anyRequest().authenticated()`); los guards de use case añaden la verificación tenant-scoped (defensa en profundidad sobre `SecurityConfig`).

## Mapeo Java/JPA

- Entidad: `domain/CompanyUser.java` con **`@EmbeddedId CompanyUserId`** (`@Embeddable` con `Long companyId`, `Long userId`) — el `Repository` usa `JpaRepository<CompanyUser, CompanyUserId>`, **no** `Long`.
- `membership_status` → enum `MembershipStatus` (`@Enumerated(EnumType.STRING)`, valores exactos `ACTIVE`, `INACTIVE`).
- `joined_at` → `LocalDateTime` con `insertable = false, updatable = false` (lo escribe la BD).
- Sin asociaciones JPA (`@ManyToOne`) — mapeo plano consistente con la fase 1.
- DTOs seguros: `CompanyMembershipResponse` (companyId, userId, email, nombre, apellido, membershipStatus, joinedAt, roleNames); **nunca** `password_hash` ni datos de empresas ajenas.

## Reglas no representables en SQL

- Alta de membresía solo por SUPER_ADMIN o SECRETARY **de la empresa solicitada** (la FK no puede evaluar el rol del autenticado).
- Una cuenta `INACTIVE` (`user_status`) no puede recibir membresías nuevas → `409`/`400` en aplicación.
- Membresía duplicada → `409` antes de violar la PK (aplicación) — el SQL devuelve 1062 como red de seguridad.
- Cambio de estado `ACTIVE ↔ INACTIVE` es **UPDATE, nunca DELETE** (decisión de la iteración 2: sin columna de estado adicional y sin endpoints DELETE; la baja se hace sobre `membership_status`).
- Pertenencia a una empresa no concede roles automáticamente: los roles se asignan aparte en `company_user_role`.
- Existe FK `ON DELETE CASCADE` desde `company_user_role`; como no hay DELETE de membresías en la API, la cascada no se activa por aplicación.

## Plan de migración

- Archivos: **ninguno** (sin DDL; sin Flyway/Liquibase en el proyecto — decisión del usuario: "no agregar nada, dejarlo como está").
- Datos preexistentes: 0 filas (verificado `COUNT(*) = 0` tras pruebas negativas).
- Riesgo de bloqueo/downtime: no aplica.
- Plan de reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- PK duplicada `(company_id,user_id)` → **ERROR 1062** `Duplicate entry '...' for key 'company_user.PRIMARY'`.
- `user_id` inexistente → **ERROR 1452** FK `fk_cu_user` (`ON DELETE RESTRICT`).
- Inserción válida (empresa + usuario + membresía) → aceptada en transacción.
- Verificación: tras `ROLLBACK`, `COUNT(*) company_user = 0` (sin residuos), `role_table = 4`.

## Entrega al generador de módulo

- Casos de uso y rutas tentativas:
  - `POST /api/companies/{companyId}/memberships` — alta (SUPER_ADMIN o SECRETARY del tenant).
  - `GET /api/companies/{companyId}/memberships` — listado paginado (SUPER_ADMIN o SECRETARY).
  - `PUT /api/companies/{companyId}/memberships/{userId}/status` — cambio `ACTIVE/INACTIVE` (SUPER_ADMIN o SECRETARY; `409` si ya está en ese estado).
  - `GET /api/users/me/memberships` — membresías propias con roles (autenticado).
- Métodos de repository tenant-scoped requeridos: `findById(CompanyUserId)`, `existsById`, `findByCompanyIdAndUserId`, listado paginado `findByCompanyId(...)`, actualización de `membershipStatus`.
- Decisiones todavía pendientes: ninguna bloqueante; DELETE de membresía **no** se implementa (decisión aprobada), baja = `membership_status = INACTIVE`.
