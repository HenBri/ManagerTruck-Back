# 🚛 ManagerTruck - Sistema de Gestión para Transporte Pesado

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue)]()

## 📌 Descripción

**ManagerTruck** es una plataforma de gestión integral para empresas de transporte pesado. Automatiza y optimiza procesos como la administración de clientes, cargas, contenedores, camiones, choferes, socios, contratos y planificación de viajes.

El sistema está Pensado para una arquitectura moderna basada en microservicios. Ofrece escalabilidad y facilidad de mantenimiento.

---

## 🧠 Características Principales

- Gestión de entidades clave del negocio de transporte.
- API REST documentada con OpenAPI/Swagger.
- Posibilidad de despliegue local

---

## 🛠️ Tecnologías

### Backend

- **Lenguaje:** Java 17+
- **Framework:** Spring Boot 3.x
- **Persistencia:** JPA + PostgreSQL 14+
- **Documentación API:** Springdoc OpenAPI 2.6.0
- **Gestor de dependencias:** Maven 3.6+

### Otros

- **Control de versiones:** Git
- **Contenedorización (opcional):** Docker
- **IDE sugerido:** IntelliJ IDEA / Visual Studio Code

---

## 📦 Requisitos Previos

### General
- Java JDK 17+
- Maven 3.6+
- PostgreSQL 14+

### Opcionales
- Docker y Docker Compose
- Git

---

## 🚀 Instalación y Ejecución

### Base de datos

Ejecutar `00-create-database.sql` conectado a una base existente, luego ejecutar en orden `01-create-schema.sql`, `02-seed-base.sql`, `03-seed-kentrukers-demo.sql` y `04-add-foreign-keys.sql` conectado a `truck_sass`. Los archivos están en `truck/src/main/resources/db/postgresql/`. El script de esquema crea las tablas en `public`; no migra datos desde MySQL. Ejemplo con `psql`:

```shell
psql -U postgres -d postgres -f truck/src/main/resources/db/postgresql/00-create-database.sql
psql -U postgres -d truck_sass -f truck/src/main/resources/db/postgresql/01-create-schema.sql
psql -U postgres -d truck_sass -f truck/src/main/resources/db/postgresql/02-seed-base.sql
psql -U postgres -d truck_sass -f truck/src/main/resources/db/postgresql/03-seed-kentrukers-demo.sql
psql -U postgres -d truck_sass -f truck/src/main/resources/db/postgresql/04-add-foreign-keys.sql
```

La semilla demo crea KenTrukers y cuatro cuentas: `superadmin@kentrukers.com`, `secretaria@kentrukers.com`, `socio@kentrukers.com` y `conductor@kentrukers.com`. La contraseña inicial de todas es `kentrukers`; se almacena como hash BCrypt en la base. Cambiarla después del primer inicio de sesión.

Configurar la conexión mediante `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`. La URL por defecto apunta a PostgreSQL local (`truck_sass`); definir usuario y contraseña según la instalación (no hay contraseña predeterminada en la aplicación).

## Estructura del proyecto
  managertruck/
      ├── backend/
      │   ├── src/main/java/...         # Código fuente del backend
      │   ├── resources/                # Archivos de configuración
      │   └── pom.xml                   # Dependencias Maven
      ├── frontend/
      │   └── ...                       # Código Angular (Proximamente)
      └── README.md
