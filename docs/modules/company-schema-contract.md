# Contrato de esquema: Company

- **Estado:** Migrado (el esquema vivo satisface el contrato; sin herramienta de migración aprobada)
- **Tabla(s):** `company`
- **Fuente de verdad:** esquema desplegado en MySQL 8.0.46, base `truck_sass` (`SHOW CREATE TABLE company`); verificado contra `references/schema_base_transporte_saas.sql` (líneas 14-22) sin diferencias.
- **Base de datos:** MySQL 8.0.46 (versión exacta confirmada con `SELECT VERSION()`), `sql_mode = ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION`
- **Dependencias previas:** ninguna (tabla raíz, sin FK)

## Objetivo de negocio

Representa una empresa cliente del SaaS (tenant de datos operativos). Es creada y administrada por la plataforma; su `company_id` es el aislamiento de tenant de todas las tablas operativas futuras. **No** es propietaria de camiones.

## Definición física

| Columna | Tipo SQL exacto | NULL | DEFAULT | Observaciones |
|---|---|---|---|---|
| `company_id` | `BIGINT UNSIGNED AUTO_INCREMENT` | NO | — | PK |
| `company_name` | `VARCHAR(150)` | NO | — | razón social |
| `company_nit` | `VARCHAR(30)` | SÍ | NULL | identificación fiscal; **no** es UNIQUE en el esquema vigente |
| `company_address` | `VARCHAR(255)` | SÍ | NULL | |
| `company_phone` | `VARCHAR(25)` | SÍ | NULL | VARCHAR, nunca numérico |
| `company_status` | `ENUM('ACTIVE','INACTIVE')` | NO | `'ACTIVE'` | soft delete / desactivación |
| `company_created_at` | `DATETIME` | NO | `CURRENT_TIMESTAMP` | gestionado por BD |

- **PK:** simple `(company_id)` `BIGINT UNSIGNED AUTO_INCREMENT` (JPA: `Long` + `GenerationType.IDENTITY`).
- **UNIQUE:** ninguno (el NIT no tiene restricción de unicidad en el esquema vigente; no inventarla).
- **Índices:** solo `PRIMARY`.
- **FKs:** ninguna.
- **CHECK:** ninguno.
- **Enums:** `company_status = ACTIVE | INACTIVE`.

## Tenant y autorización

- **Global:** `company` *es* el propietario del tenant; no lleva `company_id`.
- Origen seguro de company_id: en futuras tablas operativas proviene del contexto autenticado, nunca del body.
- Roles permitidos (fase 1, decisión aprobada en el plan):
  - `POST /api/companies`, `GET /api/companies` (listado), `PUT /api/companies/{id}`, `PUT /api/companies/{id}/status`: **SUPER_ADMIN** (scope PLATFORM, verificado en `platform_user_role`).
  - `GET /api/companies/{id}`: SUPER_ADMIN o miembro activo de esa empresa (`company_user.membership_status = ACTIVE`).
- Reglas para SUPER_ADMIN: la creación/desactivación de empresas es exclusiva de plataforma; ningún endpoint empresarial concede SUPER_ADMIN.

## Mapeo Java/JPA

- Entidad: `domain/Company.java`; ID `Long` con `@GeneratedValue(strategy = IDENTITY)`.
- `company_status` → enum Java `CompanyStatus` con `@Enumerated(EnumType.STRING)` (valores exactos `ACTIVE`, `INACTIVE`).
- `company_created_at` → `LocalDateTime` con `insertable = false, updatable = false` (lo genera la BD).
- Sin asociaciones JPA en fase 1 (no existen entidades de tablas hijas).
- DTOs seguros: `CompanyResponse` **no** expone nada sensible (no hay secretos); `password_hash` no aplica.

## Reglas no representables en SQL

- Quién puede crear/modificar empresas (requiere rol SUPER_ADMIN efectivo vía `platform_user_role`).
- La desactivación (`company_status = INACTIVE`) debe impedir nuevos usos del tenant en fases siguientes; el estado solo se cambia por caso de uso autorizado.
- No borrar empresas físicamente (FKs futuras `ON DELETE RESTRICT`); soft delete mediante `INACTIVE`.

## Plan de migración

- Archivos: **ninguno** (no se requiere DDL; el esquema vivo ya cumple el contrato). Flyway/Liquibase propuesto para fase 2 (decisión del usuario: sin herramienta en fase 1).
- Datos preexistentes: `company` vacía (0 filas) al momento de la fase 1.
- Riesgo de bloqueo/downtime: no aplica.
- Plan de reversión: no aplica (sin cambios estructurales).

## Pruebas de base de datos (ejecutadas en MySQL 8.0.46 real)

- Alta válida: `INSERT` con `company_name`+`company_status='BOGUS'` → rechazado (ERROR 1265, ENUM), con `company_status` válido → aceptado (verificado dentro de transacción con rollback).
- NOT NULL: `INSERT` solo con `company_nit` → ERROR 1364 `Field 'company_name' doesn't have a default value`.
- Estado por defecto: `company_status` DEFAULT `ACTIVE` confirmado en `SHOW CREATE TABLE`.
- Verificación de datos: tras `ROLLBACK`, `COUNT(*) company = 0` (sin residuos).

## Entrega al generador de módulo

- Casos de uso y rutas tentativas: `POST/GET/PUT /api/companies`, `GET /api/companies/{id}`, `PUT /api/companies/{id}/status`.
- Métodos de repository tenant-scoped requeridos: `findById`, `findAll(Pageable)`, `existsByCompanyNit` (no requerido; el esquema no lo hace único).
- Decisiones todavía pendientes: paginación por nombre de empresa (fase 2), endpoint de baja definitiva (no permitido por el esquema).
