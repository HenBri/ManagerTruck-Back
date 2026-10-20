# Contrato de esquema: User

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `user_table`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE user_table`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 25-36) sin diferencias.
- **Base de datos:** MySQL 8.0.46, `sql_mode` con `STRICT_TRANS_TABLES`.
- **Dependencias previas:** ninguna (tabla raíz, sin FK). Las pertenencias y roles viven en `company_user`, `company_user_role`, `platform_user_role` (**fase 2, no implementar módulos**).

## Objetivo de negocio

Cuenta global del SaaS: una misma cuenta puede operar en varias empresas. **No** tiene `company_id` ni `role_id` directos. El alta crea la credencial con hash seguro; los roles y membresías se asignan en módulos separados (fase 2).

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `user_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK |
| `user_email` | `VARCHAR(255)` | NO | — | identificador de login |
| `password_hash` | `VARCHAR(255)` | NO | — | hash BCrypt; **nunca** exponer |
| `user_name` | `VARCHAR(100)` | NO | — | |
| `user_lastname` | `VARCHAR(150)` | NO | — | |
| `user_ci` | `VARCHAR(30)` | SÍ | NULL | cédula de identidad |
| `user_phone` | `VARCHAR(25)` | SÍ | NULL | VARCHAR, nunca numérico |
| `user_status` | `ENUM('ACTIVE','INACTIVE')` | NO | `'ACTIVE'` | cuentas INACTIVE no autentican |
| `user_created_at` | `DATETIME` | NO | `CURRENT_TIMESTAMP` | gestionado por BD |

- **PK:** simple `(user_id)` `BIGINT UNSIGNED AUTO_INCREMENT` (JPA: `Long` + IDENTITY).
- **UNIQUE:** `uq_user_email (user_email)` → correos únicos.
- **Índices:** `PRIMARY`, `uq_user_email`.
- **FKs:** ninguna.
- **CHECK:** ninguno.
- **Enums:** `user_status = ACTIVE | INACTIVE`.

## Tenant y autorización

- **Global:** cuenta sin tenant propio; la membresía se resuelve con `company_user` (fase 2).
- Origen seguro de company_id: no aplica en esta tabla; `GET /api/companies/{companyId}/users` debe validar membresía/rol del contexto autenticado.
- Roles permitidos (fase 1, decisión aprobada en el plan):
  - `POST /api/users` (registro): **público** — crea cuenta `ACTIVE` con hash; **no** acepta campos de rol ni de empresa; es imposible crear SUPER_ADMIN por este camino (no hay endpoint de asignación de roles).
  - `GET /api/users/me`, `PUT /api/users/{id}` (escalares), `PUT /api/users/{id}/password`: dueño de la cuenta o SUPER_ADMIN (password solo dueño).
  - `GET /api/users` (listado global paginado), `PUT /api/users/{id}/status`: **SUPER_ADMIN**.
  - `GET /api/companies/{companyId}/users`: SUPER_ADMIN o `SECRETARY` con membresía activa en esa empresa.
- Reglas para SUPER_ADMIN: aprobadas vía `platform_user_role`; el CRUD de usuarios no otorga roles.

## Mapeo Java/JPA

- Entidad: `domain/User.java`; ID `Long` + `@GeneratedValue(strategy = IDENTITY)`.
- `user_status` → enum `UserStatus` (`@Enumerated(EnumType.STRING)`).
- `user_created_at` → `LocalDateTime` `insertable=false, updatable=false`.
- `password_hash` → `String passwordHash` con `@Column(name = "password_hash")`; incluido en el entity pero **excluido de todos los DTOs de respuesta**.
- Sin asociaciones JPA (sin `company`, sin `role`) — prohibido por regla de negocio.
- DTOs seguros: `UserResponse` (id, email, name, lastname, ci, phone, status, createdAt); `password_hash` jamás se serializa.

## Reglas no representables en SQL

- Unicidad de correo verificada también en aplicación → `409` antes de violar `uq_user_email`.
- Contraseña hasheada con `BCryptPasswordEncoder`; nunca en claro, logs ni trazas.
- Registro público no puede asignar SUPER_ADMIN ni empresas (los campos simplemente no existen en el DTO).
- `PUT /api/users/{id}` no modifica `password_hash` ni `user_status` (operaciones separadas con autorización propia).
- Desactivación (`user_status=INACTIVE`) impide autenticación subsiguiente sin borrar la fila (auditoría).

## Plan de migración

- Archivos: **ninguno** (sin DDL requerido).
- Datos preexistentes: `user_table` vacía (0 filas) al momento de la fase 1.
- Riesgo de bloqueo/downtime: no aplica.
- Plan de reversión: no aplica.

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real)

- Email duplicado: segundo `INSERT` con mismo `user_email` → ERROR 1062 `Duplicate entry ... for key 'user_table.uq_user_email'`.
- NOT NULL: sin `user_email` → ERROR 1364; sin `password_hash` → ERROR 1364.
- ENUM: `user_status='BOGUS'` → ERROR 1265 `Data truncated`.
- Alta válida: insert con email+hash+nombre+apellido → aceptado (en transacción).
- Verificación: tras `ROLLBACK`, `COUNT(*) user_table = 0` (sin residuos).

## Entrega al generador de módulo

- Casos de uso y rutas tentativas: registro público, `GET /api/users/me`, consulta/edición de perfil, cambio de contraseña, alta/baja administrativa, listado global paginado, miembros por empresa.
- Métodos de repository requeridos: `findByUserEmail`, `existsByUserEmail`, `findById`, `findAll(Pageable)` (uso interno administrativo), queries nativas de roles/membresía para autorización (solo lectura sobre tablas fase 2).
- Decisiones todavía pendientes: flujo de recuperación de contraseña (no pedido en fase 1), paginación/filtros avanzados.
