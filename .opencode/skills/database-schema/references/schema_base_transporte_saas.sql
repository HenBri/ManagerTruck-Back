-- Modelo relacional para SaaS de transporte pesado
-- Motor: MySQL 8.0.16+ (CHECK constraints operativas)
-- Alcance: multiempresa, cliente-contrato, camiones HABITUALES exclusivamente de socios y auxilios de terceros.
-- Los datos operativos quedan aislados por company_id con FK compuestas.
-- Los roles globales se separan de los roles por empresa (RBAC).
-- Cada viaje representa UN recorrido completo de origen a destino con UN camion y UN conductor.
-- company_id representa el tenant y NUNCA la propiedad del camion.

CREATE DATABASE IF NOT EXISTS transporte_saas
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE transporte_saas;

-- 1. Empresas inquilinas del SaaS
CREATE TABLE company (
  company_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_name VARCHAR(150) NOT NULL,
  company_nit VARCHAR(30) NULL,
  company_address VARCHAR(255) NULL,
  company_phone VARCHAR(25) NULL,
  company_status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  company_created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Usuarios globales: una cuenta puede operar en varias empresas
CREATE TABLE user_table (
  user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  user_name VARCHAR(100) NOT NULL,
  user_lastname VARCHAR(150) NOT NULL,
  user_ci VARCHAR(30) NULL,
  user_phone VARCHAR(25) NULL,
  user_status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  user_created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_user_email (user_email)
) ENGINE=InnoDB;

-- 3. Roles con alcance explicito: SUPER_ADMIN es GLOBAL, los otros son por empresa.
-- Se evita que el superadmin pueda asignarse como rol de una empresa y viceversa.
CREATE TABLE role_table (
  role_id SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  role_name VARCHAR(50) NOT NULL,
  role_scope ENUM('PLATFORM','COMPANY') NOT NULL,
  role_description VARCHAR(255) NULL,
  UNIQUE KEY uq_role_name (role_name),
  UNIQUE KEY uq_role_id_scope (role_id, role_scope)
) ENGINE=InnoDB;

-- El SUPER_ADMIN no necesita company_id: su permiso es de plataforma.
CREATE TABLE platform_user_role (
  user_id BIGINT UNSIGNED NOT NULL,
  role_id SMALLINT UNSIGNED NOT NULL,
  role_scope ENUM('PLATFORM','COMPANY') NOT NULL DEFAULT 'PLATFORM',
  granted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, role_id),
  KEY idx_pur_role (role_id, role_scope),
  CONSTRAINT fk_pur_user FOREIGN KEY (user_id)
    REFERENCES user_table (user_id) ON DELETE RESTRICT,
  CONSTRAINT fk_pur_role_scope FOREIGN KEY (role_id, role_scope)
    REFERENCES role_table (role_id, role_scope) ON DELETE RESTRICT,
  CONSTRAINT chk_pur_scope CHECK (role_scope = 'PLATFORM')
) ENGINE=InnoDB;

-- La pertenencia controla el ambito de una cuenta.
CREATE TABLE company_user (
  company_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  membership_status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (company_id, user_id),
  KEY idx_membership_user (user_id),
  CONSTRAINT fk_cu_company FOREIGN KEY (company_id)
    REFERENCES company (company_id) ON DELETE RESTRICT,
  CONSTRAINT fk_cu_user FOREIGN KEY (user_id)
    REFERENCES user_table (user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Un mismo usuario puede tener varios roles en la misma empresa.
CREATE TABLE company_user_role (
  company_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  role_id SMALLINT UNSIGNED NOT NULL,
  role_scope ENUM('PLATFORM','COMPANY') NOT NULL DEFAULT 'COMPANY',
  PRIMARY KEY (company_id, user_id, role_id),
  KEY idx_cur_role (role_id, role_scope),
  CONSTRAINT fk_cur_membership FOREIGN KEY (company_id, user_id)
    REFERENCES company_user (company_id, user_id) ON DELETE CASCADE,
  CONSTRAINT fk_cur_role_scope FOREIGN KEY (role_id, role_scope)
    REFERENCES role_table (role_id, role_scope) ON DELETE RESTRICT,
  CONSTRAINT chk_cur_scope CHECK (role_scope = 'COMPANY')
) ENGINE=InnoDB;

-- 3. Clientes de cada empresa (no son compartidos entre inquilinos)
CREATE TABLE client (
  client_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT UNSIGNED NOT NULL,
  client_name VARCHAR(150) NOT NULL,
  client_ci VARCHAR(30) NULL,
  client_email VARCHAR(255) NULL,
  client_phone VARCHAR(25) NULL,
  client_address VARCHAR(255) NULL,
  client_state TINYINT(1) NOT NULL DEFAULT 1,
  client_create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_client_tenant_id (company_id, client_id),
  KEY idx_client_company_name (company_id, client_name),
  CONSTRAINT fk_client_company FOREIGN KEY (company_id)
    REFERENCES company (company_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 4. El contrato pertenece EXPLICITAMENTE a un cliente de la misma empresa
CREATE TABLE contract_table (
  contract_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT UNSIGNED NOT NULL,
  client_id BIGINT UNSIGNED NOT NULL,
  contract_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
  contract_date DATE NOT NULL,
  contract_description VARCHAR(255) NULL,
  contract_type VARCHAR(50) NULL,
  contract_state ENUM('DRAFT','ACTIVE','COMPLETED','CANCELLED') NOT NULL DEFAULT 'DRAFT',
  UNIQUE KEY uq_contract_tenant_id (company_id, contract_id),
  KEY idx_contract_client (company_id, client_id),
  CONSTRAINT fk_contract_client FOREIGN KEY (company_id, client_id)
    REFERENCES client (company_id, client_id) ON DELETE RESTRICT,
  CONSTRAINT chk_contract_amount CHECK (contract_amount >= 0)
) ENGINE=InnoDB;

-- 5. Cada carga pertenece a un contrato; cliente se obtiene via contrato
CREATE TABLE charge_table (
  charge_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT UNSIGNED NOT NULL,
  contract_id BIGINT UNSIGNED NOT NULL,
  charge_description VARCHAR(255) NULL,
  charge_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  charge_type VARCHAR(100) NULL,
  charge_state ENUM('PENDING','IN_TRANSIT','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PENDING',
  UNIQUE KEY uq_charge_tenant_id (company_id, charge_id),
  KEY idx_charge_contract (company_id, contract_id),
  CONSTRAINT fk_charge_contract FOREIGN KEY (company_id, contract_id)
    REFERENCES contract_table (company_id, contract_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 6. Contenedores asignados a una carga
CREATE TABLE container_table (
  container_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT UNSIGNED NOT NULL,
  charge_id BIGINT UNSIGNED NOT NULL,
  container_code VARCHAR(30) NOT NULL,
  container_weight DECIMAL(12,3) NULL,
  container_description VARCHAR(255) NULL,
  container_type VARCHAR(50) NULL,
  container_state ENUM('AVAILABLE','LOADED','IN_TRANSIT','DELIVERED') NOT NULL DEFAULT 'AVAILABLE',
  container_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_container_tenant_id (company_id, container_id),
  UNIQUE KEY uq_container_company_code (company_id, container_code),
  KEY idx_container_charge (company_id, charge_id),
  CONSTRAINT fk_container_charge FOREIGN KEY (company_id, charge_id)
    REFERENCES charge_table (company_id, charge_id) ON DELETE RESTRICT,
  CONSTRAINT chk_container_weight CHECK (container_weight IS NULL OR container_weight >= 0)
) ENGINE=InnoDB;

-- 7. Socios / propietarios / proveedores externos.
-- Puede ser persona o empresa. La cuenta de acceso es OPCIONAL.
CREATE TABLE partner_table (
  partner_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NULL,
  partner_type ENUM('PERSON','BUSINESS') NOT NULL,
  partner_name VARCHAR(150) NOT NULL,
  partner_document VARCHAR(30) NULL,
  partner_phone VARCHAR(25) NULL,
  partner_state ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  UNIQUE KEY uq_partner_tenant_id (company_id, partner_id),
  KEY idx_partner_membership (company_id, user_id),
  CONSTRAINT fk_partner_company FOREIGN KEY (company_id)
    REFERENCES company (company_id) ON DELETE RESTRICT,
  CONSTRAINT fk_partner_membership FOREIGN KEY (company_id, user_id)
    REFERENCES company_user (company_id, user_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 8. Conductores, con o sin cuenta de acceso.
-- person_partner_id solo se informa cuando el CONDUCTOR es la MISMA persona
-- que un socio propietario (no es el campo de empleador/proveedor).
-- Para conductores que NO son socios, person_partner_id = NULL.
-- Validar que person_partner_id apunte a un partner_type = 'PERSON' en backend
-- (MySQL CHECK no puede consultar otras tablas).
CREATE TABLE driver_table (
  driver_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NULL,
  person_partner_id BIGINT UNSIGNED NULL,
  driver_name VARCHAR(150) NOT NULL,
  driver_ci VARCHAR(30) NULL,
  driver_phone VARCHAR(25) NULL,
  driver_license VARCHAR(40) NOT NULL,
  license_category VARCHAR(20) NULL,
  license_expiration DATE NULL,
  driver_state ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  UNIQUE KEY uq_driver_tenant_id (company_id, driver_id),
  UNIQUE KEY uq_driver_company_user (company_id, user_id),
  UNIQUE KEY uq_driver_partner_person (company_id, person_partner_id),
  CONSTRAINT fk_driver_company FOREIGN KEY (company_id)
    REFERENCES company (company_id) ON DELETE RESTRICT,
  CONSTRAINT fk_driver_membership FOREIGN KEY (company_id, user_id)
    REFERENCES company_user (company_id, user_id) ON DELETE RESTRICT,
  CONSTRAINT fk_driver_partner_person FOREIGN KEY (company_id, person_partner_id)
    REFERENCES partner_table (company_id, partner_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 9. TODOS los camiones son propiedad de un socio; nunca de company.
-- company_id = empresa que administra el registro del camion (tenant SaaS).
-- owner_partner_id es obligatorio; la FK compuesta exige mismo tenant.
CREATE TABLE truck_table (
  truck_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT UNSIGNED NOT NULL,
  owner_partner_id BIGINT UNSIGNED NOT NULL,
  truck_board VARCHAR(20) NOT NULL,
  truck_brand VARCHAR(50) NULL,
  truck_model VARCHAR(50) NULL,
  truck_chassis VARCHAR(50) NULL,
  truck_color VARCHAR(30) NULL,
  truck_year SMALLINT UNSIGNED NULL,
  truck_capacity DECIMAL(12,3) NULL,
  truck_state ENUM('ACTIVE','MAINTENANCE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  truck_create_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_truck_tenant_id (company_id, truck_id),
  UNIQUE KEY uq_truck_company_board (company_id, truck_board),
  KEY idx_truck_owner (company_id, owner_partner_id),
  CONSTRAINT fk_truck_owner FOREIGN KEY (company_id, owner_partner_id)
    REFERENCES partner_table (company_id, partner_id) ON DELETE RESTRICT,
  CONSTRAINT chk_truck_capacity CHECK (truck_capacity IS NULL OR truck_capacity >= 0)
) ENGINE=InnoDB;

-- 10. Relacion N:M con historial entre camion de socio y conductor.
-- Un socio puede conducir su propio camion o asignar otra persona.
CREATE TABLE truck_driver_assignment (
  assignment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT UNSIGNED NOT NULL,
  truck_id BIGINT UNSIGNED NOT NULL,
  driver_id BIGINT UNSIGNED NOT NULL,
  assigned_from DATETIME NOT NULL,
  assigned_until DATETIME NULL,
  UNIQUE KEY uq_assignment_tenant_id (company_id, assignment_id),
  UNIQUE KEY uq_assignment_start (company_id, truck_id, driver_id, assigned_from),
  KEY idx_assignment_driver (company_id, driver_id),
  CONSTRAINT fk_tda_truck FOREIGN KEY (company_id, truck_id)
    REFERENCES truck_table (company_id, truck_id) ON DELETE RESTRICT,
  CONSTRAINT fk_tda_driver FOREIGN KEY (company_id, driver_id)
    REFERENCES driver_table (company_id, driver_id) ON DELETE RESTRICT,
  CONSTRAINT chk_assignment_dates CHECK (assigned_until IS NULL OR assigned_until > assigned_from)
) ENGINE=InnoDB;

-- 11. Proveedores ocasionales de auxilio: NO son socios propietarios habituales.
-- BUSINESS: otra empresa de transporte; INDEPENDENT: transportista autonomo.
-- Identidad y contacto son propios de la empresa tenant (no se comparten
-- automaticamente entre empresas del SaaS).
CREATE TABLE auxiliary_provider (
  provider_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT UNSIGNED NOT NULL,
  provider_kind ENUM('BUSINESS','INDEPENDENT') NOT NULL,
  provider_name VARCHAR(150) NOT NULL,
  provider_document VARCHAR(30) NULL,
  provider_phone VARCHAR(25) NULL,
  provider_email VARCHAR(255) NULL,
  provider_status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_aux_provider_tenant_id (company_id, provider_id),
  KEY idx_aux_provider_name (company_id, provider_name),
  CONSTRAINT fk_aux_provider_company FOREIGN KEY (company_id)
    REFERENCES company (company_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 12. Viaje: siempre recorrido completo A -> B, sin tramos ni relevos.
-- EXACTAMENTE una modalidad de ejecucion:
--   PARTNER: camion de socio + conductor habilitado en asignacion.
--   AUXILIARY: proveedor externo/independiente con camion y conductor propios.
-- La empresa del SaaS gestiona el viaje, incluso si lo ejecuta un tercero.
-- Cliente/contrato se obtienen via contenedor -> carga -> contrato -> cliente.
CREATE TABLE journey_table (
  journey_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  company_id BIGINT UNSIGNED NOT NULL,
  container_id BIGINT UNSIGNED NOT NULL,
  journey_origin VARCHAR(255) NOT NULL,
  journey_destination VARCHAR(255) NOT NULL,
  journey_start_date DATETIME NULL,
  journey_end_date DATETIME NULL,
  journey_description VARCHAR(255) NULL,
  journey_type VARCHAR(50) NULL,
  journey_state ENUM('PLANNED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PLANNED',
  execution_type ENUM('PARTNER','AUXILIARY') NOT NULL,
  -- Solo para viajes realizados con camion de socio:
  assignment_id BIGINT UNSIGNED NULL,
  -- Solo para viajes de auxilio/subcontratados:
  auxiliary_provider_id BIGINT UNSIGNED NULL,
  auxiliary_truck_plate VARCHAR(20) NULL,
  auxiliary_driver_name VARCHAR(150) NULL,
  auxiliary_driver_license VARCHAR(40) NULL,
  auxiliary_reason ENUM('TRUCK_SHORTAGE','DRIVER_SHORTAGE','OTHER') NULL,
  agreed_amount DECIMAL(15,2) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_journey_tenant_id (company_id, journey_id),
  KEY idx_journey_container (company_id, container_id),
  KEY idx_journey_start (company_id, journey_start_date),
  KEY idx_journey_assignment (company_id, assignment_id),
  KEY idx_journey_provider (company_id, auxiliary_provider_id),
  CONSTRAINT fk_journey_container FOREIGN KEY (company_id, container_id)
    REFERENCES container_table (company_id, container_id) ON DELETE RESTRICT,
  CONSTRAINT fk_journey_assignment FOREIGN KEY (company_id, assignment_id)
    REFERENCES truck_driver_assignment (company_id, assignment_id) ON DELETE RESTRICT,
  CONSTRAINT fk_journey_provider FOREIGN KEY (company_id, auxiliary_provider_id)
    REFERENCES auxiliary_provider (company_id, provider_id) ON DELETE RESTRICT,
  CONSTRAINT chk_journey_execution CHECK (
    (execution_type = 'PARTNER'
      AND assignment_id IS NOT NULL
      AND auxiliary_provider_id IS NULL
      AND auxiliary_truck_plate IS NULL
      AND auxiliary_driver_name IS NULL
      AND auxiliary_driver_license IS NULL
      AND auxiliary_reason IS NULL
      AND agreed_amount IS NULL)
    OR
    (execution_type = 'AUXILIARY'
      AND assignment_id IS NULL
      AND auxiliary_provider_id IS NOT NULL
      AND auxiliary_truck_plate IS NOT NULL
      AND TRIM(auxiliary_truck_plate) <> ''
      AND auxiliary_driver_name IS NOT NULL
      AND TRIM(auxiliary_driver_name) <> ''
      AND auxiliary_reason IS NOT NULL)
  ),
  CONSTRAINT chk_journey_amount CHECK (agreed_amount IS NULL OR agreed_amount >= 0),
  CONSTRAINT chk_journey_dates CHECK (
    journey_start_date IS NULL OR journey_end_date IS NULL OR journey_end_date >= journey_start_date
  )
) ENGINE=InnoDB;

-- Roles iniciales. SUPER_ADMIN solo en platform_user_role; el resto por empresa.
INSERT INTO role_table (role_name, role_scope, role_description) VALUES
  ('SUPER_ADMIN', 'PLATFORM', 'Control global del SaaS; uso exclusivo del administrador de la plataforma'),
  ('SECRETARY', 'COMPANY', 'Gestion operativa y administrativa de su empresa'),
  ('PARTNER', 'COMPANY', 'Consulta de sus camiones y viajes autorizados'),
  ('DRIVER', 'COMPANY', 'Consulta y actualizacion permitida de sus viajes');

-- Creacion del superadministrador (EJEMPLO, ejecutar solo despues de dar de alta
-- su cuenta con una contrasena hasheada por el backend):
-- INSERT INTO platform_user_role (user_id, role_id)
-- SELECT 123, role_id FROM role_table WHERE role_name = 'SUPER_ADMIN';
-- Sustituir 123 por el ID REAL de tu cuenta; nunca sembrar credenciales por defecto.
-- No crear entrada en company_user para obtener privilegios de plataforma.

-- Ejemplos de asignacion de roles de EMPRESA (requiere membresia previa):
-- INSERT INTO company_user (company_id,user_id) VALUES (1,20);
-- INSERT INTO company_user_role (company_id,user_id,role_id)
-- SELECT 1,20,role_id FROM role_table WHERE role_name='SECRETARY';
-- Un socio que ademas conduce puede tener PARTNER y DRIVER en la misma empresa.

-- Ejemplo de viaje en auxilio (requiere company, container y provider existentes):
-- INSERT INTO journey_table
--   (company_id, container_id, journey_origin, journey_destination,
--    execution_type, auxiliary_provider_id, auxiliary_truck_plate,
--    auxiliary_driver_name, auxiliary_reason, agreed_amount)
-- VALUES (1, 10, 'Cochabamba', 'Santa Cruz', 'AUXILIARY', 5,
--         'XYZ-987', 'Pedro Flores', 'DRIVER_SHORTAGE', 3500.00);
-- Ejemplo de viaje de socio (requiere asignacion camion/conductor existente):
-- INSERT INTO journey_table
--   (company_id, container_id, journey_origin, journey_destination,
--    execution_type, assignment_id)
-- VALUES (1, 10, 'Cochabamba', 'Santa Cruz', 'PARTNER', 7);

-- REGLAS DE NEGOCIO ADICIONALES (BACKEND / TRANSACCIONES):
-- (1) company_id se toma del usuario autenticado y verifica permisos del tenant.
-- (2) Al iniciar viaje PARTNER, validar que assignment_id este vigente para
--     la fecha del viaje y que conductor/camion esten disponibles.
-- (3) Evitar superposicion de viajes del mismo conductor y camion.
-- (4) person_partner_id en driver se usa SOLAMENTE cuando socio y conductor
--     son la misma persona (partner_type debe ser PERSON).
-- (5) Un viaje A->B no admite cambio de camion/conductor en medio del recorrido.
--     En auxilio se selecciona tercero desde el inicio para todo el recorrido.
-- (6) No marcar COMPLETED sin fecha de llegada valida si se exige trazabilidad.
-- (7) Para servicios ocasionales, datos de placa y conductor externo se guardan
--     como fotografia historica en viaje, sin agregarlos a la flota de socios.
-- (8) Si necesitas viajes de cargas sin contenedor, ampliar la asociacion de
--     journey a charge. Aqui preservamos el modelo existente de contenedor.
-- (9) Esquema para BASE NUEVA; no es migracion de versiones anteriores.
-- (10) CHECK es operativo en MySQL 8.0.16+. Ningun CHECK sustituye autorizacion.

-- AUTORIZACION (backend, no basta con la estructura SQL):
-- 11. Verificar SUPER_ADMIN mediante platform_user_role + role_scope='PLATFORM'.
--     Puede consultar/administrar todas las empresas, con auditoria de acciones.
-- 12. Para SECRETARY, PARTNER y DRIVER exigir company_user activo y
--     role_table.role_scope='COMPANY' en el company_id de la solicitud.
-- 13. SECRETARY puede operar dentro de su empresa, no modificar otros tenants.
-- 14. PARTNER accede exclusivamente a partner_table.user_id = cuenta actual,
--     a sus camiones y a sus operaciones permitidas.
-- 15. DRIVER accede a driver_table.user_id = cuenta actual y viajes asignados.
-- 16. La coincidencia entre rol PARTNER y perfil partner, o DRIVER y perfil driver,
--     se administra desde el backend al registrar usuarios (no la impone una FK).
-- 17. Socio tambien conductor: asociar driver_table.person_partner_id con el socio
--     y vincular ambos perfiles al MISMO user_id si tiene acceso al SaaS.
-- 18. Restringir el otorgamiento de SUPER_ADMIN a un proceso seguro de provision.
-- 19. Los usuarios externos de AUXILIARY no requieren cuentas ni rol DRIVER.
