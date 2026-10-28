# 📖 Especificación Técnica Completa de la API para Frontend

Documentación exhaustiva de **todos los endpoints**, parámetros, estructuras de payload JSON, códigos de respuesta, restricciones y reglas de negocio para el backend **ManagerTruck** (Esquema de Base de Datos **Truckv3**).

---

## 📌 1. Información General y Convenciones

- **Base URL:** `http://localhost:8080/api`
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI Schema (JSON):** `http://localhost:8080/v3/api-docs`
- **Formato de datos:** `application/json` (UTF-8).
- **Formato de fechas y horas:** Estándar ISO-8601 UTC: `YYYY-MM-DDTHH:mm:ssZ` (ej. `2026-10-28T14:30:00Z`) o fechas simples `YYYY-MM-DD`.

### Autenticación
- **Esquema:** HTTP Basic Authentication.
- **Cabecera HTTP requerida en peticiones protegidas:**  
  `Authorization: Basic <credenciales_en_base64>`  
  *(donde el contenido codificado es `email:password`)*.
- **Roles:**
  - `ADMIN`: Administrador total del sistema.
  - `SECRETARY`: Operador administrativo (crea y edita flota, clientes, contratos, fletes y viajes).
  - `PARTNER`: Socio propietario (consulta sus propios camiones, fletes y viajes).
  - `DRIVER`: Conductor (consulta sus asignaciones y viajes asignados; actualiza el estado de sus viajes a `EN_PROCESO` o `COMPLETADO`).

### Parámetros de Paginación Estándar (Query Parameters)
Todos los endpoints `GET` de listado aceptan:
| Parámetro | Tipo | Default | Descripción |
|---|---|---|---|
| `page` | `Integer` | `0` | Índice de página (comienza en 0). |
| `size` | `Integer` | `20` | Cantidad de elementos por página. |
| `sort` | `String` | Por ID asc | Campo y dirección de ordenamiento (ej. `dateCreate,desc` o `clientId,asc`). |

### Estructura Estándar de Respuesta Paginada
```json
{
  "code": "200",
  "message": "SUCCESS",
  "page": 0,
  "size": 20,
  "totalElements": 50,
  "totalPages": 3,
  "<items>": [ ... ]
}
```

### Estructura de Respuesta de Error
```json
{
  "code": "400",
  "message": "Detalle del error o validación"
}
```
- `400 Bad Request`: Datos mal formateados, campos obligatorios nulos o validaciones de tamaño/rango fallidas.
- `401 Unauthorized`: Falta de cabecera `Authorization` o credenciales erróneas.
- `403 Forbidden`: El usuario autenticado carece de permisos para realizar la acción.
- `404 Not Found`: El identificador solicitado no existe o fue marcado como eliminado.
- `409 Conflict`: Regla de negocio infringida (códigos o placas duplicadas, transiciones de estado no permitidas, asignación de choferes o camiones inactivos).

---

## 📑 2. Catálogo Completo de Endpoints por Módulo

---

### 1. 👤 Usuarios (`/api/users`)

#### 1.1 Registrar Usuario Público
- **Método:** `POST`
- **Ruta:** `/api/users`
- **Permiso:** Público (sin autenticación).
- **Request Body:**
```json
{
  "email": "usuario@correo.com",           // Obligatorio, max 255, formato email
  "password": "Password123!",              // Obligatorio, min 8, max 72
  "firstName": "Carlos",                  // Obligatorio, max 100
  "lastName": "Pérez",                    // Obligatorio, max 100
  "maternalLastName": "Mamani",           // Opcional, max 100
  "documentNumber": "12345678",           // Opcional, max 30
  "phone": "+591 71234567"                // Opcional, max 25
}
```
- **Response `201 Created`:**
```json
{
  "code": "200",
  "message": "SUCCESS",
  "userId": 1,
  "personId": 1,
  "email": "usuario@correo.com",
  "firstName": "Carlos",
  "lastName": "Pérez",
  "maternalLastName": "Mamani",
  "documentNumber": "12345678",
  "phone": "+591 71234567",
  "status": "ACTIVE",
  "dateCreate": "2026-10-28T12:00:00Z"
}
```

#### 1.2 Registrar Usuario por Administrador
- **Método:** `POST`
- **Ruta:** `/api/users/admin`
- **Permiso:** `ADMIN`
- **Request Body:** Mismo formato que 1.1.
- **Response:** `201 Created` (Mismo formato que 1.1).

#### 1.3 Consultar Usuario Autenticado Actual
- **Método:** `GET`
- **Ruta:** `/api/users/me`
- **Permiso:** Cualquier usuario autenticado.
- **Response `200 OK`:** Datos del usuario logueado en la sesión actual.

#### 1.4 Consultar Usuario por ID
- **Método:** `GET`
- **Ruta:** `/api/users/{userId}`
- **Permiso:** El propio usuario dueño de la cuenta o `ADMIN`.
- **Response `200 OK`:** Datos del usuario solicitado.

#### 1.5 Actualizar Datos Personales de Usuario
- **Método:** `PUT`
- **Ruta:** `/api/users/{userId}`
- **Permiso:** El propio usuario o `ADMIN`.
- **Request Body:**
```json
{
  "firstName": "Carlos Andrés",
  "lastName": "Pérez",
  "maternalLastName": "Mamani",
  "documentNumber": "12345678",
  "phone": "+591 79998877"
}
```
- **Response `200 OK`:** Objeto `UserResponse` actualizado.

#### 1.6 Cambiar Contraseña Propia
- **Método:** `PUT`
- **Ruta:** `/api/users/{userId}/password`
- **Permiso:** Únicamente el usuario dueño de la cuenta.
- **Request Body:**
```json
{
  "currentPassword": "Password123!",
  "newPassword": "NuevaPassword456!"
}
```
- **Response `200 OK`:** `UserResponse`.

#### 1.7 Activar o Desactivar Usuario
- **Método:** `PUT`
- **Ruta:** `/api/users/{userId}/status`
- **Permiso:** `ADMIN`. *(Un usuario no puede desactivar su propia cuenta)*.
- **Request Body:**
```json
{
  "status": "INACTIVE"                    // "ACTIVE" | "INACTIVE"
}
```
- **Response `200 OK`:** `UserResponse`.

#### 1.8 Listar Todos los Usuarios
- **Método:** `GET`
- **Ruta:** `/api/users`
- **Permiso:** `ADMIN`.
- **Response `200 OK`:** `UsersResponse` paginado.

---

### 2. 🔐 Roles (`/api/roles`)

#### 2.1 Listar Catálogo de Roles
- **Método:** `GET`
- **Ruta:** `/api/roles`
- **Permiso:** Cualquier usuario autenticado.
- **Response `200 OK`:**
```json
{
  "code": "200",
  "message": "SUCCESS",
  "roles": [
    {
      "roleId": 1,
      "name": "ADMIN",
      "description": "Administrador total del sistema"
    },
    {
      "roleId": 2,
      "name": "SECRETARY",
      "description": "Gestión operativa comercial y logística"
    },
    {
      "roleId": 3,
      "name": "PARTNER",
      "description": "Socio dueño de camiones"
    },
    {
      "roleId": 4,
      "name": "DRIVER",
      "description": "Conductor de transporte"
    }
  ]
}
```

#### 2.2 Consultar Rol por ID
- **Método:** `GET`
- **Ruta:** `/api/roles/{roleId}`
- **Permiso:** Autenticado.

#### 2.3 Crear Rol
- **Método:** `POST`
- **Ruta:** `/api/roles`
- **Permiso:** `ADMIN`.
- **Request Body:**
```json
{
  "name": "OPERADOR_LOGISTICO",
  "description": "Control y seguimiento de cargas"
}
```

#### 2.4 Actualizar Rol
- **Método:** `PUT`
- **Ruta:** `/api/roles/{roleId}`
- **Permiso:** `ADMIN`.

---

### 3. 🏢 Clientes (`/api/clients`)

#### 3.1 Listar Clientes
- **Método:** `GET`
- **Ruta:** `/api/clients`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Response `200 OK`:** `ClientsResponse` paginado.

#### 3.2 Consultar Cliente por ID
- **Método:** `GET`
- **Ruta:** `/api/clients/{clientId}`
- **Permiso:** `ADMIN`, `SECRETARY`.

#### 3.3 Crear Cliente
- **Método:** `POST`
- **Ruta:** `/api/clients`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Request Body:**
```json
{
  "clientName": "Cervecería Boliviana Nacional S.A.", // Obligatorio, max 100
  "clientNit": "1028374021",                          // Obligatorio, max 20, único
  "clientPhone": "22456789",                          // Opcional, max 20
  "clientEmail": "ventas@cbn.com.bo",                 // Opcional, max 100
  "clientAddress": "Av. Montes #400, La Paz",         // Opcional, max 200
  "contactPerson": "Lic. Roberto Gómez",              // Opcional, max 100
  "contactPhone": "71543210"                          // Opcional, max 25
}
```
- **Response `201 Created`:**
```json
{
  "code": "200",
  "message": "SUCCESS",
  "clientId": 1,
  "clientName": "Cervecería Boliviana Nacional S.A.",
  "clientNit": "1028374021",
  "clientPhone": "22456789",
  "clientEmail": "ventas@cbn.com.bo",
  "clientAddress": "Av. Montes #400, La Paz",
  "contactPerson": "Lic. Roberto Gómez",
  "contactPhone": "71543210",
  "clientState": true,
  "dateCreate": "2026-10-28T14:00:00Z"
}
```

#### 3.4 Actualizar Cliente
- **Método:** `PUT`
- **Ruta:** `/api/clients/{clientId}`
- **Permiso:** `ADMIN`, `SECRETARY`.

#### 3.5 Activar / Desactivar Cliente
- **Método:** `PUT`
- **Ruta:** `/api/clients/{clientId}/status`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Request Body:**
```json
{
  "clientState": false
}
```

---

### 4. 📄 Contratos Comerciales (`/api/contracts`)

#### 4.1 Listar Contratos
- **Método:** `GET`
- **Ruta:** `/api/contracts`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Query Params:** `clientId` (opcional).

#### 4.2 Consultar Contrato por ID
- **Método:** `GET`
- **Ruta:** `/api/contracts/{contractId}`
- **Permiso:** `ADMIN`, `SECRETARY`.

#### 4.3 Crear Contrato
- **Método:** `POST`
- **Ruta:** `/api/contracts`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Request Body:**
```json
{
  "clientId": 1,                                       // Obligatorio
  "contractCode": "CTR-2026-0089",                    // Obligatorio, max 40, único
  "contractStartDate": "2026-11-01T00:00:00Z",        // Obligatorio
  "contractEndDate": "2027-10-31T23:59:59Z",          // Opcional
  "contractDescription": "Transporte de cebada a granel",
  "contractAmount": 120000.00,                        // Decimal(13,2) >= 0
  "operationType": "IMPORTACION",                     // "IMPORTACION" | "EXPORTACION" | "NACIONAL"
  "originCountry": "Argentina",                       // Max 80
  "destinationCountry": "Bolivia",                    // Max 80
  "agreedWeightKg": 500000.000,                       // Decimal(12,3) >= 0
  "weightToleranceKg": 1000.000                       // Decimal(12,3) >= 0
}
```
- **Response `201 Created`:**
```json
{
  "code": "200",
  "message": "SUCCESS",
  "contractId": 1,
  "clientId": 1,
  "contractCode": "CTR-2026-0089",
  "contractStartDate": "2026-11-01T00:00:00Z",
  "contractEndDate": "2027-10-31T23:59:59Z",
  "contractDescription": "Transporte de cebada a granel",
  "contractAmount": 120000.00,
  "operationType": "IMPORTACION",
  "originCountry": "Argentina",
  "destinationCountry": "Bolivia",
  "agreedWeightKg": 500000.000,
  "weightToleranceKg": 1000.000,
  "state": "PENDIENTE",
  "dateCreate": "2026-10-28T14:10:00Z"
}
```

#### 4.4 Actualizar Contrato
- **Método:** `PUT`
- **Ruta:** `/api/contracts/{contractId}`
- **Permiso:** `ADMIN`, `SECRETARY`.

#### 4.5 Cambiar Estado del Contrato
- **Método:** `PUT`
- **Ruta:** `/api/contracts/{contractId}/status`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Valores posibles:** `PENDIENTE`, `EN_PROCESO`, `ENTREGADO`, `CANCELADO`.
- **Request Body:**
```json
{
  "status": "EN_PROCESO"
}
```

---

### 5. 📦 Cargas (`/api/charges`) y Contenedores (`/api/containers`)

#### 5.1 Listar Cargas
- **Método:** `GET`
- **Ruta:** `/api/charges`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Query Params:** `contractId` (opcional).

#### 5.2 Crear Carga
- **Método:** `POST`
- **Ruta:** `/api/charges`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Request Body:**
```json
{
  "contractId": 1,                                    // Obligatorio
  "chargeType": "Materia Prima",                      // Obligatorio, max 100
  "chargeWeight": 25.500,                             // Obligatorio, decimal >= 0
  "chargeVolume": 18.000,                             // Opcional, decimal >= 0
  "chargeDescription": "Cebada malteada embolsada",
  "cargoReference": "REF-CBN-01",                     // Max 80
  "goodsDescription": "Cebada de primera calidad",    // Max 255
  "hazardousGoods": false,                            // Boolean
  "handlingInstructions": "Mantener seco"             // TEXT
}
```

#### 5.3 Actualizar Carga
- **Método:** `PUT`
- **Ruta:** `/api/charges/{chargeId}`
- **Permiso:** `ADMIN`, `SECRETARY`.

#### 5.4 Cambiar Estado de Carga
- **Método:** `PUT`
- **Ruta:** `/api/charges/{chargeId}/status`
- **Valores posibles:** `PENDIENTE`, `EN_PROCESO`, `ENTREGADO`, `CANCELADO`.
- **Request Body:**
```json
{
  "status": "EN_PROCESO"
}
```

#### 5.5 Listar Contenedores
- **Método:** `GET`
- **Ruta:** `/api/containers`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Query Params:** `chargeId` (opcional).

#### 5.6 Crear Contenedor
- **Método:** `POST`
- **Ruta:** `/api/containers`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Request Body:**
```json
{
  "chargeId": 1,                                      // Obligatorio
  "containerCode": "MSKU5544332",                     // Obligatorio, max 20, único
  "containerType": "DRY",                             // Obligatorio, max 50
  "containerCapacity": 28.000,                        // Decimal >= 0
  "isoSize": "40HC",                                  // "20FT" | "40FT" | "40HC"
  "containerFormat": "HIGH_CUBE",                     // Max 30
  "sealNumber": "SEAL-BO-5544",                       // Max 50
  "tareWeightKg": 3900.000,                           // Decimal(9,3) >= 0
  "payloadCapacityKg": 26580.000,                     // Decimal(9,3) >= 0
  "isReefer": false                                   // Boolean
}
```

#### 5.7 Actualizar Contenedor
- **Método:** `PUT`
- **Ruta:** `/api/containers/{containerId}`
- **Permiso:** `ADMIN`, `SECRETARY`.

#### 5.8 Cambiar Estado de Contenedor
- **Método:** `PUT`
- **Ruta:** `/api/containers/{containerId}/status`
- **Request Body:**
```json
{
  "state": "DISPONIBLE"                               // "DISPONIBLE" | "EN_USO" | "MANTENIMIENTO" | "CANCELADO"
}
```

---

### 6. 🤝 Socios Propietarios (`/api/partners`)

#### 6.1 Listar Socios
- **Método:** `GET`
- **Ruta:** `/api/partners`
- **Permiso:** `ADMIN`, `SECRETARY` (ven todos); `PARTNER` (solo ve su propio registro).

#### 6.2 Consultar Socio por ID
- **Método:** `GET`
- **Ruta:** `/api/partners/{partnerId}`
- **Permiso:** `ADMIN`, `SECRETARY`, o el propio `PARTNER`.

#### 6.3 Crear Socio
- **Método:** `POST`
- **Ruta:** `/api/partners`
- **Permiso:** `ADMIN`, `SECRETARY`.
*(Nota: Si `personId` es `null`, se crea automáticamente el registro en `person_table` y la cuenta en `user_table` con rol `PARTNER`).*
- **Request Body:**
```json
{
  "personId": null,
  "partnerType": "PERSON",                            // "PERSON" | "COMPANY"
  "firstName": "Raúl",
  "lastName": "Quispe",
  "maternalLastName": "Condori",
  "documentNumber": "5948372",
  "email": "raul.quispe@socio.com",
  "phone": "72098765",
  "companyName": null,                                // Si partnerType == "COMPANY"
  "taxId": null                                       // NIT si partnerType == "COMPANY"
}
```

#### 6.4 Actualizar Socio
- **Método:** `PUT`
- **Ruta:** `/api/partners/{partnerId}`
- **Permiso:** `ADMIN`, `SECRETARY`.

#### 6.5 Cambiar Estado de Socio
- **Método:** `PUT`
- **Ruta:** `/api/partners/{partnerId}/status`
- **Request Body:**
```json
{
  "status": "ACTIVO"                                  // "ACTIVO" | "INACTIVO"
}
```

---

### 7. 🚛 Camiones (`/api/trucks`)

#### 7.1 Listar Camiones
- **Método:** `GET`
- **Ruta:** `/api/trucks`
- **Permiso:** `ADMIN`, `SECRETARY` (ven todos); `PARTNER` (solo ve sus propios camiones).

#### 7.2 Consultar Camión por ID
- **Método:** `GET`
- **Ruta:** `/api/trucks/{truckId}`
- **Permiso:** `ADMIN`, `SECRETARY`, o `PARTNER` dueño del camión.

#### 7.3 Crear Camión
- **Método:** `POST`
- **Ruta:** `/api/trucks`
- **Permiso:** `ADMIN`, `SECRETARY`.
*(Todo camión pertenece obligatoriamente a un socio activo)*.
- **Request Body:**
```json
{
  "ownerPartnerId": 1,                                // Obligatorio
  "truckBoard": "5020-ABC",                           // Obligatorio, max 20, único
  "truckBrand": "Scania",                             // Max 50
  "truckModel": "R 450",                              // Max 50
  "truckChassis": "YS2R4X2000214589",                 // Max 50
  "truckColor": "Azul",                               // Max 30
  "truckYear": 2023,                                  // Integer
  "truckCapacity": 32.000,                            // Decimal(9,3)
  "truckEmptyWeightKg": 8450.000,                     // Decimal(9,3)
  "trailerEmptyWeightKg": 6200.000,                   // Decimal(9,3)
  "maxGrossWeightKg": 48000.000,                      // Decimal(9,3)
  "truckType": "TRACTOCAMION",                        // Max 50
  "chassisPlate": "REM-4455",                         // Max 20
  "insuranceExpiration": "2027-12-31",                // YYYY-MM-DD
  "technicalInspectionExpiration": "2027-08-30"       // YYYY-MM-DD
}
```

#### 7.4 Actualizar Camión
- **Método:** `PUT`
- **Ruta:** `/api/trucks/{truckId}`
- **Permiso:** `ADMIN`, `SECRETARY`.

#### 7.5 Cambiar Estado de Camión
- **Método:** `PUT`
- **Ruta:** `/api/trucks/{truckId}/status`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Request Body:**
```json
{
  "state": "ACTIVO"                                   // "ACTIVO" | "MANTENIMIENTO" | "INACTIVO"
}
```

---

### 8. 👨‍✈️ Conductores (`/api/drivers`)

#### 8.1 Listar Conductores
- **Método:** `GET`
- **Ruta:** `/api/drivers`
- **Permiso:** `ADMIN`, `SECRETARY` (ven todos); `DRIVER` (solo su perfil).

#### 8.2 Consultar Conductor por ID
- **Método:** `GET`
- **Ruta:** `/api/drivers/{driverId}`
- **Permiso:** `ADMIN`, `SECRETARY`, o propio `DRIVER`.

#### 8.3 Crear Conductor
- **Método:** `POST`
- **Ruta:** `/api/drivers`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Request Body:**
```json
{
  "personId": null,
  "firstName": "Gustavo",
  "lastName": "Torres",
  "maternalLastName": "Mendoza",
  "documentNumber": "6849301",
  "email": "gustavo.torres@chofer.com",
  "phone": "78012345",
  "licenseNumber": "LIC-6849301-C",                   // Obligatorio, max 40, único
  "licenseCategory": "CATEGORIA_C",                   // Obligatorio, max 20
  "licenseExpiration": "2028-11-20"                   // Obligatorio, YYYY-MM-DD
}
```

#### 8.4 Actualizar Conductor
- **Método:** `PUT`
- **Ruta:** `/api/drivers/{driverId}`
- **Permiso:** `ADMIN`, `SECRETARY`.

#### 8.5 Cambiar Estado de Conductor
- **Método:** `PUT`
- **Ruta:** `/api/drivers/{driverId}/status`
- **Request Body:**
```json
{
  "status": "ACTIVO"                                  // "ACTIVO" | "INACTIVO"
}
```

---

### 9. 🔗 Asignación Conductor - Camión (`/api/assignments`)

#### 9.1 Listar Historial de Asignaciones
- **Método:** `GET`
- **Ruta:** `/api/assignments`
- **Permiso:** `ADMIN`, `SECRETARY` (todas); `PARTNER` (sus camiones); `DRIVER` (sus asignaciones).
- **Query Params:** `truckId`, `driverId`.

#### 9.2 Consultar Asignación por ID
- **Método:** `GET`
- **Ruta:** `/api/assignments/{assignmentId}`

#### 9.3 Asignar Conductor a Camión
- **Método:** `POST`
- **Ruta:** `/api/assignments`
- **Permiso:** `ADMIN`, `SECRETARY`.
*(El camión y el conductor deben estar en estado `ACTIVO`)*.
- **Request Body:**
```json
{
  "truckId": 1,                                       // Obligatorio
  "driverId": 1,                                      // Obligatorio
  "assignedFrom": "2026-10-28T08:00:00Z",             // Obligatorio
  "assignedUntil": null                               // Opcional (null si es abierta e indefinida)
}
```
- **Response `201 Created`:**
```json
{
  "code": "200",
  "message": "SUCCESS",
  "assignmentId": 1,
  "truckId": 1,
  "driverId": 1,
  "assignedFrom": "2026-10-28T08:00:00Z",
  "assignedUntil": null,
  "state": "ACTIVE",
  "dateCreate": "2026-10-28T08:00:00Z"
}
```

#### 9.4 Finalizar Asignación
- **Método:** `PUT`
- **Ruta:** `/api/assignments/{assignmentId}/end`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Request Body:**
```json
{
  "assignedUntil": "2026-10-28T18:00:00Z"             // Obligatorio, posterior a assignedFrom
}
```

---

### 10. 🚚 Fletes (`/api/freights`)

El **Flete** es el objeto logístico de planificación que conecta el **Contrato**, el **Contenedor** y el recurso de transporte (**Camión de Socio** o **Proveedor Auxiliar externo**).

#### 10.1 Listar Fletes
- **Método:** `GET`
- **Ruta:** `/api/freights`
- **Permiso:** `ADMIN`, `SECRETARY`, `PARTNER`, `DRIVER`.
- **Query Params:**
  - `contractId`: Filtra por contrato.
  - `truckId`: Filtra por camión.
  - `state`: Filtra por estado (`PENDIENTE`, `EN_PROCESO`, `COMPLETADO`, `CANCELADO`).

#### 10.2 Consultar Flete por ID
- **Método:** `GET`
- **Ruta:** `/api/freights/{freightId}`

#### 10.3 Crear Flete
- **Método:** `POST`
- **Ruta:** `/api/freights`
- **Permiso:** `ADMIN`, `SECRETARY`.
*(Requiere `truckId` si lo transporta un camión de socio, o `auxiliaryProviderId` si es subcontratado)*.
- **Request Body:**
```json
{
  "contractId": 1,                                    // Obligatorio
  "containerId": 1,                                   // Obligatorio
  "truckId": 1,                                       // Opcional (si camión propio)
  "auxiliaryProviderId": null,                        // Opcional (si proveedor externo)
  "auxiliaryTruckPlate": null,                        // Opcional (placa auxiliar)
  "freightCode": "FLT-2026-001",                      // Obligatorio, max 40, único
  "freightDescription": "Flete Puerto Arica -> Aduana La Paz",
  "scheduledDeparture": "2026-11-05T07:00:00Z",       // Opcional
  "scheduledArrival": "2026-11-06T19:00:00Z",         // Opcional
  "freightAmount": 3200.00                            // Decimal(13,2) >= 0
}
```
- **Response `201 Created`:**
```json
{
  "code": "200",
  "message": "SUCCESS",
  "freightId": 1,
  "contractId": 1,
  "containerId": 1,
  "truckId": 1,
  "auxiliaryProviderId": null,
  "auxiliaryTruckPlate": null,
  "freightDescription": "Flete Puerto Arica -> Aduana La Paz",
  "freightCode": "FLT-2026-001",
  "scheduledDeparture": "2026-11-05T07:00:00Z",
  "scheduledArrival": "2026-11-06T19:00:00Z",
  "actualDeparture": null,
  "actualArrival": null,
  "deliveryNotes": null,
  "freightAmount": 3200.00,
  "state": "PENDIENTE",
  "dateCreate": "2026-10-28T14:30:00Z"
}
```

#### 10.4 Actualizar Flete
- **Método:** `PUT`
- **Ruta:** `/api/freights/{freightId}`
- **Permiso:** `ADMIN`, `SECRETARY`. *(Solo se permite editar fletes en estado `PENDIENTE`)*.
- **Request Body:**
```json
{
  "truckId": 1,
  "auxiliaryProviderId": null,
  "auxiliaryTruckPlate": null,
  "freightDescription": "Flete corregido Arica -> El Alto",
  "scheduledDeparture": "2026-11-05T08:00:00Z",
  "scheduledArrival": "2026-11-06T20:00:00Z",
  "deliveryNotes": "Cliente solicita aviso 2 horas antes de arribar",
  "freightAmount": 3300.00
}
```

#### 10.5 Cambiar Estado de Flete
- **Método:** `PUT`
- **Ruta:** `/api/freights/{freightId}/status`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Transiciones válidas:**
  - `PENDIENTE` $\rightarrow$ `EN_PROCESO` o `CANCELADO`
  - `EN_PROCESO` $\rightarrow$ `COMPLETADO` o `CANCELADO`
- **Request Body:**
```json
{
  "state": "EN_PROCESO"                               // "EN_PROCESO" | "COMPLETADO" | "CANCELADO"
}
```

---

### 11. 🗺️ Viajes (`/api/journeys`)

El **Viaje** representa la ejecución en carretera del flete (`freightId`). Relación 1 a 1 con el flete.

#### 11.1 Listar Viajes
- **Método:** `GET`
- **Ruta:** `/api/journeys`
- **Permiso:** `ADMIN`, `SECRETARY` (todos); `PARTNER` (sus camiones); `DRIVER` (sus viajes asignados).
- **Query Params:**
  - `freightId`: ID del flete.
  - `assignmentId`: ID de asignación camión-chofer.
  - `state`: `PENDIENTE`, `EN_PROCESO`, `COMPLETADO`, `CANCELADO`.

#### 11.2 Consultar Viaje por ID
- **Método:** `GET`
- **Ruta:** `/api/journeys/{journeyId}`

#### 11.3 Crear Viaje
- **Método:** `POST`
- **Ruta:** `/api/journeys`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Request Body:**
```json
{
  "freightId": 1,                                     // Obligatorio, único (1 viaje por flete)
  "journeyOrigin": "Arica, Chile",                    // Obligatorio, max 255
  "journeyDestination": "La Paz, Bolivia",            // Obligatorio, max 255
  "plannedStartDate": "2026-11-05T07:00:00Z",         // Opcional
  "plannedEndDate": "2026-11-06T19:00:00Z",           // Opcional
  "journeyDescription": "Viaje directo por Tambo Quemado",
  "journeyType": "INTERNACIONAL",                     // Max 50
  "assignmentId": 1,                                  // ID de asignación (Camión + Chofer)
  "auxiliaryDriverName": null,                        // Solo si es chofer de relevo / emergencia
  "auxiliaryDriverLicense": null,
  "auxiliaryReason": null,                            // "CAPACIDAD" | "FALLA_MECANICA" | "URGENCIA" | "OTRO"
  "agreedAmount": null
}
```
- **Response `201 Created`:**
```json
{
  "code": "200",
  "message": "SUCCESS",
  "journeyId": 1,
  "freightId": 1,
  "journeyOrigin": "Arica, Chile",
  "journeyDestination": "La Paz, Bolivia",
  "journeyStartDate": null,
  "journeyEndDate": null,
  "plannedStartDate": "2026-11-05T07:00:00Z",
  "plannedEndDate": "2026-11-06T19:00:00Z",
  "deliveryConfirmedAt": null,
  "recipientName": null,
  "proofOfDeliveryReference": null,
  "observations": null,
  "assignmentId": 1,
  "auxiliaryDriverName": null,
  "auxiliaryDriverLicense": null,
  "auxiliaryReason": null,
  "agreedAmount": null,
  "state": "PENDIENTE",
  "dateCreate": "2026-10-28T14:40:00Z"
}
```

#### 11.4 Actualizar Viaje
- **Método:** `PUT`
- **Ruta:** `/api/journeys/{journeyId}`
- **Permiso:** `ADMIN`, `SECRETARY`. *(Solo se pueden editar viajes en estado `PENDIENTE`)*.
- **Request Body:**
```json
{
  "journeyOrigin": "Arica, Chile",
  "journeyDestination": "El Alto, Bolivia",
  "plannedStartDate": "2026-11-05T08:00:00Z",
  "plannedEndDate": "2026-11-06T20:00:00Z",
  "journeyDescription": "Ruta actualizada",
  "journeyType": "INTERNACIONAL",
  "observations": "Pasar control fronterizo en horario diurno",
  "assignmentId": 1
}
```

#### 11.5 Cambiar Estado del Viaje (Operación del Chofer / Admin)
- **Método:** `PUT`
- **Ruta:** `/api/journeys/{journeyId}/status`
- **Permiso:** `ADMIN`, `SECRETARY`, o el `DRIVER` asignado al viaje.
- **Comportamiento automático del Backend:**
  - Al cambiar a `EN_PROCESO`: Si `journeyStartDate` es nulo, se le asigna la fecha y hora actual automáticamente.
  - Al cambiar a `COMPLETADO`: Si `journeyEndDate` es nulo, se le asigna la fecha y hora actual automáticamente.
- **Request Body:**
```json
{
  "status": "EN_PROCESO"                              // "EN_PROCESO" | "COMPLETADO" | "CANCELADO"
}
```

---

### 12. 🆘 Proveedores Auxiliares (`/api/auxiliary-providers`)

Gestión de empresas transportistas terceras o choferes independientes contratados para emergencias o apoyo.

#### 12.1 Listar Proveedores Auxiliares
- **Método:** `GET`
- **Ruta:** `/api/auxiliary-providers`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Query Params:**
  - `kind`: `COMPANY` o `INDEPENDENT`.
  - `name`: Búsqueda parcial de texto por nombre.

#### 12.2 Crear Proveedor Auxiliar
- **Método:** `POST`
- **Ruta:** `/api/auxiliary-providers`
- **Permiso:** `ADMIN`, `SECRETARY`.
- **Request Body:**
```json
{
  "providerKind": "COMPANY",                          // "COMPANY" | "INDEPENDENT"
  "providerName": "Transportes TransSur S.R.L.",       // Obligatorio, max 100
  "providerDocument": "4958201019",                   // Max 30
  "providerPhone": "22884411",                        // Max 20
  "providerEmail": "contacto@transsur.com"            // Max 100
}
```

#### 12.3 Actualizar Proveedor Auxiliar
- **Método:** `PUT`
- **Ruta:** `/api/auxiliary-providers/{providerId}`

#### 12.4 Cambiar Estado de Proveedor Auxiliar
- **Método:** `PUT`
- **Ruta:** `/api/auxiliary-providers/{providerId}/status`
- **Request Body:**
```json
{
  "status": "ACTIVO"                                  // "ACTIVO" | "INACTIVO"
}
```

---

## 🧩 3. Diccionario Completo de Enums para Formularios y Selects

| Enum | Clave en JSON | Valores Válidos | Uso |
|---|---|---|---|
| **Estado de Contrato** | `status` / `state` | `PENDIENTE`, `EN_PROCESO`, `ENTREGADO`, `CANCELADO` | Contratos comerciales |
| **Tipo de Operación** | `operationType` | `IMPORTACION`, `EXPORTACION`, `NACIONAL` | Contratos comerciales |
| **Estado de Carga** | `status` / `state` | `PENDIENTE`, `EN_PROCESO`, `ENTREGADO`, `CANCELADO` | Cargas |
| **Estado de Contenedor** | `state` | `DISPONIBLE`, `EN_USO`, `MANTENIMIENTO`, `CANCELADO` | Contenedores |
| **Tamaño ISO Contenedor**| `isoSize` | `20FT`, `40FT`, `40HC` | Contenedores |
| **Tipo de Socio** | `partnerType` | `PERSON`, `COMPANY` | Socios de camiones |
| **Tipo de Proveedor** | `providerKind` | `COMPANY`, `INDEPENDENT` | Auxiliares externos |
| **Motivo de Auxilio** | `auxiliaryReason` | `CAPACIDAD`, `FALLA_MECANICA`, `URGENCIA`, `OTRO` | Viajes con apoyo |
| **Estado de Flete** | `state` | `PENDIENTE`, `EN_PROCESO`, `COMPLETADO`, `CANCELADO` | Fletes |
| **Estado de Viaje** | `status` / `state` | `PENDIENTE`, `EN_PROCESO`, `COMPLETADO`, `CANCELADO` | Viajes ejecutados |
| **Estado de Camión** | `state` | `ACTIVO`, `MANTENIMIENTO`, `INACTIVO` | Camiones |
| **Estado General** | `status` / `state` | `ACTIVO`, `INACTIVO` | Usuarios, Socios, Choferes, Clientes, Auxiliares |
