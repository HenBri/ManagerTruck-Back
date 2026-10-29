-- Aplicar en instalaciones existentes: limita los contenedores activos
-- de cada contrato a expected_container_count.
CREATE OR REPLACE FUNCTION validate_contract_container_limit() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE v_contract_id BIGINT; v_expected INTEGER; v_current INTEGER;
BEGIN
  SELECT ch.contract_id INTO v_contract_id
    FROM charge_table ch WHERE ch.charge_id=NEW.charge_id;
  PERFORM 1 FROM contract_table WHERE contract_id=v_contract_id FOR UPDATE;
  SELECT expected_container_count INTO v_expected
    FROM contract_table WHERE contract_id=v_contract_id;
  IF v_expected IS NOT NULL THEN
    SELECT COUNT(*) INTO v_current
      FROM container_table c JOIN charge_table ch ON ch.charge_id=c.charge_id
      WHERE ch.contract_id=v_contract_id AND NOT c.is_delete;
    IF v_current >= v_expected THEN
      RAISE EXCEPTION 'El contrato ya alcanzo el maximo de % contenedores', v_expected;
    END IF;
  END IF;
  RETURN NEW;
END $$;

DROP TRIGGER IF EXISTS trg_validate_contract_container_limit ON container_table;
CREATE TRIGGER trg_validate_contract_container_limit BEFORE INSERT ON container_table
FOR EACH ROW EXECUTE FUNCTION validate_contract_container_limit();
