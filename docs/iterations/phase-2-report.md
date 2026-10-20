# Informe de Iteración 2 — Autenticación y autorización SaaS (CompanyUser, CompanyUserRole, PlatformUserRole)

- **Proyecto:** ManagerTruck-Back (`truck/`, Spring Boot 3.5.0, Java 17, JPA/Hibernate 6.6, MySQL 8.0.46)
- **Fecha:** 2026-10-16
- **Skills aplicadas:** `database-schema` (validación de esquema + contratos) y `generate-module` (implementación por capas)
- **Estado:** COMPLETADA — compilación y pruebas en verde (**80/80**), E2E 18/18, sin cambios de esquema, sin commits, sin avanzar a fase 3

---

## 1. Decisiones aprobadas por el usuario

| Tema | Decisión |
|---|---|
| Mecanismo de autenticación | **Reutilizar HTTP Basic stateless + BCrypt existente** de fase 1 (sin JWT, sin dependencias nuevas) |
| Aprovisionamiento de SUPER_ADMIN | **Procedimiento manual seguro** documentado abajo (§6); **sin endpoint de escritura** sobre `platform_user_role`; solo lectura `GET /api/platform/user-roles` para SUPER_ADMIN |
| Administración de membresías/roles | **SUPER_ADMIN + SECRETARY de la empresa** (tenant-scoped; el tenant sale de la ruta, nunca del body) |
| Revocación de roles | **Sin DELETE** y **sin columna de estado**: `company_user_role` queda tal cual existe hoy |
| Migración de esquema | **"No agregar nada, dejarlo como está"**: sin Flyway, sin DDL; las 3 tablas ya existen en `truck_sass` (fase 1) — solo validación |

## 2. Verificación de Company, User y Role (paso 1)

- `mvn clean test` baseline antes de tocar código: **29/29** en verde (idéntico al cierre de fase 1).
- Contratos de fase 1 (`company`, `user`, `role`) vigentes y sin cambios; los 4 roles sembrados intactos (`role_table = 4`).

## 3. `database-schema` — validación de las 3 tablas de fase 2

**Fuentes contrastadas:** esquema vivo `truck_sass` (`SHOW CREATE TABLE`, `information_schema.COLUMNS/STATISTICS/REFERENTIAL_CONSTRAINTS`) vs `references/schema_base_transporte_saas.sql`.

**Resultado: SIN diferencias carácter a carácter** para `platform_user_role`, `company_user` y `company_user_role`:

| Tabla | PK | FKs | CHECK | Índice |
|---|---|---|---|---|
| `platform_user_role` | `(user_id, role_id)` | `fk_pur_user→user_table` RESTRICT; `fk_pur_role_scope (role_id, role_scope)→role_table` RESTRICT | `chk_pur_scope: role_scope='PLATFORM'` | `idx_pur_role(role_id, role_scope)` |
| `company_user` | `(company_id, user_id)` | `fk_cu_company→company` RESTRICT; `fk_cu_user→user_table` RESTRICT | — | `idx_membership_user(user_id)` |
| `company_user_role` | `(company_id, user_id, role_id)` | `fk_cur_membership (company_id,user_id)→company_user` **CASCADE**; `fk_cur_role_scope→role_table` RESTRICT | `chk_cur_scope: role_scope='COMPANY'` | `idx_cur_role(role_id, role_scope)` |

Columnas/DEFAULT verificados: `membership_status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE'`; `role_scope` DEFAULT `PLATFORM`/`COMPANY` según tabla; `joined_at`/`granted_at DATETIME DEFAULT CURRENT_TIMESTAMP`. `sql_mode` con `STRICT_TRANS_TABLES`.

**Pruebas negativas en MySQL real** (transacciones con `ROLLBACK`, sin residuos) — **8/8 con el error esperado**:

| # | Caso | Error obtenido | Esperado |
|---|---|---|---|
| 1 | PK duplicada `company_user` | `ERROR 1062 ... 'company_user.PRIMARY'` | ✅ |
| 2 | `company_user.user_id` inexistente | `ERROR 1452 ... fk_cu_user` | ✅ |
| 3 | `role_scope='PLATFORM'` en `company_user_role` | `ERROR 3819 Check constraint 'chk_cur_scope'` | ✅ |
| 4 | Asignar SUPER_ADMIN (role_id=1, scope PLATFORM) a una empresa | `ERROR 3819 chk_cur_scope` | ✅ |
| 5 | `role_scope='COMPANY'` en `platform_user_role` | `ERROR 3819 chk_pur_scope` | ✅ |
| 6 | `platform_user_role.user_id` inexistente | `ERROR 1452 fk_pur_user` | ✅ |
| 7 | PK duplicada `platform_user_role` | `ERROR 1062 ... 'platform_user_role.PRIMARY'` | ✅ |
| 8 | Rol sin membresía previa (`company_user_role`) | `ERROR 1452 fk_cur_membership` | ✅ |
| + | Inserción válida: membresía + **dos roles (PARTNER+DRIVER)** | `VALID_INSERT_OK` | ✅ |

**Conteos antes/después idénticos:** `company=0, user_table=0, company_user=0, company_user_role=0, platform_user_role=0, role_table=4`.

## 4. Contratos generados (`docs/modules/`)

- `company-user-schema-contract.md`
- `company-user-role-schema-contract.md`
- `platform-user-role-schema-contract.md`

Todos con estado **Migrado/Satisface contrato**, fuente = esquema vivo `truck_sass`, definición física completa (PK/FK compuestas, CHECK, ENUM, índices), tenant/autorización, mapeo JPA (`@EmbeddedId`), reglas no representables en SQL, plan de migración (ninguno) y pruebas SQL ejecutadas (§3).

## 5. `generate-module` — archivos creados y modificados

Ruta base `truck/src/main/java/com/manager/truck/truck/`. **34 archivos nuevos en `main`, 6 modificados, 6 clases de test nuevas (51 tests), 1 test modificado.**

| Paquete | Nuevos | Modificados |
|---|---|---|
| `domain/` | `CompanyUser`, `CompanyUserId`, `CompanyUserRole`, `CompanyUserRoleId`, `PlatformUserRole`, `PlatformUserRoleId`, `enums/MembershipStatus` | `Constants` (+3 tablas con nombres SQL reales) |
| `repository/` | `CompanyUserRepository`, `CompanyUserRoleRepository`, `PlatformUserRoleRepository` | — |
| `service/` | `CompanyUserService(+Impl)`, `CompanyUserRoleService(+Impl)`, `PlatformUserRoleService(+Impl)` | `RoleService(+Impl)` (+`findByRoleName`) |
| `domain/dto/request/` | `CompanyMembershipRequest`, `CompanyMembershipStatusRequest`, `CompanyUserRolesRequest` | — |
| `domain/dto/response/` | `CompanyMembershipResponse`, `CompanyMembershipsResponse`, `CompanyUserRolesResponse`, `MyMembershipsResponse`, `PlatformUserRoleResponse`, `PlatformUserRolesResponse` | — |
| `usecase/companyuser/` | `SaveCompanyUserUseCase`, `GetCompanyMembershipsUseCase`, `UpdateCompanyMembershipStatusUseCase`, `GetMyMembershipsUseCase` | — |
| `usecase/companyuserrole/` | `AssignCompanyUserRolesUseCase`, `GetCompanyUserRolesUseCase` | — |
| `usecase/platformuserrole/` | `GetPlatformUserRolesUseCase` | — |
| `controller/` | `CompanyMembershipController`, `PlatformUserRoleController` | `UserController` (+`GET /api/users/me/memberships`) |
| `security/` | — | `SecurityConfig` (+reglas URL), `CurrentUserProvider` (+guard central `requireCompanyAdmin`) |
| `src/test/` | `AuthenticationFlowTest`(6), `CompanyMembershipSecurityTest`(19), `SaveCompanyUserUseCaseTest`(7), `UpdateCompanyMembershipStatusUseCaseTest`(5), `AssignCompanyUserRolesUseCaseTest`(8), `GetCompanyUserRolesUseCaseTest`(6) | `SecurityAccessControlTest` (+1 `@MockitoBean`) |

- Mapeo JPA: **`@Embeddable` + `@EmbeddedId`** en las 3 PK compuestas; `role_id` con `@JdbcTypeCode(SqlTypes.SMALLINT)`; `joined_at`/`granted_at` con `insertable=false, updatable=false`; sin relaciones JPA (estilo plano de fase 1).
- **Sin cambios de esquema, sin dependencias nuevas en `pom.xml`.**
- Las queries nativas de authorities de `UserRepository` (fase 1) se mantienen: ya exigen `membership_status='ACTIVE'`.

## 6. Endpoints y autorización efectiva

| Método/Ruta | SecurityConfig (URL) | Guard en use case (defensa en profundidad) |
|---|---|---|
| `GET /api/companies/{cid}/memberships` | SUPER_ADMIN o SECRETARY | `requireCompanyAdmin(cid)` — SECRETARY debe tener membresía ACTIVE y rol SECRETARY **en ese cid** |
| `POST /api/companies/{cid}/memberships` | SUPER_ADMIN o SECRETARY | + empresa ACTIVE, cuenta destino ACTIVE, 409 si ya es miembro |
| `PUT /api/companies/{cid}/memberships/{uid}/status` | SUPER_ADMIN o SECRETARY | + membresía existe (404), 409 si estado repetido; **UPDATE, nunca DELETE** |
| `GET /api/companies/{cid}/memberships/{uid}/roles` | autenticado | SUPER_ADMIN **o** SECRETARY del cid **o** el propio usuario con membresía ACTIVE; si no hay membresía → 404 |
| `POST /api/companies/{cid}/memberships/{uid}/roles` | autenticado | `requireCompanyAdmin(cid)` + membresía ACTIVE (409) + cada rol `role_scope=COMPANY` (**403 si es SUPER_ADMIN/PLATFORM**); acepta N roles en una llamada (idempotente) |
| `GET /api/users/me/memberships` | autenticado | solo las membresías y roles **propios** (multiempresa) |
| `GET /api/platform/user-roles` | SUPER_ADMIN | + `isSuperAdmin()` (solo lectura; **no existe escritura**) |
| resto de rutas | `anyRequest().authenticated()` | (los `.../roles` a nivel URL solo exigen autenticación porque el propio miembro debe poder leerse; el control real es el guard) |

Sin credenciales → **401 JSON** `{"code":"401","message":"UNAUTHORIZED"}`; rol/tenant insuficiente → **403 JSON**. Sesiones STATELESS, CSRF off, BCrypt (se conserva HTTP Basic de fase 1).

**Procedimiento seguro de aprovisionamiento de SUPER_ADMIN (decisión aprobada):** INSERT directo auditado en MySQL por el operador de la plataforma, únicamente sobre cuentas creadas vía alta controlada, p. ej.
`INSERT INTO platform_user_role (user_id, role_id, role_scope) VALUES (<user_id>, 1, 'PLATFORM');`
— con verificación previa del correo y `user_status='ACTIVE'`; nunca por endpoint, registro público ni script embarcado en la aplicación. El resultado observable es auditable vía `GET /api/platform/user-roles` (solo SUPER_ADMIN).

## 7. Pruebas obligatorias — cobertura y resultados reales

| Requisito | Prueba | Resultado |
|---|---|---|
| Inicio de sesión **válido** | `AuthenticationFlowTest.loginValidoDeberiaDevolver200` (HTTP Basic real contra `UserDetailsService` + BCrypt) + E2E #1 | ✅ 200 |
| Inicio de sesión **inválido** | `loginConPasswordIncorrectaDeberiaDevolver401`, `loginConEmailInexistenteDeberiaDevolver401`, `loginConCuentaInactivaDeberiaDevolver401`, `peticionSinCredencialesDeberiaDevolver401` + E2E #2 | ✅ 401 JSON |
| Acceso **autorizado y denegado por rol** | `CompanyMembershipSecurityTest` (19 tests: 401/403/200/201 por rol SECRETARY/SUPER_ADMIN/PARTNER/DRIVER) + tests de fase 1 intactos (15) | ✅ |
| Intento de **acceder a otra empresa** | `secretariaDeEmpresaANoPuedeOperarEnLaEmpresaB` (Assign), `...NoPuedeCrearMembresia...` (Save), `...NoPuedeCambiarEstado...` (Update) con `CurrentUserProvider` **real** + E2E #3 y #16 | ✅ 403 |
| **Varios roles al mismo usuario** | `secretariaDeEmpresaAPuedeAsignarVariosRolesEnSuEmpresa` (ArgumentCaptor: PARTNER+DRIVER) + E2E #4 | ✅ 200, `["PARTNER","DRIVER"]` |
| **Elevar privilegios a SUPER_ADMIN** | `intentoDeElevarPrivilegiosASuperAdminDeberiaSerRechazado` + E2E #5 y #17/18 (registro con `roleName` en body no crea grant PLATFORM) | ✅ 403 / grant=0 |
| **Membresías inactivas** | `asignarRolSobreMembresiaInactivaDeberiaDevolverConflicto` (409), `unMiembroInactivoNoPuedeConsultarRoles...` (403), `...CuentaInactiva...`, `...EmpresaInactiva...` + E2E #6, #7, #15 | ✅ |
| Regresión fase 1 | `SecurityAccessControlTest` (15) + `contextLoads` (**validación Hibernate de las 3 entidades nuevas contra `truck_sass`**) | ✅ |

### 7.1 Compilación y pruebas Maven (`truck/`)

```
mvn clean test
Tests run: 80, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS   (EXIT=0)
```

| Clase de test | Tests | Resultado |
|---|---|---|
| `CompanyMembershipSecurityTest` (`@WebMvcTest`) | 19 | ✅ matriz de endpoints nuevos |
| `SecurityAccessControlTest` (fase 1) | 15 | ✅ sin regresión |
| `AssignCompanyUserRolesUseCaseTest` (provider real + SecurityContext) | 8 | ✅ tenant, multi-rol, elevación, inactivos |
| `SaveCompanyUserUseCaseTest` | 7 | ✅ alta + duplicado 409 + cross-tenant |
| `AuthenticationFlowTest` (Basic real) | 6 | ✅ login válido/inválido/inactivo |
| `GetCompanyUserRolesUseCaseTest` | 6 | ✅ self/admin/inactivo/404 |
| `UpdateCompanyMembershipStatusUseCaseTest` | 5 | ✅ estado + 409 + cross-tenant |
| Fase 1 (5 clases) + `contextLoads` | 14 | ✅ |

### 7.2 E2E con la aplicación real (HTTP contra `localhost:8080`) — **18/18 OK**

Datos de prueba creados vía `POST /api/users` + SQL (2 empresas, membresías, SECRETARY, aprovisionamiento manual de SUPER_ADMIN) y **eliminados al terminar**.

| # | Escenario | HTTP esperado/obtenido |
|---|---|---|
| 1 | Login válido (Basic) | 200 ✅ |
| 2 | Password incorrecta | 401 ✅ |
| 3 | SECRETARY de A → membresías de B | 403 ✅ |
| 4 | Asignar `[PARTNER,DRIVER]` en A | 200 ✅ (ambos roles) |
| 5 | Asignar rol `SUPER_ADMIN` | 403 ✅ |
| 6 | Asignar rol sobre membresía INACTIVE | 409 ✅ |
| 7 | Miembro INACTIVE lee sus roles propios | 403 ✅ |
| 8 | Miembro activo lee sus roles propios | 200 ✅ |
| 9 | `GET /api/platform/user-roles` con SECRETARY | 403 ✅ |
| 10 | `GET /api/platform/user-roles` con SUPER_ADMIN | 200 ✅ |
| 11 | `GET /api/users/me/memberships` con 2 empresas | 200 ✅ (A con roles, B sin roles) |
| 12 | SUPER_ADMIN no miembro lista membresías de A | 200 ✅ |
| 13 | SECRETARY crea membresía | 201 ✅ |
| 14 | Membresía duplicada | 409 ✅ |
| 15 | SECRETARY pone membresía INACTIVE | 200 ✅ |
| 16 | SECRETARY de A asigna roles en B | 403 ✅ |
| 17-18 | Registro con `roleName:"SUPER_ADMIN"` + `companyId` en body | 201 sin grant (0 filas PLATFORM) ✅ |

**Verificación SQL posterior:** `password_hash` solo con prefijo BCrypt; `role_table=4` intacto; `company=0, user_table=0, company_user=0, company_user_role=0, platform_user_role=0` tras la limpieza.

## 8. Incompatibilidades detectadas y su resolución

| Incompatibilidad | Resolución |
|---|---|
| Spring Data **no resuelve queries derivadas** sobre propiedades de `@EmbeddedId` (`No property 'companyId' found for type 'CompanyUser'`) — rompía el arranque (`contextLoads` y todos los `@WebMvcTest`) | `@Query` JPQL explícito con `cu.id.companyId` / `cur.id.*`; `exists...` vía `isPresent()`/`anyMatch` sobre la lista |
| `@MockitoBean AuthUserDetailsService` **no sustituía** el `UserDetailsService` (la autoconfig creaba usuario in-memory con password generado → login siempre 401) | `@TestConfiguration` con `UserDetailsService` propio de prueba + exclusión de `UserDetailsServiceAutoConfiguration` por propiedad en el test |
| `SecurityAccessControlTest` dejó de cargar el contexto: `UserController` ganó la dependencia `GetMyMembershipsUseCase` sin mock | `@MockitoBean GetMyMembershipsUseCase` añadido al test de fase 1 |
| NPE en tests: `save()` de Mockito devuelve `null` y `Response.from(...)` lo desreferencia | Stub `thenAnswer(inv -> inv.getArgument(0))` |
| `UnnecessaryStubbing` en tests cross-tenant (el stub de la empresa A no llegaba a usarse porque el guard lanza antes) | Stubs redundantes eliminados |
| `@PageableDefault(sort="userId")` inexistente como propiedad de `CompanyUser` (fallaría en runtime) | Cambiado a `sort="id.userId"` |

No hubo diferencias entre el esquema fase 2 y la BDD vigente (sin DDL, sin cláusula de detención aplicable).

## 9. Reglas de negocio — estado

- ✅ SUPER_ADMIN con permisos globales vía `platform_user_role` y **sin necesidad de pertenecer a empresas** (E2E #12).
- ✅ SECRETARY administra operaciones de **su** empresa (membresías y roles) — verificado contra otra empresa (403).
- ✅ PARTNER/DRIVER con acceso limitado a sus recursos (autoridad efectiva por empresa; los viajes llegan en fase 3).
- ✅ Un usuario puede pertenecer a **varias empresas** (PK compuesta; E2E #11).
- ✅ Un usuario puede tener **simultáneamente PARTNER y DRIVER** (PK de 3 componentes; E2E #4).
- ✅ SUPER_ADMIN no requiere pertenecer a todas las empresas.
- ✅ SUPER_ADMIN **solo** por procedimiento manual seguro; sin escritura vía API; registro público no eleva privilegios (E2E #17/18; CHECK `chk_cur_scope` como red de seguridad en BD).
- ✅ Identidad autenticada validada siempre; membresía activa y permisos por empresa comprobados en cada operación; `company_id` **solo** desde la ruta validada, nunca del body.
- ✅ Sin consultas empresariales globales sin autorización (listados siempre tenant-scoped; `GET /api/companies` sigue exigiendo SUPER_ADMIN).
- ✅ Denegación por defecto (`anyRequest().authenticated()` + guards por defecto a 403).
- ✅ Sin exponer contraseñas/hashes ni datos de otras empresas (DTOs sin `password_hash`; test `loginValido...NoDebeExponerElHash`).

## 10. Pendientes

1. **Fase 3:** módulos operativos (`client`, `contract`, `charge`, `container`, `partner`, `driver`, `truck`, `truck_driver_assignment`, `auxiliary_provider`, `journey`) con reglas PARTNER/DRIVER por recurso.
2. **Revocación de roles:** hoy solo alta/consulta (decisión: sin DELETE ni columna de estado) — requerirá modelo de estado o DELETE de la fila pivote.
3. **Migraciones:** Flyway/Liquibase con baseline sigue pendiente (acordado no introducir en esta iteración).
4. **Login/refresh (JWT) y recuperación de contraseña** siguen pendientes (fase 2 reutilizó HTTP Basic por decisión).
5. **Procedimiento formal del aprovisionamiento de SUPER_ADMIN** (doble control/herramienta externa); hoy es manual y documentado en §6.
6. Credenciales `root/root` en `application.properties` (preexistente) → variables de entorno antes de desplegar.
7. Tests de integración con Testcontainers (sugeridos por la Skill) no incluidos; se mantienen unitarios + contexto contra MySQL real + E2E (misma decisión que fase 1).
8. **Sin commits** (solo bajo petición explícita); los borrados legados de fase 1 siguen stageados como se decidió.

## 11. Comandos ejecutados (resumen)

```
mvn clean test (baseline)                       → 29/29 — BUILD SUCCESS
mysql8: SHOW CREATE TABLE / information_schema / 8 pruebas negativas con ROLLBACK → 8/8 OK, conteos 0/0/0/0/0/4
mvn clean test                                  → Tests run: 80, Failures: 0, Errors: 0 — BUILD SUCCESS
E2E HTTP contra localhost:8080 (18 escenarios)   → 18/18 OK
DELETE de limpieza de datos de prueba            → conteos residuales 0 en las 5 tablas, role_table=4
```
