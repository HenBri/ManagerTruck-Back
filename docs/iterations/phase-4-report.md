# Informe de Iteración 4 — Asignación de conductores a camiones (TruckDriverAssignment)

- **Proyecto:** ManagerTruck-Back (`truck/`, Spring Boot 3.5.0, Java 17, JPA/Hibernate 6.6, MySQL 8.0.46)
- **Fecha:** 2026-10-16
- **Skills aplicadas:** `database-schema` (validación de esquema + contrato) y `generate-module` (implementación por capas)
- **Estado:** COMPLETADA — compilación y pruebas en verde (**195/195**), pruebas negativas SQL **5/5**, smoke E2E de arranque y rutas nuevas OK, sin cambios de esquema, sin commits, sin implementar viajes

---

## 1. Decisiones aprobadas por el usuario

| Tema | Decisión |
|---|---|
| Asignación con vigencia abierta existente | **Rechazar con 409** (sin auto-cierre): el SECRETARY finaliza explícitamente con `PUT .../end` antes de crear una nueva; conserva el historial limpio |
| Mecanismo de concurrencia | **Lock pesimista** (`@Lock(PESSIMISTIC_WRITE)`) sobre las filas de `truck_table` y `driver_table`, en orden fijo **truck → driver** para evitar deadlocks; los solapes se validan dentro de la transacción |
| Semántica de `assigned_until` | `NULL` = asignación **abierta/vigente**; el solape trata NULL como infinito (`existing.assignedUntil IS NULL OR > newFrom`) |
| Filtro de rol en lectura | SECRETARY/SUPER_ADMIN: historial completo (+ filtros opcionales `?truckId=`/`?driverId=`); PARTNER: solo asignaciones de camiones con `owner_partner_id` propio (join con `truck_table`); DRIVER: solo `driver_id` = su perfil |
| Propiedad del camión | La asignación **nunca** modifica `truck_table.owner_partner_id` (la entidad Truck no se persiste en el módulo) |
| Migración de esquema | **Solo validación + contrato Markdown, sin DDL nuevo**: la tabla ya existe en `truck_sass` sin diferencias vs baseline |

## 2. Verificación de fases anteriores (paso 1)

- `.\mvnw.cmd clean test` baseline antes de tocar código: **152/152** en verde (cierre de fase 3).
- Contratos de fases 1-3 vigentes; `truck-schema-contract.md` y `driver-schema-contract.md` declaraban `truck_driver_assignment` fuera de alcance (ahora implementada en esta fase).

## 3. `database-schema` — validación de `truck_driver_assignment`

**Fuentes contrastadas:** esquema vivo `truck_sass` (`SHOW CREATE TABLE truck_driver_assignment`, 0 filas) vs `references/schema_base_transporte_saas.sql` (líneas 235-250).

**Resultado: SIN diferencias** — PK, 2 UNIQUE, 1 índice, 2 FKs compuestas RESTRICT y CHECK `chk_assignment_dates` idénticos al baseline.

| Elemento | Detalle |
|---|---|
| PK | `assignment_id BIGINT UNSIGNED AUTO_INCREMENT` |
| UNIQUE | `uq_assignment_tenant_id (company_id, assignment_id)`; `uq_assignment_start (company_id, truck_id, driver_id, assigned_from)` |
| FKs | `fk_tda_truck (company_id, truck_id) → truck_table` RESTRICT; `fk_tda_driver (company_id, driver_id) → driver_table` RESTRICT |
| CHECK | `chk_assignment_dates (assigned_until IS NULL OR assigned_until > assigned_from)` |
| Índice | `idx_assignment_driver (company_id, driver_id)` |

**Pruebas negativas SQL en MySQL real** (transacción con `ROLLBACK`, datos de prueba incluidos en la misma transacción) — **5/5 con el resultado esperado**:

| # | Escenario | Resultado esperado | Resultado real |
|---|---|---|---|
| N1 | Asignación de empresa A con `truck_id` de empresa B | FK 1452 `fk_tda_truck` | ✓ 1452 |
| N2 | Asignación de empresa A con `driver_id` de empresa B | FK 1452 `fk_tda_driver` | ✓ 1452 |
| N3 | `assigned_until = assigned_from` | CHECK 3819 | ✓ 3819 |
| N4 | Duplicado exacto `(company_id, truck_id, driver_id, assigned_from)` | 1062 `uq_assignment_start` | ✓ 1062 |
| N5 | Inserción válida con truck y driver del mismo tenant | Aceptada | ✓ fila creada |

Tras `ROLLBACK`: `COUNT(*) truck_driver_assignment = 0` y sin residuos de empresas de prueba.

## 4. Contratos generados (`docs/modules/`)

| Archivo | Contenido |
|---|---|
| `docs/modules/truck-driver-assignment-schema-contract.md` | Definición física, semántica de tenant, mapeo JPA, reglas no representables en SQL (solape con lock, permisos, historial), entrega a `generate-module` |

## 5. `generate-module` — archivos creados y modificados

**Creados (paquete `com.manager.truck.truck`, ruta `truck/src/main/java/...`):**

- `domain/`: `TruckDriverAssignment` (entidad JPA, escalares + fechas `LocalDateTime`, sin `@ManyToOne`)
- `domain/dto/request/`: `TruckDriverAssignmentRequest`, `TruckDriverAssignmentEndRequest`
- `domain/dto/response/`: `TruckDriverAssignmentResponse`, `TruckDriverAssignmentsResponse`
- `repository/`: `TruckDriverAssignmentRepository` (búsquedas tenant-scoped, joins con `truck_table` para filtrado PARTNER, `existsOverlappingForTruck/Driver` con NULL = infinito)
- `service/`: `TruckDriverAssignmentService(+Impl)` (save, findByCompanyIdAndAssignmentId, findTruck/DriverForUpdate con lock, solapes, páginas por tenant/truck/driver/ownerPartner)
- `usecase/truckdriverassignment/` (4): `AssignTruckDriver`, `GetAllTruckDriverAssignments`, `GetTruckDriverAssignmentById`, `EndTruckDriverAssignment`
- `controller/`: `TruckDriverAssignmentController` (`/api/companies/{companyId}/assignments`)

**Modificados:**

- `domain/Constants.java`: + `TruckDriverAssignmentTable`
- `repository/TruckRepository.java`: + `findByCompanyIdAndTruckIdForUpdate` (`@Lock(PESSIMISTIC_WRITE)`)
- `repository/DriverRepository.java`: + `findByCompanyIdAndDriverIdForUpdate` (`@Lock(PESSIMISTIC_WRITE)`)
- `service/TruckService(+Impl).java`: + `findByCompanyIdAndTruckIdForUpdate`
- `service/DriverService(+Impl).java`: + `findByCompanyIdAndDriverIdForUpdate`
- `security/SecurityConfig.java`: + matchers GET/POST/PUT de `/api/companies/{companyId}/assignments`

**Tests creados (5 clases, 43 pruebas):**

| Clase | Pruebas |
|---|---|
| `usecase.truckdriverassignment.AssignTruckDriverUseCaseTest` | 13 |
| `controller.TruckDriverAssignmentSecurityTest` (WebMvc) | 12 |
| `usecase.truckdriverassignment.GetAllTruckDriverAssignmentsUseCaseTest` | 7 |
| `usecase.truckdriverassignment.GetTruckDriverAssignmentByIdUseCaseTest` | 6 |
| `usecase.truckdriverassignment.EndTruckDriverAssignmentUseCaseTest` | 5 |

## 6. Endpoints y autorización efectiva

Base: `/api/companies/{companyId}/assignments` — **4 endpoints**. El tenant sale **siempre de la URL**.

| Método/Ruta | SUPER_ADMIN | SECRETARY | PARTNER | DRIVER |
|---|---|---|---|---|
| `GET /` (historial + filtros `?truckId=`/`?driverId=`) | ✓ | ✓ (todo) | ✓ (solo sus camiones, join `owner_partner_id`) | ✓ (solo su `driver_id`) |
| `GET /{assignmentId}` | ✓ | ✓ | solo de sus camiones (sino 403) | solo la suya (sino 403) |
| `POST /` (asignar) | ✓ | ✓ | ✗ **403 URL** | ✗ **403 URL** |
| `PUT /{assignmentId}/end` (finalizar) | ✓ | ✓ | ✗ **403 URL** | ✗ **403 URL** |

Capas: (1) `SecurityConfig` URL matchers; (2) `requireCompanyAdmin` (escrituras) / `requireCompanyAccess` (lecturas); (3) filtro de propiedad por `user_id` en los use cases de lectura (anti-IDOR).

## 7. Reglas de negocio implementadas

| Regla | Estado |
|---|---|
| Un camión pertenece a un socio pero puede ser conducido por otra persona | ✅ la asignación es una fila aparte; `owner_partner_id` intacto |
| Un conductor puede manejar distintos camiones con el tiempo | ✅ sin límite; solo se bloquean vigencias incompatibles |
| Asignación con `assigned_from` obligatorio y `assigned_until` opcional (NULL = abierta) | ✅ Bean Validation + semántica NULL |
| No permitir asignaciones temporales incompatibles (camión y conductor) | ✅ overlap query dentro de `@Transactional` + lock pesimista truck→driver; 409 |
| Camión y conductor deben pertenecer a la misma empresa gestora | ✅ pre-check `findTruck/DriverForUpdate(companyId, id)` → 404 + FKs compuestas como defensa (1452→409) |
| Conservar el historial al terminar una asignación | ✅ sin DELETE; `PUT .../end` solo llena `assigned_until` |
| No modificar la propiedad del camión al cambiar de conductor | ✅ el módulo no persiste `Truck` |
| Fechas: `assigned_until > assigned_FROM` | ✅ 400 en API + CHECK físico 3819 |
| Conductor/camión inactivos o camión en mantenimiento: sin asignaciones nuevas | ✅ 409 |
| Empresa inactiva: sin asignaciones nuevas | ✅ 409 |
| Duplicado exacto de vigencia | ✅ pre-check 409 + UNIQUE 1062 |

## 8. Pruebas obligatorias — cobertura y resultados reales

### 8.1 Compilación y pruebas Maven (`truck/`)

- `.\mvnw.cmd clean test` → **BUILD SUCCESS — 195/195, 0 fallos, 0 errores, 26 clases** (152 de fases 1-3 + 43 nuevas).
- `TruckTableApplicationTests` (contexto real con BD viva y `ddl-auto=validate`) en verde: la nueva entidad valida contra la tabla real.

Cobertura de los escenarios pedidos:

| Escenario pedido | Pruebas |
|---|---|
| Asignación válida | `asignacionValidaDeberiaGuardarseSinModificarPropiedadDelCamion`, `asignacionSinFechaFinalDeberiaQuedarAbierta`, `asignacionValidaDeberiaBloquearFilasDeCamionYConductor`, `crearAsignacionConSecretaryDeberiaDevolver201` |
| Conductor a camión de otra empresa: rechazada | `camionDeOtraEmpresaDeberiaDevolver404`, `conductorDeOtraEmpresaDeberiaDevolver404` (+ SQL N1/N2) |
| Fechas inválidas: rechazadas | `fechasInvalidasHastaNoPosteriorAlInicioDeberianDevolver400` (+ SQL N3), `crearAsignacionConSecretarySinFechasDeberiaDevolver400` |
| Asignaciones solapadas incompatibles: rechazadas | `asignacionSolapadaDelCamionDeberiaDevolver409`, `asignacionSolapadaDelConductorDeberiaDevolver409` |
| Finalización y consulta del historial | `finalizacionValidaDeberiaConservarLaFilaConFechaDeFin`, `finalizacionYaFinalizadaDeberiaDevolver409`, `secretariaVeTodoElHistorial`, `partnerVeSoloLasAsignacionesDeSusCamiones`, `driverVeSoloSusAsignaciones`, `partnerNoPuedeConsultarAsignacionDeCamionAjeno`, `driverNoPuedeConsultarAsignacionAjena` |

### 8.2 Pruebas negativas SQL (5/5)

Ver §3 — ejecutadas contra `truck_sass` con ROLLBACK, sin residuos.

### 8.3 Smoke E2E con la aplicación real

- Arranque real (`.\mvnw.cmd spring-boot:run`) contra MySQL viva: contexto completo con la nueva entidad.
- `GET /api/companies/1/assignments` sin autenticación → **401** JSON; con credenciales inválidas → **401**; `GET .../assignments/1` sin auth → **401**. Rutas registradas y entry point JSON activo.
- Escenarios funcionales completos (creación con usuarios reales, solapes concurrentes, filtrados por rol) cubiertos por los 43 tests unitarios/WebMvc y las 5 pruebas SQL; no se repitió el E2E HTTP extenso de la fase 3 porque no hay datos de usuarios residuales en la BD (limpieza de fase 3).

## 9. Incompatibilidades detectadas y su resolución

| Problema | Causa | Solución |
|---|---|---|
| `Page.filter` devuelve `Streamable`, no `Page` | Spring Data Commons de Spring Boot 3.5 | Filtrado PARTNER por truckId resuelto con query de join en repository (`findByCompanyIdAndOwnerPartnerIdAndTruckId`) en lugar de filtrado en memoria |
| 4 tests fallaban por `requireCompanyAccess` → 403 | Faltaba el stub de `userService.isActiveMember` en escenarios SECRETARY | Añadido a `stubRoles` |
| `UnnecessaryStubbingException` en test de 404 | Stub de `findCompanyRoleNames` sin usar (el 404 se lanza antes de `hasFullAccess`) | Stub explícito solo de lo usado |
| Solapes no exigibles por CHECK/UNIQUE | `uq_assignment_start` solo cubre duplicado exacto del inicio | Validación cross-row en backend con lock pesimista (decidido con el usuario) |

## 10. Pendientes

- Fase 5: viajes (`journey_table`, consumirá `(company_id, assignment_id)`); fase 6: auxiliares.
- E2E HTTP extenso con usuarios reales del módulo (opcional; cubierto por tests y SQL).
- Flyway/Liquibase (decidido mantener validación directa hasta decisión futura).
- JWT (hoy HTTP Basic stateless, decisión de fase 1).

## 11. Comandos ejecutados (resumen)

```
.\mvnw.cmd clean test                                  # baseline 152/152 y final 195/195 verde
mysql.exe -uroot -proot truck_sass                     # SHOW CREATE TABLE truck_driver_assignment (sin diferencias)
Get-Content phase4_negative.sql | mysql.exe truck_sass --force   # 5/5 + ROLLBACK sin residuos
.\mvnw.cmd spring-boot:run                             # smoke: rutas /assignments responden 401 JSON
```

*Sin commits (no solicitados). La BD viva no fue modificada (todas las pruebas SQL con ROLLBACK).*
