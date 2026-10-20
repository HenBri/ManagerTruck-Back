# Reglas del dominio y mapa de dependencias (baseline)

Base de referencia: `schema_base_transporte_saas.sql`. El SQL actual del repositorio/migraciones tiene prioridad. DBML adyacente es solo diagrama resumido.

## Catálogo de tablas (16)

| Tabla | Clave primaria | ¿Tenant? | Dependencias relevantes |
|---|---|---|---|
| `company` | `company_id` | Propietaria del tenant | — |
| `user_table` | `user_id` | Global | — |
| `role_table` | `role_id` | Global + scope | — |
| `platform_user_role` | `(user_id,role_id)` | Global | usuario y rol con scope PLATFORM |
| `company_user` | `(company_id,user_id)` | Relación | company, user_table |
| `company_user_role` | `(company_id,user_id,role_id)` | Sí | company_user y role_table scope COMPANY |
| `client` | `client_id` | Sí | company |
| `contract_table` | `contract_id` | Sí | `(company_id,client_id)` → client |
| `charge_table` | `charge_id` | Sí | `(company_id,contract_id)` → contract_table |
| `container_table` | `container_id` | Sí | `(company_id,charge_id)` → charge_table |
| `partner_table` | `partner_id` | Sí | company, membresía opcional `(company_id,user_id)` |
| `driver_table` | `driver_id` | Sí | company, membresía opcional, partner_person opcional |
| `truck_table` | `truck_id` | Sí | `(company_id,owner_partner_id)` → partner_table |
| `truck_driver_assignment` | `assignment_id` | Sí | `(company_id,truck_id)` y `(company_id,driver_id)` |
| `auxiliary_provider` | `provider_id` | Sí | company |
| `journey_table` | `journey_id` | Sí | container + assignment opcional o provider opcional, según modalidad |

## Invariantes y escenarios

- `SUPER_ADMIN`: rol con scope `PLATFORM`, solo en `platform_user_role`; puede operar varias empresas por selección explícita y auditoría.
- `SECRETARY`, `PARTNER`, `DRIVER`: roles de scope `COMPANY`, requieren membresía activa; los permisos no se deducen solo del nombre del rol, deben verificarse por recurso.
- Socio = propietario de uno o más camiones de la empresa administrada; un conductor puede representar a la misma persona que un socio mediante `driver_table.person_partner_id`; este enlace no significa empleador del chofer.
- `truck_table` **siempre** refiere a `owner_partner_id` no nulo del mismo tenant; nunca propiedad de `company`.
- `journey_table` tiene `execution_type = PARTNER` con `assignment_id` y sin proveedor externo, **o** `AUXILIARY` con `auxiliary_provider_id`, placa y nombre de conductor externos, y `assignment_id` nulo.
- Proveedor de auxilio (empresa o independiente) no es socio habitual ni adquiere automáticamente cuenta/rol.
- Un viaje A→B no se divide en tramos; no se modelan relevos a mitad de camino.
- En el baseline cada viaje transporta un `container_table`; si se requiere carga sin contenedor, abrir decisión funcional antes de cambiar FK.
- `company_id` en todas las tablas operativas aísla datos de tenants. **No implica** propiedad legal de camiones ni ser el conductor.

## Restricciones que requieren aplicación (no solo SQL)

1. `driver_table.person_partner_id` debe vincular solo a socios PERSON de mismo tenant; MySQL CHECK no puede inspeccionar otra tabla.
2. No superponer viajes incompatibles de un mismo conductor/camión; transacción/locking y fecha/estado.
3. Un assignment debe estar vigente al iniciar un viaje y corresponder al mismo tenant.
4. Usuario vinculado a socio/conductor debe tener membresía activa y roles apropiados si accede como tal; la FK solo garantiza membresía, no rol.
5. En una API, autorización `company_id` + rol + acceso al objeto. FKs compuestas solo impiden vínculos incorrectos, no divulgación por `SELECT`.
6. Estados y cronología de viaje: inicio y fin coherentes, no cerrar si hay validaciones de llegada pendientes.
7. No garantizar la existencia de al menos un conductor activo solo por una FK de asignación; comprobar antes de programar el viaje.

## Nombres y tipos particulares

- `role_table.role_id = SMALLINT UNSIGNED` (Java no forzar `Long` por plantilla).
- `user_table.password_hash` 255, `user_email` 255 y `user_status ENUM(ACTIVE,INACTIVE)`.
- `company_user` y los role pivots llevan PK compuestas; se requiere identidad JPA compuesta.
- `contract_table.contract_amount DECIMAL(15,2)`, `container_table.container_weight DECIMAL(12,3)`.
- `client.client_state TINYINT(1)`, otros estados suelen ser `ENUM`, no un solo Boolean universal.
- `journey_table` agrega `agreed_amount DECIMAL(15,2)` solo para auxilio en el SQL baseline, `journey_state`, `execution_type`, reglas CHECK.
