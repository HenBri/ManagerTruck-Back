-- Aplicar en instalaciones existentes: un contenedor solo puede pertenecer
-- a un flete activo a la vez.
CREATE OR REPLACE FUNCTION validate_freight_container_availability() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  IF NEW.is_delete OR NEW.state IN ('COMPLETADO','CANCELADO') THEN RETURN NEW; END IF;
  PERFORM pg_advisory_xact_lock(4001, hashtext(NEW.container_id::text));
  IF EXISTS (
    SELECT 1 FROM freight_table f
     WHERE f.freight_id<>NEW.freight_id AND f.container_id=NEW.container_id
       AND NOT f.is_delete AND f.state IN ('PENDIENTE','EN_PROCESO')
  ) THEN
    RAISE EXCEPTION 'Contenedor ya asignado a un flete activo';
  END IF;
  RETURN NEW;
END $$;

DROP TRIGGER IF EXISTS trg_freight_container_availability ON freight_table;
CREATE TRIGGER trg_freight_container_availability BEFORE INSERT OR UPDATE OF
  container_id,state,is_delete ON freight_table
FOR EACH ROW EXECUTE FUNCTION validate_freight_container_availability();
