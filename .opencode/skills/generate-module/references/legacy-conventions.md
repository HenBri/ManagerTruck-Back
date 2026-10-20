# Convenciones heredadas y compatibilidad

La Skill original `generate-module` incluía esta estructura: `Constants`, entidad JPA, Request, respuestas singular/plural, JpaRepository, Service + Impl, cinco use cases (`GetAll`, `GetById`, `Save`, `Update`, `Delete`) y Controller REST/OpenAPI. Conservar esta organización cuando tenga sentido.

## Convenciones que sí conservar

- Base: `truck/src/main/java/com/manager/truck/truck/`.
- Nombres de paquetes `domain`, `domain/dto/request`, `domain/dto/response`, `repository`, `service`, `usecase/{x}` en minúscula, `controller`.
- `@RequestMapping("/api/{xs}")`; no usar `@RestController("api/x")` como ruta.
- El controller delega a usecases; usecases a services; services a repositories.
- Clases de caso de uso `{Verbo}{X}UseCase` con `execute(...)`.
- Reutilizar `CommonResponse`/`StatusCodeResponse` si existen, pero devolver payload con DTO seguro.
- Respetar `Constants` para nombres físicos, cambiando supuestos de naming antiguo por los nombres SQL reales.
- POST legado con IDs relacionados en path, **solo** donde no exponga o permita elegir un tenant arbitrario.
- Soft delete cuando la tabla dispone de campo/estado y el dominio permite desactivación.
- Usar Swagger/OpenAPI como en el módulo existente.

## Reglas heredadas que se corrigen (NO copiar)

| Skill original | Política nueva |
|---|---|
| `JpaRepository<X, Long>` siempre | `JpaRepository<X, ID>` según PK SQL, incluida PK compuesta |
| `Boolean state` + `Date date` obligatorios | Solo si el esquema realmente tiene esos campos; enums y fecha del tipo correcto |
| `Double` para montos y `Integer` para teléfonos | `BigDecimal` para dinero, `String` para teléfonos |
| Devolver JPA directamente | DTOs mapeados para evitar hashes, recursión y filtraciones |
| Prohibir `@Valid`, `@Transactional` y errores centrales | Usar validación, transacciones y manejo de errores coherente |
| `getById()` sin validación | Búsqueda opcional con restricción por tenant y manejo `404` |
| `findAll()` indiscriminado | Consultas filtradas por tenant, rol y perfil con paginación |
| Relaciones por IDs en URL siempre | Solo cuando seguro y semánticamente útil; no aceptar tenant del cliente |
| PUT nunca actualiza relaciones | Mantener por defecto; cambios de relación bajo caso de uso autorizado |
| `@Autowired` campo obligatorio | Respetar herencia si necesario, permitir inyección por constructor en nuevas clases |
| No comprobación de permisos | Autorización explícita por plataforma, tenant, recurso y acción |

## Nombres reales del esquema SaaS base

`company`, `user_table`, `role_table`, `platform_user_role`, `company_user`, `company_user_role`, `client`, `contract_table`, `charge_table`, `container_table`, `partner_table`, `driver_table`, `truck_table`, `truck_driver_assignment`, `auxiliary_provider`, `journey_table`.

No usar las relaciones antiguas `Journey → User`, `Truck → User`, `User → Company` como si representasen asignación de conductor, propiedad o tenant. Los modelos actuales separan dichos conceptos.
