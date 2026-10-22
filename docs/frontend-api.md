# Guía de API para el frontend

Este documento resume los endpoints disponibles en el backend ManagerTruck, el acceso por rol y el estado de integración conocido. Se contrastó con los controllers y `SecurityConfig.java`.

## Estado de integración

El repositorio inspeccionado contiene el backend, pero no el código fuente del frontend; por eso no se puede confirmar cuáles endpoints ya consume la interfaz. Todos los endpoints se marcan **Por validar** en uso. Al contrastar este inventario con el frontend, actualizar cada elemento a **En uso** o **Disponible sin integrar**. No interpretar “disponible” como evidencia de consumo.

## Conexión y convenciones

- Base URL depende del entorno (local, pruebas o producción); el prefijo de rutas es `/api`.
- Seguridad actual: HTTP Basic, sin sesión (stateless). Enviar credenciales en cada petición HTTPS. El endpoint de registro `POST /api/users` es público.
- Respuestas de seguridad: `401 UNAUTHORIZED` si falta o es inválida la autenticación; `403 FORBIDDEN` si el usuario autenticado no tiene acceso.
- `{companyId}` es el tenant/empresa de las rutas multiempresa. Debe corresponder a una empresa a la que el usuario pertenezca cuando el caso de uso lo exige.
- Los endpoints paginados aceptan parámetros Spring `page`, `size` y `sort`; defaults frecuentes: `size=20`. Revisar el esquema de respuesta y campos en OpenAPI.
- Requests y respuestas pueden tener reglas de validación y transiciones de estado. Consultar Swagger antes de construir formularios.
- Documentación interactiva: `/swagger-ui/index.html`; OpenAPI JSON: `/v3/api-docs` (en el host del backend).

## Roles

| Rol | Uso principal |
|---|---|
| `SUPER_ADMIN` | Administración global de cuentas, empresas, roles de plataforma y operaciones administrativas en empresas. |
| `SECRETARY` | Administración operativa de la empresa y de sus miembros. |
| `PARTNER` | Consulta de recursos permitidos y asociados a sus camiones; consulta viajes de sus camiones. |
| `DRIVER` | Consulta de sus asignaciones y viajes, y actualización de estado de sus viajes asignados según las reglas de negocio. |

La autorización de ruta no siempre expresa todo el alcance de datos: casos de uso filtran resultados por empresa, socio o conductor. Un `200` en listado no implica acceso a todos los registros de esa empresa.

## Inventario de endpoints

En la columna **Acceso**, los roles se refieren al permiso de la ruta según `SecurityConfig`; cuando se indique “reglas del caso de uso”, la autorización adicional depende del propietario, membresía o contexto del recurso. Estado inicial de integración: **Por validar**.

### Cuentas, empresas y membresías

| Método | Endpoint | Función | Acceso | Uso frontend |
|---|---|---|---|---|
| POST | `/api/users` | Registrar cuenta (201) | Público | Por validar |
| GET | `/api/users/me` | Consultar cuenta autenticada | Autenticado | Por validar |
| GET | `/api/users/me/memberships` | Consultar empresas y roles propios | Autenticado | Por validar |
| GET | `/api/users/{userId}` | Consultar cuenta | Autenticado; propia o `SUPER_ADMIN` según caso de uso | Por validar |
| PUT | `/api/users/{userId}` | Actualizar datos personales | Autenticado; propia o `SUPER_ADMIN` según caso de uso | Por validar |
| PUT | `/api/users/{userId}/password` | Cambiar contraseña verificando la actual | Autenticado; propia según caso de uso | Por validar |
| GET | `/api/users` | Listado global paginado | `SUPER_ADMIN` | Por validar |
| PUT | `/api/users/{userId}/status` | Activar/desactivar cuenta | `SUPER_ADMIN` | Por validar |
| POST | `/api/companies` | Crear empresa | `SUPER_ADMIN` | Por validar |
| GET | `/api/companies` | Listar empresas paginadas | `SUPER_ADMIN` | Por validar |
| GET | `/api/companies/{companyId}` | Consultar empresa | Autenticado; `SUPER_ADMIN` o miembro activo según caso de uso | Por validar |
| PUT | `/api/companies/{companyId}` | Actualizar empresa | `SUPER_ADMIN` | Por validar |
| PUT | `/api/companies/{companyId}/status` | Activar/desactivar empresa | `SUPER_ADMIN` | Por validar |
| GET | `/api/companies/{companyId}/users` | Listar usuarios de empresa | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| GET | `/api/companies/{companyId}/memberships` | Listar membresías | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| POST | `/api/companies/{companyId}/memberships` | Crear membresía desde correo (201) | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| PUT | `/api/companies/{companyId}/memberships/{userId}/status` | Activar/desactivar membresía | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| GET | `/api/companies/{companyId}/memberships/{userId}/roles` | Consultar roles del miembro | Autenticado; `SUPER_ADMIN`, `SECRETARY` o propio usuario según caso de uso | Por validar |
| POST | `/api/companies/{companyId}/memberships/{userId}/roles` | Asignar roles de empresa | `SUPER_ADMIN`, `SECRETARY`; no acepta `SUPER_ADMIN` como rol de empresa | Por validar |

### Catálogos y recursos de empresa

Las rutas de esta sección tienen el prefijo `/api/companies/{companyId}`.

| Recurso | Método | Sufijo | Función | Acceso | Uso |
|---|---|---|---|---|---|
| Clientes | GET | `/clients` | Listar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Clientes | GET | `/clients/{clientId}` | Consultar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Clientes | POST | `/clients` | Crear (201) | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Clientes | PUT | `/clients/{clientId}` | Actualizar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Clientes | PUT | `/clients/{clientId}/status` | Cambiar estado | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Contratos | GET | `/contracts` | Listar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Contratos | GET | `/contracts/{contractId}` | Consultar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Contratos | POST | `/contracts` | Crear (201) | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Contratos | PUT | `/contracts/{contractId}` | Actualizar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Contratos | PUT | `/contracts/{contractId}/status` | Cambiar estado | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Cargos | GET | `/charges` | Listar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Cargos | GET | `/charges/{chargeId}` | Consultar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Cargos | POST | `/charges` | Crear (201) | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Cargos | PUT | `/charges/{chargeId}` | Actualizar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Cargos | PUT | `/charges/{chargeId}/status` | Cambiar estado | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Contenedores | GET | `/containers` | Listar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Contenedores | GET | `/containers/{containerId}` | Consultar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Contenedores | POST | `/containers` | Crear (201) | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Contenedores | PUT | `/containers/{containerId}` | Actualizar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Contenedores | PUT | `/containers/{containerId}/status` | Cambiar estado | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Proveedores auxiliares | GET | `/auxiliary-providers` | Listar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Proveedores auxiliares | GET | `/auxiliary-providers/{providerId}` | Consultar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Proveedores auxiliares | POST | `/auxiliary-providers` | Crear (201) | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Proveedores auxiliares | PUT | `/auxiliary-providers/{providerId}` | Actualizar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Proveedores auxiliares | PUT | `/auxiliary-providers/{providerId}/status` | Cambiar estado | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Socios | GET | `/partners` | Listar | Los cuatro roles; alcance de datos sujeto a reglas del caso de uso | Por validar |
| Socios | GET | `/partners/{partnerId}` | Consultar | Los cuatro roles; reglas del caso de uso | Por validar |
| Socios | POST | `/partners` | Crear (201); `PERSON` crea cuenta, membresía activa y rol `PARTNER`; `BUSINESS` no crea cuenta | `SUPER_ADMIN`, `SECRETARY` | En uso |
| Socios | PUT | `/partners/{partnerId}` | Actualizar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Socios | PUT | `/partners/{partnerId}/status` | Cambiar estado | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Conductores | GET | `/drivers` | Listar | Los cuatro roles; alcance de datos sujeto a reglas del caso de uso | Por validar |
| Conductores | GET | `/drivers/{driverId}` | Consultar | Los cuatro roles; reglas del caso de uso | Por validar |
| Conductores | POST | `/drivers` | Crear (201) junto con cuenta, membresía activa y rol `DRIVER` | `SUPER_ADMIN`, `SECRETARY` | En uso |
| Conductores | PUT | `/drivers/{driverId}` | Actualizar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Conductores | PUT | `/drivers/{driverId}/status` | Cambiar estado | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Camiones | GET | `/trucks` | Listar | `SUPER_ADMIN`, `SECRETARY`, `PARTNER`; alcance filtrado para socio | Por validar |
| Camiones | GET | `/trucks/{truckId}` | Consultar | `SUPER_ADMIN`, `SECRETARY`, `PARTNER`; reglas del caso de uso | Por validar |
| Camiones | POST | `/trucks` | Crear (201) | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Camiones | PUT | `/trucks/{truckId}` | Actualizar | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| Camiones | PUT | `/trucks/{truckId}/status` | Cambiar estado | `SUPER_ADMIN`, `SECRETARY` | Por validar |

### Asignaciones y viajes

| Método | Endpoint | Función / parámetros | Acceso | Uso frontend |
|---|---|---|---|---|
| GET | `/api/companies/{companyId}/assignments` | Listar; filtros opcionales `truckId`, `driverId`; paginado | Los cuatro roles; socio ve asignaciones de sus camiones, conductor las propias | Por validar |
| GET | `/api/companies/{companyId}/assignments/{assignmentId}` | Consultar; socio/conductor limitado a su alcance | Los cuatro roles, con alcance por caso de uso | Por validar |
| POST | `/api/companies/{companyId}/assignments` | Asignar conductor a camión (201) | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| PUT | `/api/companies/{companyId}/assignments/{assignmentId}/end` | Finalizar asignación conservando historial | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| GET | `/api/companies/{companyId}/journeys` | Listar; filtros opcionales `containerId`, `state`, `executionType`; paginado | Los cuatro roles; los resultados se limitan según rol | Por validar |
| GET | `/api/companies/{companyId}/journeys/{journeyId}` | Consultar viaje según alcance del rol | Los cuatro roles; reglas del caso de uso | Por validar |
| POST | `/api/companies/{companyId}/journeys` | Crear viaje en estado `PLANNED` (201) | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| PUT | `/api/companies/{companyId}/journeys/{journeyId}` | Actualizar datos escalares del viaje planificado | `SUPER_ADMIN`, `SECRETARY` | Por validar |
| PUT | `/api/companies/{companyId}/journeys/{journeyId}/status` | Transiciones de estado; `DRIVER` puede iniciar/completar viajes PARTNER asignados | `SUPER_ADMIN`, `SECRETARY`, `DRIVER` | Por validar |

### Administración de roles de plataforma

| Método | Endpoint | Función | Acceso | Uso |
|---|---|---|---|---|
| GET | `/api/platform/user-roles` | Consultar roles de plataforma | `SUPER_ADMIN` | Por validar |
| GET | `/api/roles` | Listar roles | Autenticado (regla global `anyRequest().authenticated()`) | Por validar |
| GET | `/api/roles/{roleId}` | Consultar rol | Autenticado | Por validar |
| POST | `/api/roles` | Crear rol | `SUPER_ADMIN` | Por validar |
| PUT | `/api/roles/{roleId}` | Actualizar rol | `SUPER_ADMIN` | Por validar |

## Flujos de uso sugeridos por rol

Son secuencias funcionales sugeridas, no requisitos obligatorios. Cada llamada sigue su control de permisos y validaciones.

- **Inicio de sesión / selección de empresa (todos):** autenticar con HTTP Basic → `GET /api/users/me` → `GET /api/users/me/memberships` → seleccionar `companyId` para el contexto de empresa. Conocer las membresías evita solicitar recursos de un tenant ajeno.
- **`SUPER_ADMIN`:** consultar cuentas/empresas → crear o actualizar empresa → crear membresía → asignar roles de empresa → administrar recursos. `SUPER_ADMIN` es rol de plataforma y no se asigna desde la ruta de roles de membresía.
- **`SECRETARY`:** obtener miembros y membresías de su empresa → gestionar socios, conductores, camiones y recursos operativos → crear asignaciones → crear/actualizar viajes → consultar viajes.
- **`PARTNER`:** consultar socios, camiones y asignaciones visibles → consultar viajes de sus camiones. Las escrituras de viajes, camiones y asignaciones no están habilitadas para este rol en la configuración actual.
- **`DRIVER`:** consultar sus asignaciones y viajes visibles → actualizar estado del viaje asignado mediante `/journeys/{journeyId}/status`. El caso de uso restringe las transiciones que puede ejecutar.

## Errores y comportamiento a considerar

- `401`: credenciales ausentes o no válidas.
- `403`: rol insuficiente o acceso fuera del alcance permitido.
- `400`: solicitud inválida, validación fallida, filtro/enum no reconocido o transición de estado no permitida (confirmar cuerpo de error en OpenAPI).
- `404`: recurso inexistente o no visible según las reglas del caso de uso.
- Para operaciones de creación, los controllers responden `201 Created`; otros códigos/cuerpos deben confirmarse en Swagger y en las excepciones del servicio.

## Actualización y verificación

Al cambiar un controller o `SecurityConfig.java`, actualizar esta guía. Para identificar **En uso**, buscar las llamadas HTTP en el repositorio frontend y anotar módulo/pantalla que usa cada ruta. Contrastar permisos con pruebas de seguridad y el comportamiento de los casos de uso, y revisar los DTOs en `/v3/api-docs` antes de implementar formularios o tipos del cliente.
