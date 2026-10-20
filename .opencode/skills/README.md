# Skills para ManagerTruck-Back

Este paquete contiene dos Skills complementarias:

- **`database-schema/`**: verifica/diseña tablas y migraciones en MySQL 8, relaciones multiempresa, integridad, tipos y el contrato de esquema por módulo.
- **`generate-module/`**: crea la implementación Java/Spring Boot (Controller → UseCase → Service → Repository → Entity) alineada con dicho contrato.

## Orden de uso

1. Copiar ambas carpetas completas al directorio de Skills de tu agente/editor (según la herramienta). Mantener las carpetas `references/` y `examples/` junto con cada `SKILL.md`.
2. En el repositorio de ManagerTruck, confirmar **qué migraciones y esquema SQL están en uso**. El archivo `database-schema/references/schema_base_transporte_saas.sql` es un **punto de partida**, no una migración que se ejecuta automáticamente.
3. Solicitar: «Usa `database-schema` para revisar el módulo User y generar su contrato SQL en `docs/modules/user-schema-contract.md`. Si necesita migración, créala con la herramienta que utiliza el proyecto».
4. Después: «Usa `generate-module` para implementar User a partir del contrato aprobado, con permisos, DTOs seguros, tests y compilación».

## Módulos sugeridos (dependencias)

`company` → `user` / `role` → `company_user` → `company_user_role` / `platform_user_role`; en paralelo `client` → `contract` → `charge` → `container`; además `partner` → `truck`, `driver` → `truck_driver_assignment`; `auxiliary_provider` y finalmente `journey`.

## Importante

- Las Skills **no sustituyen migraciones, Spring Security ni un servidor MySQL**. Indican al agente cómo trabajar con la tecnología que el repositorio tenga configurada.
- El DBML incluido es un diagrama resumido; no trae todas las columnas ni restricciones de la versión SQL.
- En MySQL 8, los `CHECK` exigen MySQL 8.0.16+ para aplicación efectiva.
- No asumir que los scripts se ejecutaron ni que Maven pasó pruebas si no se corrieron contra el proyecto.
- Regla confirmada: empresa gestora no posee camiones; pertenecen a socios. Auxilio externo opcional. Cada viaje recorre A a B sin tramos.
