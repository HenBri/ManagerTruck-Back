# Informe de Iteración 5 — Gestión comercial y de cargas (Client, Contract, Charge, Container)

- **Proyecto:** ManagerTruck-Back (`truck/`, Spring Boot 3.5.0, Java 17, JPA/Hibernate 6.6, MySQL 8.0.46)
- **Fecha:** 2026-10-16
- **Skills aplicadas:** `database-schema` (validación de esquema + contratos) y `generate-module` (implementación por capas)
- **Estado:** COMPLETADA — compilación y pruebas en verde (**320/320**), pruebas negativas SQL **7/7**, smoke E2E de arranque y rutas nuevas OK, sin cambios de esquema, sin commits, sin implementar viajes

---

## 1. Decisiones aprobadas por el usuario

| Tema | Decisión |
|---|---|
| Acceso de PARTNER y DRIVER a los módulos comerciales | **Sin acceso (403 en URL)** para GET/POST/PUT: ninguna consulta comercial autorizada en esta fase; solo `SUPER_ADMIN` y `SECRETARY` |
| Estados de `contract_state` / `charge_state` / `container_state` | **Transiciones estrictas** validadas en backend (el SQL acepta cualquier valor del enum); retrocesos/saltos → 409 |
| Reglas de creación en cadena | Contrato requiere cliente **ACTIVE** (409); carga requiere contrato **ACTIVE** (409 si DRAFT/COMPLETED/CANCELLED); contenedor requiere carga **no CANCELLED** (409) |
| Historial comercial | **Sin DELETE** en ningún módulo; la vigencia/ciclo de vida se expresa con estados (`client_state` booleano; enums del SQL en las demás tablas) |
| `client_id` en `charge_table` | **No se duplica**: la carga solo guarda `contract_id`; el cliente se deriva del contrato (jerarquía exacta del esquema) |

## 2. Verificación de fases anteriores (paso 1)

- `.\mvnw.cmd clean test` baseline antes de tocar código: **195/195** en verde (cierre de fase 4).
- Contratos de fases 1-4 vigentes en `docs/modules/`; `truck-driver-assignment-schema-contract.md` declaraba `client/contract_table/charge_table/container_table` fuera de alcance de la fase 4 (ahora implementadas).
- Esquema vivo verificado: las 4 tablas comerciales **ya existían** en `truck_sass` con 0 filas.

## 3. `database-schema` — validación de las 4 tablas

**Fuentes contrastadas:** esquema vivo `truck_sass` (`SHOW CREATE TABLE` de `client`, `contract_table`, `charge_table`, `container_table`; 0 filas) vs `references/schema_base_transporte_saas.sql` (líneas 94-159).

**Resultado: SIN diferencias** — PK, UNIQUEs, índices, FKs compuestas RESTRICT y CHECK idénticos al baseline:

| Tabla | PK | UNIQUE | Índice | FK (compuesta → destino) | CHECK |
|---|---|---|---|---|---|
| `client` | `client_id` AI | `uq_client_tenant_id (company_id, client_id)` | `idx_client_company_name` | `fk_client_company (company_id) → company` | — |
| `contract_table` | `contract_id` AI | `uq_contract_tenant_id` | `idx_contract_client` | `fk_contract_client (company_id, client_id) → client` | `chk_contract_amount >= 0` |
| `charge_table` | `charge_id` AI | `uq_charge_tenant_id` | `idx_charge_contract` | `fk_charge_contract (company_id, contract_id) → contract_table` | — |
| `container_table` | `container_id` AI | `uq_container_tenant_id`, `uq_container_company_code (company_id, container_code)` | `idx_container_charge` | `fk_container_charge (company_id, charge_id) → charge_table` | `chk_container_weight (NULL OR >= 0)` |

**Pruebas negativas SQL en MySQL real** (transacción con `ROLLBACK`, empresas/clientes/contratos/cargas de prueba incluidos en la misma transacción) — **7/7 con el resultado esperado**:

| # | Escenario | Resultado esperado | Resultado real |
|---|---|---|---|
| N1 | Contrato de empresa A con `client_id` de empresa B | FK 1452 `fk_contract_client` | ✓ 1452 |
| N2 | Carga de empresa A con `contract_id` de empresa B | FK 1452 `fk_charge_contract` | ✓ 1452 |
| N3 | Contenedor de empresa A con `charge_id` de empresa B | FK 1452 `fk_container_charge` | ✓ 1452 |
| N4 | `contract_amount = -0.01` | CHECK 3819 `chk_contract_amount` | ✓ 3819 |
| N5 | `container_weight = -0.500` | CHECK 3819 `chk_container_weight` | ✓ 3819 |
| N6 | Mismo `container_code` dos veces en la empresa | UNIQUE 1062 `uq_container_company_code` | ✓ 1062 |
| N7 | Cadena válida completa del mismo tenant | Aceptada | ✓ fila creada |

Tras `ROLLBACK`: `COUNT(*)` de companies/clients/contracts/charges/containers de prueba = **0** (sin residuos).

## 4. Contratos generados (`docs/modules/`)

| Archivo | Contenido |
|---|---|
| `client-schema-contract.md` | Definición física, `client_state` booleano, tenant, mapeo JPA, reglas no-SQL (cliente inactivo, historial) |
| `contract-schema-contract.md` | FK compuesta a client, `DECIMAL(15,2)`, máquina de estados, cadena con cargas |
| `charge-schema-contract.md` | FK compuesta a contract, sin `client_id` duplicado, estados PENDING→…→DELIVERED |
| `container-schema-contract.md` | FK compuesta a charge, código único por empresa, `DECIMAL(12,3)`, ladder AVAILABLE→…→DELIVERED |

## 5. `generate-module` — archivos creados y modificados

**Creados (paquete `com.manager.truck.truck`, ruta `truck/src/main/java/...`):**

- `domain/`: `Client`, `Contract`, `Charge`, `Container` (entidades JPA escalares, sin `@ManyToOne`; fechas DB con `insertable=false, updatable=false`; montos/pesos `BigDecimal`)
- `domain/enums/`: `ContractState`, `ChargeState`, `ContainerState` (valores exactos SQL)
- `domain/dto/request/` (12): `ClientRequest/ClientUpdateRequest/ClientStatusRequest`, `ContractRequest/ContractUpdateRequest/ContractStatusRequest`, `ChargeRequest/ChargeUpdateRequest/ChargeStatusRequest`, `ContainerRequest/ContainerUpdateRequest/ContainerStatusRequest`
- `domain/dto/response/` (8): `{Client,Contract,Charge,Container}Response` + `{Clients,Contracts,Charges,Containers}Response`
- `repository/` (4): `ClientRepository`, `ContractRepository`, `ChargeRepository`, `ContainerRepository` (tenant-scoped; filtros por relación y estado; `exists...ContainerCode(Not)` para unicidad)
- `service/` (8): `ClientService(+Impl)`, `ContractService(+Impl)`, `ChargeService(+Impl)`, `ContainerService(+Impl)`
- `usecase/` (20): `client/`, `contract/`, `charge/`, `container/` — `Save*`, `GetAll*` (filtros), `Get*ById`, `Update*`, `Update*Status`
- `controller/` (4): `ClientController`, `ContractController`, `ChargeController`, `ContainerController`

**Modificados:**

- `domain/Constants.java`: + `ClientTable`, `ContractTable`, `ChargeTable`, `ContainerTable`
- `security/SecurityConfig.java`: + matchers GET/POST/PUT de `/api/companies/{companyId}/{clients|contracts|charges|containers}` → **solo `SUPER_ADMIN`/`SECRETARY`**

**Tests creados (16 clases, 125 pruebas):**

| Clase | Pruebas |
|---|---|
| `usecase.client.SaveClientUseCaseTest` | 5 |
| `usecase.client.GetAllClientsUseCaseTest` | 4 |
| `usecase.client.GetClientByIdUseCaseTest` | 3 |
| `usecase.client.UpdateClientStatusUseCaseTest` | 4 |
| `usecase.contract.SaveContractUseCaseTest` | 6 |
| `usecase.contract.UpdateContractStatusUseCaseTest` | 10 |
| `usecase.contract.GetAllContractsUseCaseTest` | 5 |
| `usecase.charge.SaveChargeUseCaseTest` | 7 |
| `usecase.charge.UpdateChargeStatusUseCaseTest` | 10 |
| `usecase.container.SaveContainerUseCaseTest` | 8 |
| `usecase.container.UpdateContainerUseCaseTest` | 4 |
| `usecase.container.UpdateContainerStatusUseCaseTest` | 8 |
| `controller.ClientSecurityTest` (WebMvc) | 12 |
| `controller.ContractSecurityTest` (WebMvc) | 13 |
| `controller.ChargeSecurityTest` (WebMvc) | 12 |
| `controller.ContainerSecurityTest` (WebMvc) | 14 |

## 6. Endpoints y autorización efectiva

**20 endpoints** (5 por módulo). El tenant sale **siempre de la URL**; nunca del body.

| Ruta base | Endpoints |
|---|---|
| `/api/companies/{companyId}/clients` | `GET /` (`?active=`), `GET /{clientId}`, `POST /`, `PUT /{clientId}`, `PUT /{clientId}/status` |
| `/api/companies/{companyId}/contracts` | `GET /` (`?clientId=`, `?state=`), `GET /{contractId}`, `POST /`, `PUT /{contractId}`, `PUT /{contractId}/status` |
| `/api/companies/{companyId}/charges` | `GET /` (`?contractId=`, `?state=`), `GET /{chargeId}`, `POST /`, `PUT /{chargeId}`, `PUT /{chargeId}/status` |
| `/api/companies/{companyId}/containers` | `GET /` (`?chargeId=`, `?state=`), `GET /{containerId}`, `POST /`, `PUT /{containerId}`, `PUT /{containerId}/status` |

| Método/Ruta | SUPER_ADMIN | SECRETARY | PARTNER | DRIVER |
|---|---|---|---|---|
| `GET` (listado/detalle, cualquier módulo) | ✓ | ✓ | ✗ **403 URL** | ✗ **403 URL** |
| `POST` (crear) | ✓ | ✓ | ✗ **403 URL** | ✗ **403 URL** |
| `PUT /{id}` (escalares) | ✓ | ✓ | ✗ **403 URL** | ✗ **403 URL** |
| `PUT /{id}/status` | ✓ | ✓ | ✗ **403 URL** | ✗ **403 URL** |

Capas de defensa: (1) `SecurityConfig` URL matchers; (2) `requireCompanyAdmin` en **todos** los use cases (lectura y escritura) — SUPER_ADMIN global o SECRETARY con membresía/rol verificados; (3) FKs compuestas SQL como defensa final. Contra IDOR: toda lectura/escritura es `findByCompanyIdAndXId`; el FK del body (`clientId`/`contractId`/`chargeId`) se resuelve **dentro del tenant** → 404 si es de otra empresa.

## 7. Reglas de negocio implementadas

| Regla | Estado |
|---|---|
| Cada cliente pertenece a una empresa gestora | ✅ `company_id` de la ruta + `fk_client_company`; sin `findAll()` global |
| Un cliente puede tener múltiples contratos | ✅ sin límite; listado `?clientId=` |
| Un contrato pertenece directamente a un cliente | ✅ `client_id` propio con FK compuesta `(company_id, client_id)` |
| Un contrato puede incluir múltiples cargas | ✅ listado `?contractId=`; cliente derivado vía contrato |
| Una carga puede contener múltiples contenedores | ✅ listado `?chargeId=` |
| No duplicar `client_id` en Charge | ✅ `Charge` no tiene `clientId`; se deriva del contrato |
| Todas las referencias respetan `company_id` | ✅ pre-check 404 por tenant + FKs compuestas (1452→409) |
| BigDecimal para montos y pesos | ✅ `contract_amount DECIMAL(15,2)` y `container_weight DECIMAL(12,3)` con `@Column(precision, scale)`; nunca `Double` |
| Estados y restricciones SQL respetados | ✅ enums `@Enumerated(STRING)` con valores exactos; CHECK/UNIQUE verificados con pruebas negativas |
| Conservar el historial comercial | ✅ sin DELETE; `PUT .../status` solo cambia el estado; transiciones estrictas (409 en retrocesos/saltos/terminales) |
| Montos y pesos no negativos | ✅ Bean Validation 400 (`@DecimalMin`, `@Digits`) + CHECK físico 3819 |
| Código de contenedor único por empresa | ✅ pre-check 409 + `uq_container_company_code` (1062) |
| Cliente/contrato/carga en estado válido para crear hijos | ✅ 409 según cadena aprobada |
| Empresa inactiva sin altas comerciales | ✅ 409 en los 4 `Save*UseCase` |
| Filtros por relaciones | ✅ `?clientId=`, `?contractId=`, `?chargeId=`, `?state=`, `?active=` |

## 8. Pruebas obligatorias — cobertura y resultados reales

### 8.1 Compilación y pruebas Maven (`truck/`)

- `.\mvnw.cmd clean test` → **BUILD SUCCESS — 320/320, 0 fallos, 0 errores, 42 clases** (195 de fases 1-4 + 125 nuevas).
- `TruckTableApplicationTests` (contexto real con BD viva y `ddl-auto=validate`) en verde: las 4 entidades nuevas **validan contra las tablas reales**.

Cobertura de los escenarios pedidos:

| Escenario pedido | Pruebas |
|---|---|
| Creación de cliente, contrato, carga y contenedor | `secretariaCreaClienteEnSuEmpresa`, `secretariaCreaContratoParaClienteActivoDeSuEmpresa`, `secretariaCreaCargaSobreContratoActivo`, `secretariaCreaContenedorSobreCargaNoCancelada` (+ 201 WebMvc por módulo) |
| Restricciones de claves foráneas | SQL N1-N3 (1452 ×3) + `clienteDeOtraEmpresaDeberiaDevolver404`, `contratoDeOtraEmpresaDeberiaDevolver404`, `cargaDeOtraEmpresaDeberiaDevolver404`, `contenedorDeOtraEmpresaDeberiaDevolver404` |
| Relacionar registros de empresas distintas | `secretariaDeEmpresaANoPuede*` en los 16 use cases de escritura/lectura + N1-N3 SQL |
| Validación de montos | `crearContratoConMontoNegativoDeberiaDevolver400`, `montoCeroDeberiaSerAceptado` (+ SQL N4 3819) |
| Validación de pesos | `crearContenedorConPesoNegativoDeberiaDevolver400` (+ SQL N5 3819) |
| Validación de estados | 24 pruebas de máquina de estados (`draftNoPuedeCompletarseDirectamente`, `deliveredEsEstadoTerminal`, `availableNoPuedeSaltarseACargarEnTransito`, …) + `cambiarEstadoAValorInvalidoDeberiaDevolver400` por módulo |
| Código de contenedor duplicado | `codigoDuplicadoEnLaEmpresaDeberiaDevolver409`, `cambioDeCodigoDuplicadoDeberiaDevolver409` (+ SQL N6 1062) |
| Consultas autorizadas | 200 SECRETARY/SUPER_ADMIN en los 4 `*SecurityTest` |
| Consultas denegadas | 401 sin auth, 403 PARTNER/DRIVER en GET/POST/PUT de los 4 módulos (26 pruebas WebMvc) |
| Reglas de cadena | `clienteInactivoDeberiaDevolver409`, `contratoDraftNoAdmiteCargas`, `contratoCancelledNoAdmiteCargas`, `contratoCompletadoNoAdmiteCargasNuevas`, `cargaCancelledNoAdmiteContenedores`, `cargaDeliveredSiAdmiteContenedores` |

### 8.2 Pruebas negativas SQL (7/7)

Ver §3 — ejecutadas contra `truck_sass` con ROLLBACK, sin residuos.

### 8.3 Smoke E2E con la aplicación real

- Arranque real (`.\mvnw.cmd spring-boot:run`) contra MySQL viva: contexto completo con las 4 entidades nuevas (`ddl-auto=validate` en verde).
- `GET /api/companies/1/{clients,contracts,charges,containers}` y `GET .../containers/1` sin autenticación → **401** en las 5 rutas; credenciales inválidas → **401**. Rutas registradas y entry point JSON activo.
- Escenarios funcionales completos (cadena cliente→contrato→carga→contenedor, cross-tenant, transiciones, filtros) cubiertos por los 125 tests unitarios/WebMvc y las 7 pruebas SQL.

## 9. Incompatibilidades detectadas y su resolución

| Problema | Causa | Solución |
|---|---|---|
| `PageImpl` recalcula `total` cuando la página de contenido no la llena (test de paginación: 12→11) | Spring Data Commons de Spring Boot 3.5 | Test ajustado para usar una página intermedia completa (`PageRequest.of(1,5)` con 5 elementos y total 12), misma lección que el `Page.filter` de la fase 4 |
| Advertencia de Mockito self-attaching en JDK 21 | Preexistente (fases anteriores); no bloquea | Sin cambio (fuera de alcance) |

## 10. Pendientes

- Fase 6: viajes (`journey_table` consumirá `(company_id, container_id)` y `(company_id, assignment_id)`) y auxiliares.
- Consultas comerciales para DRIVER/PARTNER cuando los viajes lo requieran (hoy 403 por decisión aprobada).
- Flyway/Liquibase (decidido mantener validación directa hasta decisión futura).
- JWT (hoy HTTP Basic stateless, decisión de fase 1).

## 11. Comandos ejecutados (resumen)

```
.\mvnw.cmd clean test                                  # baseline 195/195 y final 320/320 verde
mysql.exe -uroot -proot truck_sass                     # SHOW CREATE TABLE client/contract_table/charge_table/container_table (sin diferencias vs baseline)
Get-Content phase5_negative.sql | mysql.exe truck_sass --force   # 7/7 (1452×3, 3819×2, 1062, cadena válida) + ROLLBACK sin residuos
.\mvnw.cmd spring-boot:run                             # smoke: 401 en /clients, /contracts, /charges, /containers
```

*Sin commits (no solicitados). La BD viva no fue modificada (todas las pruebas SQL con ROLLBACK).*
