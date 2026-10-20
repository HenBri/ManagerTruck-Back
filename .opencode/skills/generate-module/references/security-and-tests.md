# Seguridad y pruebas de módulos ManagerTruck SaaS

## Checklist obligatorio de autorización

- Confirmar autenticación real y `user_table.user_status = ACTIVE`.
- Para recursos de empresa: contexto de empresa autorizado, membresía `company_user` activa y rol de scope `COMPANY`; `SUPER_ADMIN` global de scope `PLATFORM` requiere selección explícita del tenant.
- `SECRETARY`: CRUD operativo de su tenant, sin permisos globales ni operación fuera del tenant.
- `PARTNER`: aplicar filtros por `partner_table.user_id` de la cuenta actual y `truck_table.owner_partner_id`; las consultas de viajes requieren la relación con el vehículo del socio (por `assignment_id → truck_id`) y tenant.
- `DRIVER`: aplicar filtros por `driver_table.user_id` y `journey_table.assignment_id → truck_driver_assignment.driver_id`; solo viajes asignados, sin exponer datos de otros choferes.
- Auxilios (`AUXILIARY`): proveedor y snapshot externo no obtienen acceso a la plataforma por existir en BD.
- Contra IDOR: leer por id+tenant, y luego revisar rol/pertenencia al recurso. La ruta y el body no conceden acceso.
- Rechazar habilitar `SUPER_ADMIN` desde CRUD público o secretaria; usar aprovisionamiento seguro y auditoría.
- Password: almacenar hash con encoder fuerte, nunca incluir `password_hash` en DTO, logs ni trazas.
- FKs entre tenants: al escribir, verificar asociación por `(company_id, fk_id)`; permitir que el motor fuerce integridad con FK compuestas.

## Matriz mínima de pruebas

1. Secretaria de empresa A crea/lee/edita registros de A → autorizado.
2. Secretaria A solicita ID válido de B → `403` o `404`, sin datos de B.
3. Usuario de A sin membresía en B envía `companyId=B` → rechazado.
4. Socio A visualiza su camión → permitido; de otro socio del mismo tenant → rechazado salvo autorización especial.
5. Chofer A ve viaje asignado → permitido; otro viaje de A → rechazado.
6. Usuario socio+chofer: un mismo `user_id` obtiene acciones autorizadas para ambos perfiles, sin duplicar cuenta.
7. Un usuario con cuenta en empresas A y B mantiene permisos separados; rol de A no se traslada a B.
8. Intento de insertar `truck.company_id=A` con `owner_partner_id` de B → error de negocio y rechazo por FK compuesta.
9. `journey.execution_type=PARTNER` con `auxiliary_provider_id` → rechazado por CHECK.
10. `journey.execution_type=AUXILIARY` sin nombre/placa de conductor y camión → rechazado por CHECK.
11. `SUPER_ADMIN` puede administrar tenant explícitamente seleccionado; intentos de asignar ese rol por endpoints empresariales → rechazados.
12. Actualización de usuario no expone ni altera hash salvo caso específico de cambio de contraseña.
13. Desactivación según `user_status`/`membership_status` impide acceso subsiguiente sin borrar auditoría.
14. Rechazo de dos viajes incompatibles concurrentes para el mismo camión/conductor (requiere validación transaccional/locking, no basta con CHECK).

## Excepciones y transacciones

- `400` datos inválidos; `401` no autenticado; `403` rol/scope/tenant prohibido; `404` entidad no accesible o inexistente; `409` constraint unique/estado conflictivo; respuesta estructurada consistente.
- En múltiples escrituras atómicas (crear user+membership+roles, reservar camión/conductor, cambiar estado de viaje) usar `@Transactional` y probar rollback ante fallo.
- Test de integración SQL real recomendado con MySQL 8/Testcontainers (CHECK y FKs compuestas), además de unit tests de usecases.
