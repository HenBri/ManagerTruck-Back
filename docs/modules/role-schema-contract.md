# Contrato de esquema: Role

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `role_table`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE role_table`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 40-47) sin diferencias.
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** ninguna (tabla raíz, sin FK). La asignación de roles está en `platform_user_role` y `company_user_role` (**fase 2, no implementar módulos**).

## Objetivo de negocio

Catálogo global de los cuatro roles del SaaS con su alcance explícito: `SUPER_ADMIN` (scope `PLATFORM`) y `SECRETARY`, `PARTNER`, `DRIVER` (scope `COMPANY`). El alcance impide que un rol de plataforma se asigne a una empresa y viceversa (lo garantizan los CHECK de las tablas pivote en fase 2).

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `role_id` | `SMALLINT UNSIGNED AUTO_INCREMENT` | NO | — | PK; **no** asumir `Long` en Java |
| `role_name` | `VARCHAR(50)` | NO | — | nombre canónico del rol |
| `role_scope` | `ENUM('PLATFORM','COMPANY')` | NO | — | sin default |
| `role_description` | `VARCHAR(255)` | SÍ | NULL | texto informativo |

- **PK:** simple `(role_id)` `SMALLINT UNSIGNED AUTO_INCREMENT` (JPA: `Integer` + IDENTITY).
- **UNIQUE:** `uq_role_name (role_name)`, `uq_role_id_scope (role_id, role_scope)`.
- **Índices:** `PRIMARY`, `uq_role_name`, `uq_role_id_scope`.
- **FKs:** ninguna.
- **CHECK:** ninguno local (los CHECK `chk_pur_scope`/`chk_cur_scope` viven en las tablas pivote, fase 2).
- **Enums:** `role_scope = PLATFORM | COMPANY`.

### Datos sembrados vigentes (fila por fila, verificado con `SELECT *`)

| role_id | role_name | role_scope | role_description |
|---|---|---|---|
| 1 | `SUPER_ADMIN` | `PLATFORM` | Control global del SaaS; uso exclusivo del administrador de la plataforma |
| 2 | `SECRETARY` | `COMPANY` | Gestion operativa y administrativa de su empresa |
| 3 | `PARTNER` | `COMPANY` | Consulta de sus camiones y viajes autorizados |
| 4 | `DRIVER` | `COMPANY` | Consulta y actualizacion permitida de sus viajes |

## Tenant y autorización

- **Global + scope:** la tabla no es de tenant; el scope define dónde puede asignarse cada rol.
- Origen seguro de company_id: no aplica.
- Roles permitidos (fase 1, decisión aprobada en el plan):
  - `GET /api/roles`, `GET /api/roles/{id}`: cualquier usuario autenticado (catálogo necesario para UI).
  - `POST /api/roles`, `PUT /api/roles/{id}`: **SUPER_ADMIN** exclusivamente.
  - `DELETE`: **no se implementa** (no existe columna de estado en la tabla y las FKs de fase 2 usan `ON DELETE RESTRICT`); queda como pendiente documentado.
- Reglas para SUPER_ADMIN: **no existe endpoint que asigne roles** en fase 1; el alta de un rol llamado `SUPER_ADMIN` queda además protegida porque `uq_role_name` ya lo tiene sembrado (ERROR 1062) y la creación de roles es solo PLATFORM-admin.

## Mapeo Java/JPA

- Entidad: `domain/Role.java`; ID **`Integer`** con `@GeneratedValue(strategy = IDENTITY)` (SMALLINT UNSIGNED).
- `role_scope` → enum `RoleScope` (`@Enumerated(EnumType.STRING)`, valores exactos `PLATFORM`, `COMPANY`).
- Sin asociaciones JPA en fase 1 (las tablas pivote son fase 2).
- DTOs seguros: `RoleResponse` (id, name, scope, description); no hay secretos.

## Reglas no representables en SQL

- Unicidad de `role_name` verificada en aplicación → `409` antes de violar `uq_role_name`.
- Cambiar `role_scope` de un rol existente está prohibido en la aplicación (rompería `uq_role_id_scope` y las asignaciones de fase 2) → `409`/`400`.
- Los cuatro roles sembrados no se eliminan ni se renombran libremente por endpoints no autorizados.
- El rol efectivo de un usuario se consulta en `platform_user_role`/`company_user_role`, nunca se deduce del nombre.

## Plan de migración

- Archivos: **ninguno** (sin DDL requerido).
- Datos preexistentes: 4 filas sembradas (los cuatro roles); se conservan intactas.
- Riesgo de bloqueo/downtime: no aplica.
- Plan de reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real)

- Nombre duplicado: `INSERT ('SECRETARY','COMPANY')` → ERROR 1062 `Duplicate entry 'SECRETARY' for key 'role_table.uq_role_name'`.
- ENUM de scope: `role_scope='GLOBAL'` → ERROR 1265 `Data truncated for column 'role_scope'`.
- Alta válida: rol nuevo con scope válido → aceptado (en transacción).
- Verificación: tras `ROLLBACK`, `COUNT(*) role_table = 4` (semillas intactas).

## Entrega al generador de módulo

- Casos de uso y rutas tentativas: `GET /api/roles`, `GET /api/roles/{id}`, `POST /api/roles` (admin), `PUT /api/roles/{id}` (admin, solo nombre/descripción).
- Métodos de repository requeridos: `findById`, `findAll` ordenado, `existsByRoleName`, `findByRoleName`.
- Decisiones todavía pendientes: baja de roles (bloqueada por diseño en fase 1), endpoint de asignación de roles (fase 2).
