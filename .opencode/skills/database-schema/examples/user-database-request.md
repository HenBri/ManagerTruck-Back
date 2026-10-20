# Ejemplo de invocación: especificar la tabla User antes de generar el módulo Java

> Usa `database-schema` para preparar **solo la tabla `user_table`** de ManagerTruck SaaS. Compara el SQL actualmente migrado del repositorio con `references/schema_base_transporte_saas.sql`. Si ya existe, no la crees por segunda vez ni alteres columnas sin necesidad. El esquema de referencia espera: `user_id BIGINT UNSIGNED AUTO_INCREMENT PK`, `user_email VARCHAR(255) UNIQUE NOT NULL`, `password_hash VARCHAR(255) NOT NULL`, `user_name VARCHAR(100) NOT NULL`, `user_lastname VARCHAR(150) NOT NULL`, `user_ci VARCHAR(30) NULL`, `user_phone VARCHAR(25) NULL`, `user_status ENUM(ACTIVE,INACTIVE) DEFAULT ACTIVE` y `user_created_at DATETIME`.
>
> Las cuentas son globales, sin `company_id` o `role_id` directos. La pertenencia a empresas y los roles se gestionarán en `company_user`, `company_user_role`, `platform_user_role` y `role_table`, como módulos separados. No generar datos de superadmin ni contraseñas de prueba.
>
> Antes de escribir SQL, confirma si el proyecto ya utiliza Flyway o Liquibase. Entrega la **migración incremental que corresponda** (o indica que la tabla actual ya satisface el contrato), `docs/modules/user-schema-contract.md`, restricciones y casos de prueba SQL. No implementar el CRUD Java: se hará después con `generate-module`.

## Ejemplo de resultado esperado

- Resultado de comparación de DDL real vs. tabla de referencia.
- Migración `V...__...sql` nueva **solo si hace falta** y la herramienta está disponible/aprobada.
- Contrato con tabla, PK, tipos, restricciones y dependencias.
- Validación en MySQL 8 real si está disponible y pruebas de email UNIQUE/NOT NULL; si no, limitación explícita.
- Siguiente paso: utilizar `generate-module` para implementar User sin suponer relaciones directas.
