# Ejemplo para invocar generate-module: User

> Implementa el módulo `User` de ManagerTruck-Back con la Skill `generate-module`. Antes, ejecuta la Skill `database-schema` para verificar que `user_table` coincide con el SQL migrado y generar `docs/modules/user-schema-contract.md`. Respeta el esquema del repositorio, sin introducir `company_id` ni `role_id` en `user_table`.
>
> **Campos esperados del esquema de referencia** (confirmar contra SQL real): `user_id BIGINT UNSIGNED PK`, `user_email VARCHAR(255) UNIQUE NOT NULL`, `password_hash VARCHAR(255) NOT NULL`, `user_name VARCHAR(100)`, `user_lastname VARCHAR(150)`, `user_ci VARCHAR(30) NULL`, `user_phone VARCHAR(25) NULL`, `user_status ENUM('ACTIVE','INACTIVE')`, `user_created_at DATETIME`.
>
> **Alcance**: alta segura con password hasheado, consulta por id autorizada, `/api/users/me`, modificación de datos personales y desactivación según privilegios. NO asignar roles ni empresas en este módulo: reservar la membresía para `CompanyUser` y roles para `CompanyUserRole` / `PlatformUserRole`. No crear endpoints que den `SUPER_ADMIN`.
>
> **Permisos**: `SUPER_ADMIN` con acceso global auditado; `SECRETARY` solo puede listar usuarios pertenecientes a su empresa y dentro de los permisos definidos; socios y conductores pueden consultar/actualizar su propio perfil. No permitir `findAll()` abierto.
>
> **Estructura**: Controller → UseCase → Service → Repository → Entity; DTOs seguros y Swagger/OpenAPI. Mantener `CommonResponse` si existe. Validar emails, unicidad y campos; implementar manejo de errores coherente.
>
> **Entrega**: indicar archivos nuevos/modificados, rutas y acceso, tests de aislamiento A/B, y salida de Maven real. Preguntar si falta Spring Security; no simular autorización.

Este ejemplo no ordena regenerar todo el esquema ni otros módulos. La versión SQL migrada del proyecto prevalece sobre los campos esperados enumerados aquí.
