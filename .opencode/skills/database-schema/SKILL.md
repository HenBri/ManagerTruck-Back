---
name: database-schema
description: Diseña, valida y evoluciona el esquema MySQL 8 de ManagerTruck SaaS módulo por módulo: entidades, PK/FK compuestas, relaciones, estados, tenant company_id, índices, restricciones, scripts de migración y contrato JPA. Úsala antes de generate-module cuando se cree o modifique una tabla, relación, migración o módulo dependiente de base de datos.
---

# Skill: database-schema — ManagerTruck SaaS (MySQL 8)

## Objetivo y frontera con `generate-module`

Esta Skill **diseña/verifica/evoluciona la base de datos** y emite un contrato consumible por `generate-module`. No genera controllers, repositories ni lógica CRUD Java; eso corresponde a `generate-module`.

**Trabajo por módulo**: cada iteración cambia solamente las tablas/índices/constraints que el caso de uso requiere, incluidas dependencias imprescindibles confirmadas. Nunca rediseñar todo el SaaS por defecto.

El baseline de esta Skill se encuentra en `references/schema_base_transporte_saas.sql` y su vista simplificada en `references/schema_base_transporte_saas.dbml`. **Son referencias de diseño, no una orden de ejecutar `CREATE DATABASE` ni de reconstruir la instalación existente.** El SQL es más completo que el DBML y prevalece sobre él para campos, CHECK, ENUM, índices y nulabilidad.

## Fuente de verdad y detección de diferencias

1. **Máxima prioridad:** migraciones versionadas y esquema efectivamente desplegado en el repositorio del usuario, más decisiones explícitas del usuario.
2. **Segunda:** especificaciones aprobadas por módulo, SQL/DML de estado existente y convenciones aplicables del repo.
3. **Tercera:** SQL baseline adjunto en `references/`; el DBML solamente ilustra relaciones.
4. Nunca aplicar automáticamente un archivo baseline de creación de cero sobre BD con datos. Si difieren fuentes, informar la diferencia con columna/constraint precisa y **solicitar resolución antes de ejecutar cambios destructivos o ambiguos**.
5. Inspeccionar `pom.xml`, `application.yml/properties`, carpetas de migraciones (Flyway `db/migration`, Liquibase `db/changelog`, u otra) y entidades existentes; elegir la herramienta ya instalada. Si no hay migraciones, proponer Flyway/Liquibase y pedir acuerdo antes de introducirla. No dar por hecho `hibernate.hbm2ddl.auto=update`; no usarlo para cambios estructurales productivos.

## Reglas permanentes del dominio

Leer `references/domain-and-invariants.md` antes de modificar un módulo del flujo logístico, permisos o usuarios.

- Modelo multiempresa de tablas compartidas: `company_id` representa el **tenant** de datos operativos; **no** titularidad de camiones. `user_table` es cuenta global y no tiene `company_id`.
- Roles: `SUPER_ADMIN` ámbito `PLATFORM` por `platform_user_role`. `SECRETARY`, `PARTNER`, `DRIVER` ámbito `COMPANY` por `company_user`, `company_user_role`. Mismo usuario puede tener ambos roles PARTNER y DRIVER.
- Empresa gestora **no es propietaria de camiones**: `truck_table.owner_partner_id` es obligatorio hacia `partner_table` del mismo tenant.
- Conductor (`driver_table`) separado de propietario; `truck_driver_assignment` asocia a ambos con vigencias. `driver_table.person_partner_id` opcional solo si el conductor es **la misma persona física** que un socio tipo `PERSON`.
- Auxilios: `auxiliary_provider` registra tercero ocasional; el viaje de ejecución `AUXILIARY` conserva foto histórica de placa y conductor externos. No convertir auxilios en socios automáticamente.
- Viaje `journey_table`: **recorrido único A → B sin tramos ni relevos**. `execution_type` distingue `PARTNER` (assignment) de `AUXILIARY` (proveedor y datos externos), con exclusividad CHECK.
- Jerarquía: contrato refiere directamente al cliente; cargas al contrato, contenedores a la carga, viajes al contenedor según baseline actual.
- No permitir relaciones cruzadas entre tenants; `company_id` + FK operativa forman una FK compuesta cuando intervienen dos tablas multiempresa.

## Información a recopilar por solicitud

Antes de generar DDL:

1. **Módulo/entidad** (nombre singular) y objetivo del negocio.
2. **Tabla existente vs. nueva**, y si ya hay filas que migrar.
3. Campos: nombre físico, tipo/longitud/precisión, `NOT NULL`, `DEFAULT`, unicidad, enums, datos sensibles.
4. Relaciones: tabla referida, cardinalidad (1:N/N:M/1:1), obligatoriedad, semántica, actualización/eliminación e invariante tenant.
5. Reglas invariantes: estados/transiciones, propiedad, acceso por usuario, CHECK, no superposición temporal, etc.
6. Consultas previstas, filtros por empresa y patrones de acceso que justifiquen índices.
7. Estado de migraciones, DBMS + versión (MySQL **8.0.16+** si se utilizan CHECK), entorno destino y plan rollback.

Cuando falten datos, consultar antes de inventar relaciones, modificar cardinalidades o aplicar cambios destructivos. Para campos obvios **no sensibles**, sugerir valores y etiquetar claramente como propuesta; no suponer reglas de negocio.

## Diseño relacional obligatorio

- Nombres de tablas y columnas **exactamente según esquema aprobado**, snake_case; mantener `Constants` y mapeos JPA compatibles.
- ID simples existentes: respetar `BIGINT UNSIGNED AUTO_INCREMENT`; compuestas: mantener **todos** sus componentes (p.ej. `company_user (company_id,user_id)` y `company_user_role (company_id,user_id,role_id)`). No inventar surrogate ID salvo aprobación.
- Integridad cross-tenant: para `truck_table.owner_partner_id` utilizar FK `(company_id,owner_partner_id) → partner_table(company_id,partner_id)`; para `contract_table.client_id`, FK `(company_id,client_id) → client(company_id,client_id)`. Asegurar índice único o clave referenciada legal en tabla destino; conservar índices de apoyo.
- Reglas locales con `CHECK` cuando puedan expresarse sin subconsultas; MySQL no permite CHECK para verificar propiedad o tipo de fila de otra tabla. Validaciones cross-row/cross-table, disponibilidad y permisos deben implementarse con lógica transaccional de backend y, cuando aplique, locking/constrains adecuados.
- Datos monetarios `DECIMAL`, telefónicos `VARCHAR`, fechas `DATE` vs `DATETIME` según semántica. Evitar `DOUBLE` para montos. En Java `BigDecimal`, `LocalDate`/`LocalDateTime`, `@Enumerated(EnumType.STRING)` con valores exactos.
- `NOT NULL`, DEFAULT, UNIQUE y `ON DELETE` **según negocio aprobado**, no usar CASCADE para datos operativos históricos. Mantener RESTRICT salvo justificación; comprobar cascadas de tablas pivote que el esquema ya use.
- Roles y secretos: `password_hash` no se siembra con contraseñas de ejemplo. La instalación del rol `SUPER_ADMIN` requiere aprovisionamiento seguro; las reglas DB no sustituyen RBAC en API.
- No añadir `state`, `date` o columnas genéricas por plantilla a todas las tablas; respetar `user_status`, `membership_status`, `truck_state`, etc.

## Secuencia de trabajo por módulo

1. **Inspeccionar:** catálogo SQL real, migraciones aplicadas, JPA y datos; identificar dependencias directas de la tabla solicitada.
2. **Analizar:** relaciones, invariantes y riesgos para SaaS. Verificar si ya existe módulo equivalente y no duplicarlo.
3. **Proponer:** tabla, tipos, claves, restricciones, endpoints que impactarán (sin implementarlos), cambios en orden de dependencias.
4. **Emitir DDL incremental:** crear **una migración nueva** versionada según convención del proyecto (`Vn__...sql` Flyway o changeset Liquibase). Nunca editar retroactivamente una migración aplicada ni repetir tablas ya creadas.
5. **Evolución con datos:** hacer migración en dos o más pasos si hace falta (añadir columna nullable → poblar/verificar → hacer NOT NULL). Prever detección de huérfanos/duplicados y plan de rollback/restauración; no asumir cambios destructivos sin confirmación.
6. **Actualizar documentación**: esquema DBML **solo como diagrama** (no como fuente de restricciones), diccionario/contrato de módulo según `references/module-contract-template.md`.
7. **Validar:** sintaxis SQL y ejecución en MySQL 8 de prueba si disponible; inspeccionar `SHOW CREATE TABLE`, `information_schema`, FKs, CHECK, índices y ejemplos negativos entre tenants. El chequeo estático o lectura de archivos **no equivale** a ejecución en MySQL.
8. **Entregar:** migraciones nuevas, tabla/campos, FKs, dependencias, contratos, consultas de prueba, diferencias vs. el baseline, resultado real de validación y pendientes para la Skill `generate-module`.

## Contrato que recibe `generate-module`

Crear o actualizar `docs/modules/<modulo>-schema-contract.md` en el repositorio, siguiendo `references/module-contract-template.md` e incluyendo **como mínimo**:

- Fuente (nombre de migración/DDL vigente), tabla(s), estado de aprobación y dependencia SQL.
- PK (tipos, simple/compuesta), campos (nullable, default, longitudes), enums, índices/UNIQUE y `CHECK`.
- FKs incluyendo pares completos `(company_id, fk_id)` y entidad destino, delete/update behavior.
- Semántica de `company_id` y perfiles/roles que acceden a cada tipo de recurso.
- Reglas no exigibles por SQL: permisos, estado de asignación, concurrencia, solapamiento temporal, validaciones de socio/chofer.
- Mapeo JPA sugerido (incluyendo `@EmbeddedId`/`@MapsId`/`@JoinColumns` para compuestas) y comportamiento de listados.
- Casos de prueba positivos y negativos, detalles aún por confirmar.

Si el repositorio no está montado, emitir el contrato como documento para copiar, en lugar de fingir que fue guardado allí.

## Orden incremental recomendado (por dependencias)

1. `company`, `user_table`, `role_table`.
2. `company_user`, `platform_user_role`, `company_user_role`.
3. `client`, `contract_table`, `charge_table`, `container_table`.
4. `partner_table`, `driver_table`, `truck_table`, `truck_driver_assignment` (ajustar el orden según FK).
5. `auxiliary_provider`, `journey_table`.

El orden es una **guía de dependencias**, no una obligación de implementar módulos no pedidos ni una promesa de que solo una tabla crea un módulo Java.

## Material de apoyo

- `references/domain-and-invariants.md`: reglas de negocio y FK por tenant por tabla.
- `references/module-contract-template.md`: documento que se entrega a `generate-module`.
- `references/schema_base_transporte_saas.sql`: baseline SQL MySQL 8, **no migración automática**.
- `references/schema_base_transporte_saas.dbml`: vista de relaciones resumida (no incluye todos los campos).
- `examples/user-database-request.md`: ejemplo de solicitud para preparar la tabla de usuarios.

## Nunca hacer

- Reescribir la base completa ante una solicitud de un módulo.
- Crear `journey_leg`, tramos o recursos propios de la empresa que contradigan reglas confirmadas.
- Aplicar `DROP TABLE`, `TRUNCATE` o `DELETE` de producción como solución rápida.
- Relacionar una empresa directamente como **propietaria** del camión.
- Conceder permisos `SUPER_ADMIN` desde endpoints empresariales.
- Presentar migraciones como probadas sin ejecutarlas.
