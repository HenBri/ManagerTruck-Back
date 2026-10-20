# Contrato de esquema: PlatformUserRole (roles de plataforma)

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `platform_user_role`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE platform_user_role`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 49-60) **sin diferencias**.
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** `user_table` (FK `fk_pur_user`), `role_table` (FK compuesta `fk_pur_role_scope`). Requiere los contratos `user-schema-contract.md` y `role-schema-contract.md`.

## Objetivo de negocio

Guarda los **roles de ámbito `PLATFORM`** de una cuenta — en la práctica, quién es `SUPER_ADMIN`. **No tiene columna `company_id`**: el permiso es global sobre la plataforma y su titular **no necesita pertenecer a todas las empresas** (regla explícita de la iteración). El `CHECK chk_pur_scope` garantiza que solo roles `PLATFORM` puedan registrarse aquí (la tabla es inútil para roles de empresa).

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `user_id` | `BIGINT UNSIGNED` | NO | — | 1º componente PK; cuenta global |
| `role_id` | `SMALLINT UNSIGNED` | NO | — | 2º componente PK; **`Integer` en Java** |
| `role_scope` | `ENUM('PLATFORM','COMPANY')` | NO | `PLATFORM` | debe ser `PLATFORM` (CHECK) |
| `granted_at` | `DATETIME` | NO | `CURRENT_TIMESTAMP` | gestionado por la BD (`DEFAULT_GENERATED`) |

- **PK:** **compuesta** `(user_id, role_id)` — sin surrogate, sin AUTO_INCREMENT.
- **UNIQUE:** ninguno adicional.
- **Índices:** `PRIMARY (user_id, role_id)`, `idx_pur_role (role_id, role_scope)` (joins con `role_table` por la FK compuesta).
- **FKs:**
  - `fk_pur_user (user_id) → user_table(user_id)` **ON DELETE RESTRICT**.
  - `fk_pur_role_scope (role_id, role_scope) → role_table(role_id, role_scope)` **ON DELETE RESTRICT**.
- **CHECK:** `chk_pur_scope CHECK (role_scope = 'PLATFORM')` — **operativo** (verificado: ERROR 3819).
- **Enums:** `role_scope = PLATFORM | COMPANY` (solo `PLATFORM` permitido).
- **Charset:** `utf8mb4` / `utf8mb4_0900_ai_ci`, `ENGINE=InnoDB`.

## Tenant y autorización

- **Global:** no hay `company_id`; un SUPER_ADMIN opera tenants **explícitamente seleccionados en la ruta** y sus acciones deben ser sobre recursos concretos (nunca "todas las empresas" implícitas).
- Escritura en la API: **NO**. Decisión de la iteración 2 (aprobada): el rol `SUPER_ADMIN` se aprovisiona **solo mediante un procedimiento manual seguro** (INSERT directo auditado en MySQL sobre `platform_user_role` por el operador de la plataforma), **nunca** por registro público ni por ningún endpoint. Por tanto:
  - **No existe** `POST`/`PUT`/`DELETE` sobre esta tabla en la API.
  - `GET /api/platform/user-roles` (solo lectura): **SUPER_ADMIN** autenticado, para auditoría de quién tiene la asignación PLATFORM.
- La authorities `ROLE_SUPER_ADMIN` en Spring Security se carga desde esta tabla vía `UserRepository.findPlatformRoleNames` (query nativa existente, `role_scope='PLATFORM'`).

## Mapeo Java/JPA

- Entidad: `domain/PlatformUserRole.java` con **`@EmbeddedId PlatformUserRoleId`** (`@Embeddable`: `Long userId`, `Integer roleId`); `Repository` → `JpaRepository<PlatformUserRole, PlatformUserRoleId>`.
- `role_scope` → enum `RoleScope` (`@Enumerated(EnumType.STRING)`); se persiste `PLATFORM`.
- `granted_at` → `LocalDateTime` con `insertable = false, updatable = false`.
- Sin asociaciones JPA.
- DTOs seguros: `PlatformUserRoleResponse` (userId, email del usuario, roleName, grantedAt) — **solo para SUPER_ADMIN**, sin `password_hash`.

## Reglas no representables en SQL

- **Aprovisionamiento seguro exclusivo** (procedimiento manual documentado en `docs/iterations/phase-2-report.md` §x): ningún endpoint de escritura, ni registro público, ni `POST /api/users` (el DTO de registro no acepta roles — regla de fase 1 conservada y re-verificada).
- Intento de elevar privilegios a `SUPER_ADMIN` vía endpoints empresariales → `403` (no existe tal endpoint + `chk_cur_scope` en la tabla de empresa como red de seguridad).
- Una cuenta puede ser `SUPER_ADMIN` **sin** tener membresías (`company_user` vacío) — la pertenencia no es requerida.
- Solo cuentas `user_status='ACTIVE'` con esta asignación autenticán como SUPER_ADMIN (`UserPrincipal.isEnabled`).

## Plan de migración

- Archivos: **ninguno** (sin DDL; sin Flyway/Liquibase — decisión del usuario).
- Datos preexistentes: 0 filas (verificado tras `ROLLBACK`). **No se siembra ningún SUPER_ADMIN desde la aplicación.**
- Riesgo de bloqueo/downtime: no aplica.
- Plan de reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real, con `ROLLBACK`)

- `role_scope='COMPANY'` → **ERROR 3819** `Check constraint 'chk_pur_scope' is violated`.
- `user_id` inexistente → **ERROR 1452** FK `fk_pur_user`.
- PK duplicada `(user_id, role_id)` → **ERROR 1062**.
- Inserción válida (`scope='PLATFORM'`, rol SUPER_ADMIN sembrado id=1) → aceptada en transacción.
- Verificación: tras `ROLLBACK`, `COUNT(*) platform_user_role = 0`.

## Entrega al generador de módulo

- Casos de uso y rutas tentativas:
  - `GET /api/platform/user-roles` — listado de grants PLATFORM, **solo SUPER_ADMIN** (solo lectura).
  - **Ningún caso de uso de escritura** (aprovisionamiento manual, decisión aprobada).
- Métodos de repository requeridos: `findAll()` (lectura de auditoría); las queries de authorities ya existen en `UserRepository` (sin duplicar).
- Decisiones todavía pendientes: definir (posterior a esta fase) un procedimiento con doble control/herramienta externa para el alta/baja de SUPER_ADMIN; hoy es manual.
