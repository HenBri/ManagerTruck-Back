---
name: generate-module
description: Implementa o modifica módulos de ManagerTruck-Back en Spring Boot/JPA siguiendo Controller → UseCase → Service → Repository → Entity, DTOs y OpenAPI. Úsala para CRUD, consultas filtradas, autenticación, asociaciones, roles, propietarios, conductores, viajes y auxiliares; verifica primero el contrato SQL de database-schema, el tenant y los permisos.
---

# Skill: generate-module — ManagerTruck-Back (SaaS)

## Propósito

Crear **un módulo o capacidad por solicitud** sin alterar inadvertidamente la arquitectura ni el contrato de datos. Adaptación de la Skill original: mantiene `Constants`, DTOs, repository, service + impl, use cases `execute(...)`, controller REST/OpenAPI, nombres en minúscula para paquetes y borrado lógico **cuando el esquema lo permita**. Corrige las prácticas inseguras del generador original para una aplicación multiempresa.

**Ruta de código esperada:** `truck/src/main/java/com/manager/truck/truck/`.
**Paquete raíz:** `com.manager.truck.truck`.
**Repositorio real > ejemplos de esta Skill**: inspeccionar los archivos del proyecto antes de escribir.

## Entradas y coordinación con `database-schema`

1. Identificar módulo, casos de uso, roles autorizados, tenant, campos, relaciones, endpoints y pruebas. Usar `examples/user.md` como **ejemplo**, no como contrato universal.
2. Encontrar el contrato SQL/migración vigente y `docs/modules/<modulo>-schema-contract.md` generado por **database-schema**. Si no existe, activar primero la Skill `database-schema` para revisar el esquema **sin inventar tablas**; generar el contrato antes de implementar entidades.
3. Si los archivos SQL, DBML, código existente y contrato difieren, **detener los cambios conflictivos** y explicar la discrepancia. Precedencia: esquema migrado del repositorio y decisiones confirmadas del usuario > contrato derivado > SQL/DBML de referencia de la otra Skill > ejemplo de la Skill.
4. Determinar si la petición implica solo backend, solo DDL o ambos. **No crear migraciones SQL aquí por duplicado**: corresponden a `database-schema`.
5. Si faltan detalles esenciales que alteran cardinalidades, rol, tenant o seguridad, formular preguntas puntuales antes de generar.

## Reglas de negocio no negociables

- **Cuatro roles:** `SUPER_ADMIN` (global, `platform_user_role`), `SECRETARY`, `PARTNER`, `DRIVER` (por empresa, `company_user_role`). La tabla `company_user` define membresía; `role_table` define nombres y `role_scope`.
- **Un usuario** (`user_table`) puede pertenecer a múltiples empresas; el mismo usuario puede ser `PARTNER` y `DRIVER` en una misma empresa. Una cuenta NO equivale automáticamente a un socio o conductor.
- **Una empresa no es propietaria de camiones.** Camiones habituales (`truck_table`) tienen `owner_partner_id` obligatorio a un socio de la misma empresa; `company_id` representa al tenant.
- **Conductor != propietario.** `driver_table` representa al conductor; `truck_driver_assignment` asigna conductor a camión con vigencia. `driver_table.person_partner_id` es opcional y significa que conductor y socio propietario son **la misma persona**.
- **Auxilio externo:** `auxiliary_provider` es proveedor ocasional, no se convierte artificialmente en `partner_table`. Un `journey_table` de tipo `AUXILIARY` registra proveedor y datos del vehículo/conductor externos.
- **Un viaje = un recorrido completo A → B**, un solo camión/conductor y `execution_type = PARTNER | AUXILIARY`; **no crear etapas, `journey_leg` ni cambios de conductor en el recorrido**. La integridad depende de `chk_journey_execution`.
- Jerarquía comercial: `company → client → contract_table → charge_table → container_table → journey_table` (relaciones de tenant en el SQL). No restablecer la relación duplicada `charge → client` sin decisión explícita.

## Arquitectura canónica

```text
controller/{X}Controller          @RestController; solo recibe/delega y devuelve DTO
  ↓
usecase/{x}/{Accion}{X}UseCase     @Service; execute(...); orquesta y aplica reglas/autorización
  ↓
service/{X}Service + Impl          @Service; acceso/operaciones de dominio, transacciones coherentes
  ↓
repository/{X}Repository          Spring Data JpaRepository<X, ID real>
  ↓
domain/{X}                       @Entity JPA alineada 1:1 con SQL vigente
```

- Conservar nombres y convenciones existentes cuando no comprometan seguridad. Paquetes `usecase/{x}` **minúsculas**, clase `{X}` singular PascalCase, tabla y columna **tal cual SQL**. Preferir inyección por constructor para clases nuevas si el repositorio lo admite; si el proyecto exige `@Autowired` de campo, respetarlo en módulos heredados y no reescribir solo por estilo.
- Controller no accede a repository ni service; use case no accede directamente a repository; service encapsula repositorios. No duplicar reglas de autorización en cinco capas: centralizar componentes reutilizables y verificar en límites de aplicación.
- Los use cases pueden ser transaccionales (`@Transactional`) cuando ejecutan operaciones múltiples. Consultas pueden ser `readOnly=true`. No prohibir `@Transactional`.
- Validar input con `@Valid` y Jakarta Bean Validation según dependencias presentes. Usar respuestas HTTP y errores adecuados (`400`, `401`, `403`, `404`, `409`); si hay `@ControllerAdvice`, reutilizarlo; si falta, proponer/agregar uno mínimo cuando lo requieran los casos de uso.
- No usar `JpaRepository.getById()` como validación de existencia: puede devolver referencia diferida. Para consultas autorizadas utilizar `findById...().orElseThrow(...)` o repositorio equivalente.
- **No serializar entidades JPA** en respuestas HTTP. DTOs explícitos evitan filtrar `password_hash`, recursión y consultas LAZY involuntarias. Si existe `CommonResponse`, conservar su envoltorio y sustituir la carga por DTOs seguros.
- **Soft delete solo si la tabla lo permite** (`*_state`, `*_status`, etc.); nunca inventar una columna `state` ni forzar `Boolean` donde SQL usa `ENUM`. No realizar borrado físico sin especificación explícita y control de dependencias.

## Seguridad, permisos y aislamiento multiempresa

Consultar `references/security-and-tests.md` antes de implementar rutas o casos de uso de lectura, escritura, roles, contraseña y pertenencia.

1. Obtener identidad y `company_id` **efectivo** del contexto autenticado/autorizado. Un `companyId` en la URL es solo una solicitud a validar, no prueba de pertenencia; jamás confiar en el tenant del body. `SUPER_ADMIN` debe seleccionar explícitamente tenant para operar datos empresariales y sus acciones deben auditarse.
2. Repositorios de entidades empresariales deben filtrar por `company_id` en **cada lectura y escritura**; emplear consultas `findByIdAndCompanyId(...)`, y paginación `findByCompanyId(...)` o equivalentes. Nunca publicar `findAll()` global en un endpoint empresarial.
3. Verificar membresía `company_user.membership_status = ACTIVE`, cuenta activa, roles de `COMPANY` o `PLATFORM` con sus alcances. `SECRETARY` gestiona su empresa, `PARTNER` solamente sus camiones/operaciones habilitadas y `DRIVER` sus viajes; filtrar por identidad de perfil, no solo por rol.
4. Asignaciones de FKs se resuelven **dentro del mismo tenant** (`company_id + id`) antes de persistir; claves foráneas compuestas SQL son defensa adicional, no sustituyen autorización.
5. `SUPER_ADMIN` nunca se concede por CRUD común, body libre, autorregistro o cliente frontend. Requiere aprovisionamiento seguro; no insertar credenciales por defecto.
6. Hash de contraseña con `PasswordEncoder` de Spring Security (p. ej. BCrypt/Argon2 si está disponible); jamás almacenar ni retornar contraseñas en claro. Para cambio de password usar caso de uso específico.
7. No generar endpoints de administración protegidos sin la infraestructura de seguridad que los haga efectivos. Si todavía no existe Spring Security, reportar el bloqueante o incluir la implementación de seguridad solo con aprobación de alcance.

## Modelo JPA: mapeo preciso (sin inferir el tipo por nombre)

| MySQL | Java/JPA sugerido | Restricción |
|---|---|---|
| `BIGINT [UNSIGNED]` | `Long` si rango de aplicación ≤ `Long.MAX_VALUE` | Usar ID real de entidad; confirmar tratamiento de UNSIGNED |
| `SMALLINT UNSIGNED` | `Integer` o tipo probado con el driver | `role_id` **no** asumir Long |
| `DECIMAL(p,s)` | `BigDecimal` + `@Column(precision=p, scale=s)` | Nunca `Double` para montos |
| `DATE` | `LocalDate` | No inventar hora |
| `DATETIME` | `LocalDateTime` | Establecer convención de zona horaria de la aplicación |
| `ENUM` SQL | `enum` Java + `@Enumerated(EnumType.STRING)` | Exacta lista SQL de valores |
| `VARCHAR(n)` | `String` + `@Column(length=n)` | No truncar emails ni longitudes |
| `TINYINT(1)` | `Boolean`, solo si su semántica es binaria | No transformar otros estados |

- Tablas de PK simple: `@Id` y `@GeneratedValue(strategy=IDENTITY)` **solo** si SQL define `AUTO_INCREMENT`.
- Tablas de PK compuesta (`company_user`, `company_user_role`, `platform_user_role`): `@Embeddable` + `@EmbeddedId` (o `@IdClass` si ya existe), y `@MapsId` cuando corresponda. Repository con tipo de ID compuesto, **no** `Long` genérico.
- FKs compuestas: `@JoinColumns` + columnas del tenant, con política de propiedad/inserción coherente para evitar *repeated column*. Usar `insertable=false, updatable=false` en las asociaciones que comparten `company_id` si el diseño del mapping lo requiere; no sacrificar la FK compuesta por comodidad.
- `Constants.java`: añadir solo las constantes necesarias con nombres reales de SQL; no convertir `password_hash` en `user_password` ni `created_at` en `*_createDate`.
- Consultas paginadas cuando el volumen lo requiere; prevenir N+1 con proyecciones, `@EntityGraph` o fetch adecuado caso a caso.

## Formato esperado del módulo

Crear **solo los archivos necesarios**, siguiendo la estructura de la Skill original:

1. `domain/Constants.java` (modificación de constantes si procede).
2. `domain/{X}.java` y clases de ID/enum si aplica.
3. `domain/dto/request/{X}Request.java` y DTOs de operaciones específicas.
4. `domain/dto/response/{X}Response.java` y DTO de colección `...sResponse` si se sigue el patrón existente; payload **sin entidades JPA**.
5. `repository/{X}Repository.java`.
6. `service/{X}Service.java`, `service/{X}ServiceImpl.java`.
7. `usecase/{x}/GetAll{X}UseCase`, `Get{X}ByIdUseCase`, `Save{X}UseCase`, `Update{X}UseCase`, `Delete{X}UseCase`: crear únicamente los que tengan sentido y autorización definida. Otros como `GetActive{X}UseCase` o filtros por relación bajo demanda.
8. `controller/{X}Controller.java` con `@RestController`, `@RequestMapping`, Swagger `@Operation`, `@Tag`, `@Parameter`.
9. Tests unitarios, integración y de control de acceso donde la funcionalidad los requiera.

### Endpoints y IDs relacionados

- Mantener el estilo legado con IDs de relaciones como segmentos **etiquetados** en POST, cuando sea compatible: `/api/charges/{contractId}`; no construir `/api/charges/{clientId}/{contractId}` porque el SQL actual ya deriva cliente de contrato.
- Excepción obligatoria: **el tenant no se acepta libremente** como FK en POST. Usar `company_id` del contexto seguro o ruta protegida `/api/companies/{companyId}/...` validando membresía/globalidad.
- Excepciones justificadas: asociaciones con PK compuesta, asignación de roles, cambio de contraseña, endpoints de acciones de viaje (iniciar/finalizar) deben diseñarse como operaciones con semántica propia; no forzar CRUD genérico.
- `PUT /{id}` actualiza escalares sin cambiar FKs, como norma original; cambios de relaciones de negocio deben tener caso de uso y autorización explícitos.
- Ejemplo de rutas de usuario: `GET /api/users/me`; `GET /api/companies/{companyId}/users` (administración autorizada); `POST /api/companies/{companyId}/memberships` (asignación de pertenencia aparte del registro de cuenta). No exponer `GET /api/users` para todos los tenants.

## Flujo de ejecución

1. **Inspeccionar** código actual, estructura de paquetes, `pom.xml`, `application.yml/properties`, estrategia de migraciones y Spring Security.
2. **Leer** el contrato generado por `database-schema` y las migraciones vigentes; trazar tabla, PK, FKs, índices/UNIQUE/CHECK, enums y nulabilidad.
3. **Planificar** archivos a crear/modificar, endpoints, permisos, dependencias, riesgos de migración y tests.
4. **Implementar** por capas respetando los contratos, sin reescribir módulos no pedidos.
5. **Comprobar** permisos y casos límite (ver matriz en `references/security-and-tests.md`).
6. **Verificar** con `./mvnw -q test` en `truck/` si el wrapper/dependencias están disponibles. Si falla, reportar motivo, y hacer como mínimo `./mvnw -q compile`. En PowerShell: `./mvnw.cmd ...`. Nunca declarar compilación o pruebas exitosas sin ejecución real.
7. **Informar** archivos creados/modificados, endpoints, decisiones de diseño, dependencias, resultado real de compilación/pruebas y pendientes.

## Referencias en esta Skill

- `references/security-and-tests.md`: seguridad RBAC multiempresa y tests mínimos.
- `references/legacy-conventions.md`: patrones de la Skill original que se preservan y los que deben sustituirse.
- `examples/user.md`: ejemplo de solicitud para implementar `User` de manera incremental.
