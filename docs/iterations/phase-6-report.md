# Informe de Iteración 6 — Auxilios y gestión de viajes (AuxiliaryProvider, Journey)

- **Proyecto:** ManagerTruck-Back (`truck/`, Spring Boot 3.5.0, Java 17, JPA/Hibernate 6.6, MySQL 8.0.46)
- **Fecha:** 2026-10-16
- **Skills aplicadas:** `database-schema` (validación de esquema + contratos) y `generate-module` (implementación por capas)
- **Estado:** COMPLETADA — compilación y pruebas en verde (**429/429**), pruebas negativas SQL **9/9**, smoke E2E de arranque y rutas nuevas OK, **sin cambios de esquema**, sin commits

---

## 1. Decisiones aprobadas por el usuario

| Tema | Decisión |
|---|---|
| Transiciones de estado para DRIVER | **Solo `→IN_PROGRESS` y `→COMPLETED`**, y únicamente en sus viajes PARTNER asignados; crear, editar, cancelar y viajes AUXILIARY quedan para `SECRETARY`/`SUPER_ADMIN` |
| Cancelación | **Permitida** desde `PLANNED` e `IN_PROGRESS` → `CANCELLED`; `COMPLETED`/`CANCELLED` terminales |
| Integración con `container_state` | **Sin acoplar**: el viaje valida pertenencia del contenedor y que la carga no esté `CANCELLED`, pero **no muta** `container_state` (limitación documentada) |
| Viajes simultáneos | **Un solo viaje activo** (`PLANNED`/`IN_PROGRESS`) por asignación (camión+conductor) y por placa de auxilio; chequeo transaccional en backend (no hay constraint SQL posible) |

## 2. Verificación de fases anteriores (paso 1)

- `.\mvnw.cmd clean test` baseline antes de tocar código: **320/320** en verde (cierre de fase 5).
- Contratos de fases 1-5 vigentes en `docs/modules/`; `container-schema-contract.md` declaraba `journey_table` como consumidor futuro (ahora implementado).
- Jerarquía comercial intacta: `company → client → contract_table → charge_table → container_table → journey_table`.

## 3. `database-schema` — validación de las 2 tablas

**Fuentes contrastadas:** esquema vivo `truck_sass` (`SHOW CREATE TABLE auxiliary_provider` y `journey_table`; **0 filas** cada una) vs `references/schema_base_transporte_saas.sql` (líneas 256-327).

**Resultado: SIN diferencias** — PK, UNIQUE, índices, FKs compuestas RESTRICT y CHECK idénticos al baseline:

| Tabla | PK | UNIQUE | Índices | FKs (compuestas → destino) | CHECK |
|---|---|---|---|---|---|
| `auxiliary_provider` | `provider_id` AI | `uq_aux_provider_tenant_id` | `idx_aux_provider_name` | `fk_aux_provider_company (company_id) → company` | — |
| `journey_table` | `journey_id` AI | `uq_journey_tenant_id` | `idx_journey_container/start/assignment/provider` | `fk_journey_container`, `fk_journey_assignment`, `fk_journey_provider` (todas `(company_id, fk_id)`, RESTRICT) | `chk_journey_execution`, `chk_journey_amount`, `chk_journey_dates` |

**Sin DDL ni migración necesaria** (tablas ya desplegadas; Flyway/Liquibase sigue sin introducirse, decisión pendiente de fases anteriores).

**Pruebas negativas SQL en MySQL real** (transacción con `ROLLBACK`, empresas A/B y cadena completa de prueba incluida en la misma transacción) — **9/9 con el resultado esperado**:

| # | Escenario | Resultado esperado | Resultado real |
|---|---|---|---|
| N1 | Viaje PARTNER con placa/nombre auxiliares | 3819 `chk_journey_execution` | ✓ 3819 |
| N2 | Viaje AUXILIARY con `assignment_id` | 3819 `chk_journey_execution` | ✓ 3819 |
| N3 | `agreed_amount = -0.01` | 3819 `chk_journey_amount` | ✓ 3819 |
| N4 | `end_date < start_date` | 3819 `chk_journey_dates` | ✓ 3819 |
| N5 | `container_id` de otra empresa | 1452 `fk_journey_container` | ✓ 1452 |
| N6 | `assignment_id` de otra empresa | 1452 `fk_journey_assignment` | ✓ 1452 |
| N7 | `auxiliary_provider_id` de otra empresa | 1452 `fk_journey_provider` | ✓ 1452 |
| N8 | Viaje PARTNER válido + viaje AUXILIARY válido | Aceptados | ✓ 2 filas |
| N9 | AUXILIARY sin placa | 3819 `chk_journey_execution` | ✓ 3819 |

Tras `ROLLBACK`: companies/journeys/providers de prueba = **0/0/0** (sin residuos). El script temporal fue eliminado tras la ejecución (misma convención que la fase 5).

## 4. Contratos generados (`docs/modules/`)

| Archivo | Contenido |
|---|---|
| `auxiliary-provider-schema-contract.md` | Definición física, enums `provider_kind/provider_status`, nombre único por empresa (pre-check), regla "proveedor nunca socio" |
| `journey-schema-contract.md` | 3 FKs compuestas, exclusividad de modalidad (`chk_journey_execution`), máquina de estados, matriz de autorización por rol, regla de solapamiento, mapeo JPA |

## 5. `generate-module` — archivos creados y modificados

**Creados (paquete `com.manager.truck.truck`, ruta `truck/src/main/java/...`):**

- `domain/`: `AuxiliaryProvider`, `Journey` (entidades JPA escalares, sin `@ManyToOne`; `created_at` con `insertable=false, updatable=false`; `agreed_amount` `BigDecimal(15,2)`)
- `domain/enums/`: `AuxiliaryProviderKind`, `AuxiliaryProviderStatus`, `JourneyState`, `JourneyExecutionType`, `AuxiliaryReason` (valores exactos SQL)
- `domain/dto/request/` (8): `AuxiliaryProviderRequest/UpdateRequest/StatusRequest`, `JourneyRequest/JourneyUpdateRequest/JourneyStatusRequest`
- `domain/dto/response/` (4): `{AuxiliaryProvider,Journey}Response` + `{AuxiliaryProviders,Journeys}Response`
- `repository/` (2): `AuxiliaryProviderRepository` (tenant-scoped + filtros kind/status/name + unicidad de nombre), `JourneyRepository` (8 combos de filtros full-access; alcance PARTNER por `owner_partner_id` y DRIVER por `driver_id` con filtros opcionales; `existsActive...` por asignación y placa)
- `service/` (4): `AuxiliaryProviderService(+Impl)`, `JourneyService(+Impl)`
- `usecase/` (10): `auxiliaryprovider/` — `Save*`, `GetAll*`, `Get*ById`, `Update*`, `Update*Status`; `journey/` — `Save*`, `GetAll*`, `Get*ById`, `Update*`, `Update*Status`
- `controller/` (2): `AuxiliaryProviderController`, `JourneyController` (OpenAPI `@Tag/@Operation/@Parameter`)

**Modificados:**

- `domain/Constants.java`: + `AuxiliaryProviderTable` (9 columnas), `JourneyTable` (18 columnas) — nombres físicos exactos SQL
- `security/SecurityConfig.java`: + matchers de `/api/companies/{companyId}/auxiliary-providers/**` (solo `SUPER_ADMIN`/`SECRETARY`) y `/api/companies/{companyId}/journeys` (GET los 4 roles; POST/PUT `SUPER_ADMIN`/`SECRETARY`; `PUT .../status` además `DRIVER`)

## 6. Endpoints y autorización efectiva

**10 endpoints** (5 por módulo). El tenant sale **siempre de la ruta**; nunca del body.

| Ruta base | Endpoints |
|---|---|
| `/api/companies/{companyId}/auxiliary-providers` | `GET /` (`?kind=&status=&name=`), `GET /{providerId}`, `POST /`, `PUT /{providerId}`, `PUT /{providerId}/status` |
| `/api/companies/{companyId}/journeys` | `GET /` (`?containerId=&state=&executionType=`), `GET /{journeyId}`, `POST /`, `PUT /{journeyId}`, `PUT /{journeyId}/status` |

| Método/Ruta | SUPER_ADMIN | SECRETARY | PARTNER | DRIVER |
|---|---|---|---|---|
| `GET` auxiliary-providers | ✓ | ✓ | ✗ **403 URL** | ✗ **403 URL** |
| `POST`/`PUT` auxiliary-providers | ✓ | ✓ | ✗ **403 URL** | ✗ **403 URL** |
| `GET` journeys (alcance filtrado) | ✓ todos | ✓ todos | ✓ solo viajes de **sus camiones** | ✓ solo sus viajes **PARTNER asignados** |
| `POST` journeys | ✓ | ✓ | ✗ **403 URL** | ✗ **403 URL** |
| `PUT /{id}` (escalares, PLANNED) | ✓ | ✓ | ✗ **403 URL** | ✗ **403 URL** |
| `PUT /{id}/status` | ✓ | ✓ | ✗ **403 URL** | ✓ (ruta) → use case: solo `→IN_PROGRESS`/`→COMPLETED` y solo viajes propios PARTNER |

Capas de defensa: (1) `SecurityConfig` URL matchers; (2) `requireCompanyAdmin`/`requireCompanyAccess` + verificación de rol **por recurso** en use cases (PARTNER por `partner_table.user_id`, DRIVER por `driver_table.user_id`); (3) lecturas `findByCompanyIdAndJourneyId`/scope JPQL (anti-IDOR; viaje ajeno → 404); (4) FKs compuestas SQL como defensa final (1452→409). Los módulos comerciales de fase 5 **siguen sin acceso** para PARTNER/DRIVER (decisión vigente).

## 7. Reglas de negocio implementadas

| Regla | Estado |
|---|---|
| Un viaje = recorrido único A → B, sin tramos | ✅ sin `journey_leg` ni relevos; una fila por viaje |
| Un solo camión/conductor por viaje | ✅ PARTNER: una asignación (camión+conductor únicos); AUXILIARY: foto histórica de placa/conductor |
| Modalidades exclusivas PARTNER/AUXILIARY | ✅ validación backend 400 (mezcla en cualquier sentido) + `chk_journey_execution` (3819) |
| PARTNER exige asignación registrada | ✅ `assignment_id` obligatorio; resuelta en tenant (404); vigente al crear y **re-validada al iniciar** (409) |
| AUXILIARY usa proveedor externo | ✅ proveedor tenant (404) y ACTIVE (409); placa/nombre/motivo obligatorios (400); license y monto opcionales |
| No convertir auxiliares en socios | ✅ ningún código crea `partner_table`/`user_table`/roles |
| El viaje pertenece a la empresa dueña de la carga | ✅ contenedor resuelto en tenant; carga no `CANCELLED` (409); contenedor no `DELIVERED` (409); jerarquía intacta |
| Foto histórica del auxilio | ✅ placa, nombre, licencia, motivo y `agreed_amount` persistidos en el viaje |
| Máquina de estados estricta | ✅ `PLANNED→IN_PROGRESS→COMPLETED`; `CANCELLED` desde `PLANNED`/`IN_PROGRESS`; terminales; 409 en saltos/retrocesos/mismo estado |
| Fechas coherentes | ✅ start fijado al iniciar; end al completar; `end >= start` (409 + CHECK 3819); montos `BigDecimal` (400 Bean Validation + 3819) |
| Sin viajes simultáneos incompatibles | ✅ máx. 1 activo por asignación y por placa normalizada (409) |
| Autorización por recurso PARTNER/DRIVER | ✅ scope JPQL (`owner_partner_id` / `driver_id`); viaje ajeno → 404; DRIVER sin conductor vinculado → 403 |
| No acceder a viajes de otra empresa | ✅ `findByCompanyIdAndJourneyId` + FKs compuestas (1452) |
| Historial conservado | ✅ sin DELETE en ambos módulos; ciclo por estados/`provider_status` |

## 8. Pruebas obligatorias — cobertura y resultados reales

### 8.1 Compilación y pruebas Maven (`truck/`)

- `.\mvnw.cmd clean test` → **BUILD SUCCESS — 429/429, 0 fallos, 0 errores, 54 clases** (320 de fases 1-5 + **109 nuevas** en 12 clases).
- `TruckTableApplicationTests` (contexto real con BD viva y `ddl-auto=validate`) en verde: las 2 entidades nuevas **validan contra las tablas reales**.

Escenarios pedidos → pruebas:

| Escenario pedido | Pruebas |
|---|---|
| Viaje realizado por camión de socio | `secretariaCreaViajePartnerValido` (SaveJourney, 24 pruebas en la clase) |
| Viaje realizado mediante auxilio externo | `secretariaCreaViajeAuxiliaryValido` + validación de proveedor/placa/conductor/motivo |
| Intento de mezclar ambas modalidades | `viajePartnerConDatosAuxiliaresDeberiaDevolver400`, `viajeAuxiliaryConAssignmentDeberiaDevolver400` (+ SQL N1/N2 3819) |
| Asignación inválida o de otra empresa | `assignmentDeOtraEmpresa...404`, `assignmentNoVigente...409`, `assignmentExpirada...409`, `camionNoActivo...409`, `conductorNoActivo...409` |
| Solapamiento incompatible | `solapamientoPorAsignacionDeberiaDevolver409`, `solapamientoPorPlacaDeberiaDevolver409` |
| Actualización de estados | 19 pruebas de máquina (`plannedAInProgressFijaFechaInicio`, `completedEsTerminal...409`, `retroceso...409`, `salto...409`, `asignacionExpiradaNoPermiteIniciar...409`, …) |
| DRIVER: progreso autorizado/denegado | `driverPuedeIniciar/CompletarSuViajePartner`, `driverNoPuedeCancelar...403`, `driverNoPuedeVerViajeAjeno...404`, `driverSinConductorVinculado...403`, `driverNoPuedeActualizarViajeAuxiliary...403` |
| Acceso autorizado y denegado (WebMvc) | `AuxiliaryProviderSecurityTest` (12): 401/200/201/403 por rol; `JourneySecurityTest` (15): GET 200 los 4 roles, POST/PUT 403 PARTNER/DRIVER, `PUT status` 200 SECRETARY/DRIVER |
| Integración con contrato, carga y contenedor | `cargaCanceladaDeberiaDevolver409`, `contenedorDeliveredDeberiaDevolver409`, `contenedorDeOtraEmpresaDeberiaDevolver404` (+ SQL N5) |
| Proveedor de auxilio CRUD | 20 unitarias + 12 WebMvc (nombre duplicado 409, INACTIVE no ejecuta viajes 409, cross-tenant 404/403) |

Detalle de clases nuevas (109 pruebas):

| Clase | Pruebas |
|---|---|
| `usecase.auxiliaryprovider.SaveAuxiliaryProviderUseCaseTest` | 5 |
| `usecase.auxiliaryprovider.GetAllAuxiliaryProvidersUseCaseTest` | 4 |
| `usecase.auxiliaryprovider.GetAuxiliaryProviderByIdUseCaseTest` | 3 |
| `usecase.auxiliaryprovider.UpdateAuxiliaryProviderUseCaseTest` | 4 |
| `usecase.auxiliaryprovider.UpdateAuxiliaryProviderStatusUseCaseTest` | 4 |
| `controller.AuxiliaryProviderSecurityTest` | 12 |
| `usecase.journey.SaveJourneyUseCaseTest` | 24 |
| `usecase.journey.UpdateJourneyUseCaseTest` | 5 |
| `usecase.journey.UpdateJourneyStatusUseCaseTest` | 19 |
| `usecase.journey.GetAllJourneysUseCaseTest` | 7 |
| `usecase.journey.GetJourneyByIdUseCaseTest` | 7 |
| `controller.JourneySecurityTest` | 15 |

### 8.2 Pruebas negativas SQL (9/9)

Ver §3 — ejecutadas contra `truck_sass` con `ROLLBACK`, sin residuos.

### 8.3 Smoke E2E con la aplicación real

- Arranque real (`.\mvnw.cmd spring-boot:run`) contra MySQL viva: contexto completo con las 2 entidades nuevas (`ddl-auto=validate` en verde).
- `GET /api/companies/1/journeys`, `/journeys/1`, `/auxiliary-providers`, `/auxiliary-providers/1` sin autenticación → **401** en las 4 rutas; entry point JSON activo.
- Escenarios funcionales (creación de viajes por ambas modalidades, mezcla rechazada, máquina de estados, alcance PARTNER/DRIVER, cross-tenant) cubiertos por las 109 pruebas unitarias/WebMvc y las 9 pruebas SQL.

## 9. Incompatibilidades detectadas y su resolución

| Problema | Causa | Solución |
|---|---|---|
| Los enums creados vía PowerShell rompían la compilación (`illegal character '\ufeff'`) | PowerShell 5.1 escribió BOM UTF-8 en archivos nuevos | Reescritos los 5 enums sin BOM (`UTF8Encoding(false)`) |
| Tras añadir matchers de Journey/AuxiliaryProvider, **4 tests de fase 5 fallaron** (`cambiarEstado*ConPartnerDeberiaDevolver403` → 200) | La edición de `SecurityConfig` desplazó por error el bloque `PUT /clients|contracts|charges|containers|.../status` (solo `SUPER_ADMIN`/`SECRETARY`), dejando esas rutas caer en `anyRequest().authenticated()` | Restaurado el bloque PUT comercial y eliminado el duplicado de partners; suite completa vuelve a verde |
| Mockito self-attaching en JDK 21 (advertencia) | Preexistente (fases 1-5) | Sin cambio (fuera de alcance) |

## 10. Pendientes

- Consultas comerciales (clients/contracts/charges/containers) para DRIVER/PARTNER cuando se decida ampliar su alcance (hoy 403 por decisión de fase 5; los viajes no requirieron exponerlas).
- Flyway/Liquibase (decidido mantener validación directa hasta decisión futura).
- JWT (hoy HTTP Basic stateless, decisión de fase 1).

## 11. Limitaciones

- **Sin acople de `container_state`** (decisión aprobada): iniciar/completar un viaje **no** cambia el estado del contenedor; la sincronización queda como mejora futura.
- **Solapamiento** por recurso activo único (no por ventanas temporales de fechas): un viaje `PLANNED` sin fechas bloquea nuevas asignaciones activas del mismo camión/conductor/placa hasta completarse o cancelarse.
- La vigencia de la asignación se re-valida al **iniciar** el viaje, no en cada lectura.
- `journey_type` es una etiqueta `VARCHAR(50)` libre (sin catálogo).
- Un viaje transporta **un contenedor** (FK obligatoria del baseline); viajes multi-contenedor requerirían decisión funcional.

## 12. Comandos ejecutados (resumen)

```
.\mvnw.cmd clean test                                  # baseline 320/320 y final 429/429 verde
mysql.exe truck_sass  (SHOW CREATE TABLE)              # auxiliary_provider y journey_table sin diferencias vs baseline
mysql.exe truck_sass --force < phase6_negative.sql     # 9/9 (3819×5, 1452×3, 2 válidos) + ROLLBACK sin residuos
.\mvnw.cmd spring-boot:run                             # smoke: 401 en /journeys y /auxiliary-providers
```

*Sin commits (no solicitados). La BD viva no fue modificada (todas las pruebas SQL con ROLLBACK).*
