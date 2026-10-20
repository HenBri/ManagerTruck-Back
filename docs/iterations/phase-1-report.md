# Informe de Iteración 1 — Entidades fundamentales (Company, User, Role)

- **Proyecto:** ManagerTruck-Back (`truck/`, Spring Boot 3.5.0, Java 17, JPA/Hibernate 6.6, MySQL 8)
- **Fecha:** 2026-10-16
- **Skills aplicadas:** `database-schema` (validación + contratos) y `generate-module` (implementación por capas)
- **Estado:** COMPLETADA — compilación y pruebas en verde (29/29), sin cambios destructivos sobre la BDD

---

## 1. Decisiones de alcance aprobadas por el usuario

| Tema | Decisión |
|---|---|
| Base de datos | `truck_sass` (MySQL 8.0.46 local; tiene las 16 tablas completas) |
| Seguridad | Spring Security mínimo: BCrypt + HTTP Basic stateless + roles efectivos leídos de `platform_user_role`/`company_user` **solo como consulta de autorización** (sin crear módulos de fase 2) |
| Migraciones | Sin Flyway/Liquibase en fase 1: el esquema vivo ya cumple el contrato; se documenta con `ddl-auto=validate` |
| Módulos legados | Los 120 archivos legados borrados y stageados se **mantienen borrados** (incompatibles con el esquema nuevo); no se restauran ni modifican módulos ajenos |
| Verificación | Tests unitarios (Mockito) + tests de control de acceso (`@WebMvcTest`) + `contextLoads` con validación contra MySQL real |

## 2. `database-schema` — validación del esquema vigente

**Fuentes contrastadas:** esquema desplegado en `truck_sass` (`SHOW CREATE TABLE`, `information_schema`, `SHOW INDEX`) vs `references/schema_base_transporte_saas.sql` vs migraciones del proyecto (no existen; verificado: no hay Flyway `db/migration` ni Liquibase `db/changelog`).

**Resultado: SIN diferencias** para `company`, `user_table` y `role_table`. Tipos, nulabilidad, DEFAULT, ENUMs, AUTO_INCREMENT e índices coinciden carácter a carácter con el baseline:

- `company`: PK `company_id BIGINT UNSIGNED AI`; `company_status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE'`; sin UNIQUE adicionales.
- `user_table`: PK `user_id BIGINT UNSIGNED AI`; **UNIQUE `uq_user_email`**; `password_hash VARCHAR(255) NOT NULL`; `user_status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE'`; sin `company_id` ni `role_id` (cuenta global, regla confirmada).
- `role_table`: PK `role_id SMALLINT UNSIGNED AI`; UNIQUE `uq_role_name` y `uq_role_id_scope`; `role_scope ENUM('PLATFORM','COMPANY')`; **4 filas sembradas** (SUPER_ADMIN/PLATFORM, SECRETARY/COMPANY, PARTNER/COMPANY, DRIVER/COMPANY).
- `sql_mode` con `STRICT_TRANS_TABLES` (validación estricta activa). Las tres tablas de fase 1 **no tienen FK**.

**Pruebas negativas ejecutadas en MySQL real** (transacción con `ROLLBACK`, sin dejar residuos):

| Caso | Error obtenido | Esperado |
|---|---|---|
| Email duplicado | `ERROR 1062 ... 'user_table.uq_user_email'` | ✅ |
| `user_email` sin valor | `ERROR 1364 Field 'user_email' doesn't have a default value` | ✅ |
| `password_hash` sin valor | `ERROR 1364 Field 'password_hash' doesn't have a default value` | ✅ |
| `user_status='BOGUS'` | `ERROR 1265 Data truncated for column 'user_status'` | ✅ |
| Rol duplicado (`SECRETARY`) | `ERROR 1062 ... 'role_table.uq_role_name'` | ✅ |
| `role_scope='GLOBAL'` | `ERROR 1265 Data truncated for column 'role_scope'` | ✅ |
| `company_status='BOGUS'` | `ERROR 1265 Data truncated for column 'company_status'` | ✅ |
| `company` sin `company_name` | `ERROR 1364 Field 'company_name' doesn't have a default value` | ✅ |
| Inserción válida (transacción) | Aceptada | ✅ |
| **Conteos tras `ROLLBACK`** | `company=0, user_table=0, role_table=4` (semillas intactas) | ✅ |

**Otros hallazgos de BDD:** existen3 bases con el esquema: `truck_sass` (16 tablas, completa), `transporte_saas` (15 tablas — **le falta `platform_user_role`**) y `transporte_saasroles` (16). Se usa `truck_sass`. **No se detectó incompatibilidad que detenga la fase 1.**

## 3. Contratos de esquema generados (`docs/modules/`)

- `docs/modules/company-schema-contract.md`
- `docs/modules/user-schema-contract.md`
- `docs/modules/role-schema-contract.md`

Todos con estado **Migrado/Satisface contrato**, fuente = esquema vivo `truck_sass`, definición física completa, tenant/autorización, mapeo JPA, reglas no representables en SQL, plan de migración (ninguno) y pruebas SQL ejecutadas.

## 4. `generate-module` — archivos creados (63 en `main`,6 en `test`)

Ruta base: `truck/src/main/java/com/manager/truck/truck/`. Cadena Controller → UseCase → Service(+Impl) → Repository → Entity, paquetes `usecase/{x}` en minúscula, inyección por constructor.

| Paquete | Archivos |
|---|---|
| `constant/` | `ResponseConstant.java` (recreado de HEAD) |
| `controller/` | `CompanyController`, `UserController`, `RoleController`, `GlobalExceptionHandler` |
| `domain/` | `Company`, `User`, `Role`, `Constants` (solo constantes de las3 tablas, con nombres SQL reales) |
| `domain/enums/` | `CompanyStatus`, `UserStatus`, `RoleScope` (`@Enumerated(STRING)`) |
| `domain/dto/request/` | `CompanyRequest`, `CompanyStatusRequest`, `UserRegisterRequest`, `UserUpdateRequest`, `UserPasswordRequest`, `UserStatusRequest`, `RoleRequest`, `RoleUpdateRequest` |
| `domain/dto/response/` | `CommonResponse` (recreado), `ErrorResponse`, `CompanyResponse`, `CompaniesResponse`, `UserResponse`, `UsersResponse`, `RoleResponse`, `RolesResponse` (nunca exponen `password_hash`) |
| `exception/` | `ResourceNotFoundException` (404), `ConflictException` (409), `ForbiddenOperationException` (403) |
| `repository/` | `CompanyRepository`, `RoleRepository`, `UserRepository` (+ queries nativas de autorización de solo lectura) |
| `security/` | `SecurityConfig`, `AuthUserDetailsService`, `UserPrincipal`, `CurrentUser`, `CurrentUserProvider`, `SecurityRoles` |
| `service/` | `CompanyService(+Impl)`, `UserService(+Impl)`, `RoleService(+Impl)` |
| `usecase/company/` | `SaveCompanyUseCase`, `GetAllCompaniesUseCase`, `GetCompanyByIdUseCase`, `UpdateCompanyUseCase`, `UpdateCompanyStatusUseCase` |
| `usecase/user/` | `RegisterUserUseCase`, `GetCurrentUserUseCase`, `GetUserByIdUseCase`, `UpdateUserUseCase`, `UpdateUserPasswordUseCase`, `UpdateUserStatusUseCase`, `GetAllUsersUseCase`, `GetCompanyUsersUseCase` |
| `usecase/role/` | `GetAllRolesUseCase`, `GetRoleByIdUseCase`, `SaveRoleUseCase`, `UpdateRoleUseCase` |
| `src/test/` | `SecurityAccessControlTest`, `RegisterUserUseCaseTest`, `UpdateUserPasswordUseCaseTest`, `SaveRoleUseCaseTest`, `GetCompanyByIdUseCaseTest`, `GetAllUsersUseCaseTest` |

**No se creó ningún módulo de fase 2** (no hay controllers/usecases/DTOs de `company_user`, `platform_user_role` ni `company_user_role`; las queries nativas sobre esas tablas son exclusivamente de autorización interna, como se aprobó).

## 5. Archivos existentes modificados

| Archivo | Cambio | Motivo |
|---|---|---|
| `truck/pom.xml` | +`spring-boot-starter-security`, +`spring-boot-starter-validation`, +`spring-security-test` (test) | Reglas de negocio: hash BCrypt, autorización efectiva, `@Valid` |
| `truck/pom.xml` | springdoc `2.4.0` → `2.8.6` | **Incompatibilidad detectada**: 2.4.0 lanza `NoSuchMethodError: ControllerAdviceBean.<init>` con Spring Framework 6.2 (Boot 3.5) y `/v3/api-docs` devolvía 500 |
| `truck/src/main/resources/application.properties` | datasource `truck` → **`truck_sass`** | La BD `truck` **no existe** (config original rota) |
| `truck/src/main/resources/application.properties` | `ddl-auto` `create` → **`validate`** | `create` borraba las4 filas sembradas de `role_table` en cada arranque; `validate` comprueba entidades vs BDD |
| `truck/src/main/resources/application.properties` | +`spring.jpa.open-in-view=false` | Con OSIV, `save`+`findById` compartían el mismo PC y `*_created_at` (DEFAULT de BD) no se releía |
| `truck/.gitignore` | eliminada la regla `/src/test` | **Ignoraba toda la carpeta de tests**: los tests de la entrega no se habrían versionado |

## 6. Endpoints implementados y autorización

| Método/Ruta | Autorización efectiva | Notas |
|---|---|---|
| `POST /api/users` | **Público** | Alta con BCrypt; DTO sin campos de rol/empresa → imposible crear SUPER_ADMIN aquí; status forzado `ACTIVE` |
| `GET /api/users/me` | Autenticado | Devuelve la propia cuenta |
| `GET /api/users/{id}` | Propia o SUPER_ADMIN | 403 en otro caso |
| `PUT /api/users/{id}` | Propia o SUPER_ADMIN | Solo escalares (no email, ni password, ni status) |
| `PUT /api/users/{id}/password` | **Solo el dueño** | Verifica contraseña actual; 403 si no coincide |
| `PUT /api/users/{id}/status` | SUPER_ADMIN | 409 si intenta desactivarse a sí mismo |
| `GET /api/users` (paginado) | SUPER_ADMIN | Sin `findAll()` global abierto |
| `GET /api/companies/{companyId}/users` | SUPER_ADMIN o `SECRETARY` **de esa empresa** | Membresía `ACTIVE` + rol verificados por query |
| `POST /api/companies` | SUPER_ADMIN | Alta de tenant |
| `GET /api/companies` (paginado) | SUPER_ADMIN | |
| `GET /api/companies/{id}` | SUPER_ADMIN o miembro activo | 403 a no miembros |
| `PUT /api/companies/{id}` | SUPER_ADMIN | Escalares sin cambiar estado |
| `PUT /api/companies/{id}/status` | SUPER_ADMIN | Soft delete → `INACTIVE` |
| `GET /api/roles`, `GET /api/roles/{id}` | Autenticado | Catálogo |
| `POST /api/roles`, `PUT /api/roles/{id}` | SUPER_ADMIN | Único nombre → 409; **no cambia `role_scope`**; sin endpoint que asigne roles |
| `DELETE /api/roles/{id}` | — | **No implementado** (sin columna de estado; FKs de fase 2 con RESTRICT) → pendiente |

Todo lo no listado exige autenticación (`anyRequest().authenticated()`); sin credenciales → **401 JSON** `{"code":"401","message":"UNAUTHORIZED"}`; rol insuficiente → **403 JSON**. Sesiones STATELESS, CSRF desactivado, HTTP Basic contra `user_table.user_email` con `BCryptPasswordEncoder`; cuentas `INACTIVE` no autentican.

## 7. Pruebas ejecutadas y resultados

### 7.1 Validación SQL en MySQL 8.0.46 real
Ver sección2:8/8 restricciones negativas rechazadas con el error esperado y rollback limpio (`0/4/0`).

### 7.2 Compilación y pruebas Maven (`truck/`)

```
mvn clean test
Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS   (EXIT=0)
```

| Clase de test | Tests | Resultado |
|---|---|---|
| `SecurityAccessControlTest` (`@WebMvcTest` + `SecurityConfig`) | 15 | ✅ 401/403/200/201 por ruta y rol |
| `RegisterUserUseCaseTest` | 2 | ✅ hash BCrypt + status ACTIVE; duplicado → 409 |
| `UpdateUserPasswordUseCaseTest` | 3 | ✅ solo dueño; contraseña actual errónea → 403; hash nunca en claro |
| `SaveRoleUseCaseTest` | 3 | ✅ no-SUPER_ADMIN → 403; nombre duplicado → 409; alta con scope |
| `GetCompanyByIdUseCaseTest` | 3 | ✅ no miembro → 403; miembro y SUPER_ADMIN → 200 |
| `GetAllUsersUseCaseTest` | 2 | ✅ no-SUPER_ADMIN → 403; paginación sin `password_hash` |
| `TruckTableApplicationTests` (`contextLoads`) | 1 | ✅ arranque completo + **validación Hibernate del esquema real** (DDL vivas contra `truck_sass`) |

### 7.3 Prueba E2E con la aplicación real (curl contra `localhost:8080`)

| Escenario | HTTP | Resultado |
|---|---|---|
| `GET /api/roles` sin credenciales | 401 | `{"code":"401","message":"UNAUTHORIZED"}` |
| `POST /api/users` con `roleName:"SUPER_ADMIN"` y `company:99` en el body (inyección) | 201 | Creada la cuenta **sin** rol/empresa asignados (DTO los ignora) |
| `POST /api/users` email duplicado | 409 | `El correo ya está registrado` |
| `POST /api/users` email inválido + campos vacíos | 400 | Errores de Bean Validation por campo |
| `GET /api/users/me` con Basic correcto | 200 | Devuelve la cuenta con `createdAt` real |
| `GET /api/users/me` con password incorrecta | 401 | JSON consistente `UNAUTHORIZED` |
| `GET /api/users` (lista global) sin rol admin | 403 | `{"code":"403","message":"FORBIDDEN"}` |
| `PUT /api/roles/1` con rol no admin | 403 | Bloqueado |
| `GET /v3/api-docs` (OpenAPI) | 200 | Documentación operativa (tras subir springdoc) |

**Verificación SQL posterior al E2E:** `password_hash` con prefijo `$2a$10$` y **0** coincidencias de la contraseña en claro; `role_table` intacta (4 filas originales); `platform_user_role = 0`, `company_user = 0`, `company_user_role = 0`, `company = 0` (nadie quedó con SUPER_ADMIN); datos de prueba eliminados (`user_table = 0`).

## 8. Incompatibilidades detectadas y su resolución

| Incompatibilidad | Resolución |
|---|---|
| springdoc 2.4.0 ↔ Spring Framework 6.2 (`/v3/api-docs` → 500) | Actualizado a springdoc **2.8.6** (probado: 200) |
| `role_table.role_id SMALLINT UNSIGNED` vs `Integer` esperado (`Schema-validation: found smallint unsigned, expecting integer`) | `@JdbcTypeCode(SqlTypes.SMALLINT)` en `Role.roleId` — se mantiene `Integer` en Java (no `Long`) según la Skill |
| `application.properties` apuntaba a la BD `truck` inexistente y `ddl-auto=create` destruiría las semillas | `truck_sass` + `validate` |
| `target/` con clases legadas del paquete `usecase/Company` (C mayúscula) impedía compilar `usecase.company` en Windows | `mvn clean` (resuelto y verificado) |
| `createdAt` nulo en respuestas de alta (Open EntityManager In View) | `spring.jpa.open-in-view=false` + recarga posterior al insert (verificado E2E) |
| `.gitignore` de `truck/` ignoraba `/src/test` | Regla eliminada; los6 tests nuevos son visibles para git |

No hubo incompatibilidad entre el esquema de fase 1 y la BDD vigente (cláusula de detención no aplicable).

## 9. Reglas de negocio — estado

- ✅ Company = empresa cliente del SaaS (tenant), soft delete por `company_status`.
- ✅ User = cuenta global: entidad/DTO **sin** `company_id` ni `role_id`; membresías y roles en fase 2.
- ✅ Role = los4 roles sembrados con `role_scope` respetado; `uq_role_name`/`uq_role_id_scope` intactos.
- ✅ Ámbitos `PLATFORM`/`COMPANY` respetados (lectura de rol efectivo distingue plataforma vs empresa).
- ✅ Correos únicos (BD + validación previa →409 + captura de `DataIntegrityViolation`).
- ✅ Contraseñas con BCrypt (`$2a$10$`), jamás en respuestas, logs ni DTOs.
- ✅ Sin creación pública de SUPER_ADMIN (no existe endpoint de asignación de roles; el body de registro no tiene campos de rol).
- ✅ Sin endpoints administrativos sin autorización efectiva (401/403 verificados en tests y E2E).

## 10. Pendientes

1. **Fase 2:** módulos `company_user`, `platform_user_role`, `company_user_role`; asignación de membresías/roles; endpoints de la matriz `security-and-tests.md` (`SECRETARY`/`PARTNER`/`DRIVER` por recurso).
2. **Autenticación:** login/refresh (JWT u otro) y recuperación de contraseña; hoy se usa HTTP Basic.
3. **Migraciones:** introducir Flyway/Liquibase con baseline del esquema existente (acordado posponer).
4. **Configuración:** credenciales `root/root` en claro en `application.properties` (configuración preexistente) → migrar a variables de entorno antes de cualquier despliegue.
5. **Endpoints no implementados por diseño:** `DELETE` de roles/usuarios/empresas (soft delete disponible para empresa/usuario vía status; roles sin columna de baja).
6. **Cambio de email** de usuario no habilitado (identidad de login inmutable en fase 1); paginación/filtros avanzados de empresas.
7. **Tests de integración con Testcontainers** (sugeridos por la Skill) no incluidos: se optó por unitarios + validación de contexto contra MySQL real (decisión aprobada).
8. **No se realizó ningún commit** (solo se ejecuta bajo petición explícita). Los120 borrados legados siguen stageados como se decidió.

## 11. Comandos ejecutados (resumen)

```
mysql8 (SHOW CREATE TABLE / information_schema / pruebas negativas con ROLLBACK)  → OK
mvn clean test        → Tests run: 29, Failures: 0, Errors: 0 — BUILD SUCCESS
curl E2E contra la app real (9 escenarios)                                        → OK
SELECT/DELETE de limpieza de datos de prueba en truck_sass                       → 0 filas residuales
```
