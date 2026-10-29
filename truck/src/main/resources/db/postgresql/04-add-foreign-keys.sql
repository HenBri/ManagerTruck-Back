-- PostgreSQL 16+ | Completa las relaciones entre entidades existentes.
-- Puede ejecutarse sobre una base ya creada: solo agrega una FK cuando no
-- existe otra FK equivalente entre las mismas tablas.
BEGIN;

DO $$
BEGIN
  -- Herencia y cuentas de usuario
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'user_table'::regclass
      AND confrelid = 'person'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE user_table ADD CONSTRAINT fk_user_person
      FOREIGN KEY (person_id) REFERENCES person(person_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'partner_table'::regclass
      AND confrelid = 'person'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE partner_table ADD CONSTRAINT fk_partner_person
      FOREIGN KEY (partner_id) REFERENCES person(person_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'driver_table'::regclass
      AND confrelid = 'person'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE driver_table ADD CONSTRAINT fk_driver_person
      FOREIGN KEY (driver_id) REFERENCES person(person_id);
  END IF;

  -- Seguridad y permisos (RBAC)
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'user_role'::regclass
      AND confrelid = 'user_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE user_role ADD CONSTRAINT fk_user_role_user
      FOREIGN KEY (user_id) REFERENCES user_table(user_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'user_role'::regclass
      AND confrelid = 'role_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE user_role ADD CONSTRAINT fk_user_role_role
      FOREIGN KEY (role_id) REFERENCES role_table(role_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'role_permission'::regclass
      AND confrelid = 'role_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE role_permission ADD CONSTRAINT fk_role_permission_role
      FOREIGN KEY (role_id) REFERENCES role_table(role_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'role_permission'::regclass
      AND confrelid = 'permission_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE role_permission ADD CONSTRAINT fk_role_permission_permission
      FOREIGN KEY (permission_id) REFERENCES permission_table(permission_id);
  END IF;

  -- Módulo comercial
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'contract_table'::regclass
      AND confrelid = 'client'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE contract_table ADD CONSTRAINT fk_contract_client
      FOREIGN KEY (client_id) REFERENCES client(client_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'charge_table'::regclass
      AND confrelid = 'contract_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE charge_table ADD CONSTRAINT fk_charge_contract
      FOREIGN KEY (contract_id) REFERENCES contract_table(contract_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'container_table'::regclass
      AND confrelid = 'charge_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE container_table ADD CONSTRAINT fk_container_charge
      FOREIGN KEY (charge_id) REFERENCES charge_table(charge_id);
  END IF;

  -- Flota y asignaciones
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'truck_table'::regclass
      AND confrelid = 'partner_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE truck_table ADD CONSTRAINT fk_truck_partner
      FOREIGN KEY (owner_partner_id) REFERENCES partner_table(partner_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'truck_driver_assignment'::regclass
      AND confrelid = 'truck_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE truck_driver_assignment ADD CONSTRAINT fk_assignment_truck
      FOREIGN KEY (truck_id) REFERENCES truck_table(truck_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'truck_driver_assignment'::regclass
      AND confrelid = 'driver_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE truck_driver_assignment ADD CONSTRAINT fk_assignment_driver
      FOREIGN KEY (driver_id) REFERENCES driver_table(driver_id);
  END IF;

  -- Operaciones, fletes y viajes
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'freight_table'::regclass
      AND confrelid = 'contract_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE freight_table ADD CONSTRAINT fk_freight_contract
      FOREIGN KEY (contract_id) REFERENCES contract_table(contract_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'freight_table'::regclass
      AND confrelid = 'container_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE freight_table ADD CONSTRAINT fk_freight_container
      FOREIGN KEY (container_id) REFERENCES container_table(container_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'freight_table'::regclass
      AND confrelid = 'truck_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE freight_table ADD CONSTRAINT fk_freight_truck
      FOREIGN KEY (truck_id) REFERENCES truck_table(truck_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'freight_table'::regclass
      AND confrelid = 'auxiliary_provider'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE freight_table ADD CONSTRAINT fk_freight_provider
      FOREIGN KEY (auxiliary_provider_id) REFERENCES auxiliary_provider(provider_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'journey_table'::regclass
      AND confrelid = 'freight_table'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE journey_table ADD CONSTRAINT fk_journey_freight
      FOREIGN KEY (freight_id) REFERENCES freight_table(freight_id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'journey_table'::regclass
      AND confrelid = 'truck_driver_assignment'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE journey_table ADD CONSTRAINT fk_journey_assignment
      FOREIGN KEY (assignment_id) REFERENCES truck_driver_assignment(assignment_id);
  END IF;

  -- Módulo RAG / asistente documental
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'assistant_document_version'::regclass
      AND confrelid = 'assistant_document'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE assistant_document_version ADD CONSTRAINT fk_doc_version_document
      FOREIGN KEY (document_id) REFERENCES assistant_document(id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'assistant_chunk'::regclass
      AND confrelid = 'assistant_document'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE assistant_chunk ADD CONSTRAINT fk_chunk_document
      FOREIGN KEY (document_id) REFERENCES assistant_document(id) ON DELETE CASCADE;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conrelid = 'assistant_index_job'::regclass
      AND confrelid = 'assistant_document'::regclass AND contype = 'f'
  ) THEN
    ALTER TABLE assistant_index_job ADD CONSTRAINT fk_index_job_document
      FOREIGN KEY (document_id) REFERENCES assistant_document(id);
  END IF;
END $$;

COMMIT;
