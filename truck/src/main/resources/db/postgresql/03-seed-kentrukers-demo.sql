-- Datos iniciales de demostración para KenTrukers.
-- Ejecutar después de 02-seed-base.sql sobre truck_sass.
-- Todas las cuentas usan contraseña: kentrukers (almacenada como BCrypt).
-- Cambiar estas credenciales después de la primera autenticación.
-- Este script inserta los usuarios y asigna roles; no crea ningún dato destructivo.

BEGIN;

DO $$
DECLARE
    v_company_id BIGINT;
    v_super_admin_id BIGINT;
    v_secretary_id BIGINT;
    v_partner_user_id BIGINT;
    v_driver_user_id BIGINT;
    v_super_admin_role_id SMALLINT;
    v_secretary_role_id SMALLINT;
    v_partner_role_id SMALLINT;
    v_driver_role_id SMALLINT;
    v_partner_id BIGINT;
BEGIN
    -- Obtener o crear el tenant una sola vez.
    SELECT company_id INTO v_company_id
    FROM company
    WHERE company_name = 'KenTrukers'
    ORDER BY company_id
    LIMIT 1;

    IF v_company_id IS NULL THEN
        INSERT INTO company (company_name, company_status)
        VALUES ('KenTrukers', 'ACTIVE')
        RETURNING company_id INTO v_company_id;
    END IF;

    -- Las cuatro cuentas usan BCrypt para contraseña `kentrukers`.
    INSERT INTO user_table (user_email, password_hash, user_name, user_lastname, user_status)
    VALUES ('superadmin@kentrukers.com', '$2a$10$f08iselxXvYKiVH79DpfPed3duDxCwHqCaPflDcrtZ4giQoQ39FHi', 'Super', 'Admin', 'ACTIVE')
    ON CONFLICT (user_email) DO UPDATE SET
        password_hash = EXCLUDED.password_hash,
        user_name = EXCLUDED.user_name,
        user_lastname = EXCLUDED.user_lastname,
        user_status = 'ACTIVE'
    RETURNING user_id INTO v_super_admin_id;

    INSERT INTO user_table (user_email, password_hash, user_name, user_lastname, user_status)
    VALUES ('secretaria@kentrukers.com', '$2a$10$f08iselxXvYKiVH79DpfPed3duDxCwHqCaPflDcrtZ4giQoQ39FHi', 'Secretaria', 'KenTrukers', 'ACTIVE')
    ON CONFLICT (user_email) DO UPDATE SET
        password_hash = EXCLUDED.password_hash,
        user_name = EXCLUDED.user_name,
        user_lastname = EXCLUDED.user_lastname,
        user_status = 'ACTIVE'
    RETURNING user_id INTO v_secretary_id;

    INSERT INTO user_table (user_email, password_hash, user_name, user_lastname, user_status)
    VALUES ('socio@kentrukers.com', '$2a$10$f08iselxXvYKiVH79DpfPed3duDxCwHqCaPflDcrtZ4giQoQ39FHi', 'Socio', 'KenTrukers', 'ACTIVE')
    ON CONFLICT (user_email) DO UPDATE SET
        password_hash = EXCLUDED.password_hash,
        user_name = EXCLUDED.user_name,
        user_lastname = EXCLUDED.user_lastname,
        user_status = 'ACTIVE'
    RETURNING user_id INTO v_partner_user_id;

    INSERT INTO user_table (user_email, password_hash, user_name, user_lastname, user_status)
    VALUES ('conductor@kentrukers.com', '$2a$10$f08iselxXvYKiVH79DpfPed3duDxCwHqCaPflDcrtZ4giQoQ39FHi', 'Conductor', 'KenTrukers', 'ACTIVE')
    ON CONFLICT (user_email) DO UPDATE SET
        password_hash = EXCLUDED.password_hash,
        user_name = EXCLUDED.user_name,
        user_lastname = EXCLUDED.user_lastname,
        user_status = 'ACTIVE'
    RETURNING user_id INTO v_driver_user_id;

    SELECT role_id INTO STRICT v_super_admin_role_id FROM role_table WHERE role_name = 'SUPER_ADMIN' AND role_scope = 'PLATFORM';
    SELECT role_id INTO STRICT v_secretary_role_id FROM role_table WHERE role_name = 'SECRETARY' AND role_scope = 'COMPANY';
    SELECT role_id INTO STRICT v_partner_role_id FROM role_table WHERE role_name = 'PARTNER' AND role_scope = 'COMPANY';
    SELECT role_id INTO STRICT v_driver_role_id FROM role_table WHERE role_name = 'DRIVER' AND role_scope = 'COMPANY';

    -- El SUPER_ADMIN es global: no se agrega como miembro de KenTrukers.
    INSERT INTO platform_user_role (user_id, role_id, role_scope)
    VALUES (v_super_admin_id, v_super_admin_role_id, 'PLATFORM')
    ON CONFLICT (user_id, role_id) DO NOTHING;

    -- Membresías activas para los roles operativos de la empresa.
    INSERT INTO company_user (company_id, user_id, membership_status)
    VALUES
        (v_company_id, v_secretary_id, 'ACTIVE'),
        (v_company_id, v_partner_user_id, 'ACTIVE'),
        (v_company_id, v_driver_user_id, 'ACTIVE')
    ON CONFLICT (company_id, user_id) DO UPDATE SET membership_status = 'ACTIVE';

    INSERT INTO company_user_role (company_id, user_id, role_id, role_scope)
    VALUES
        (v_company_id, v_secretary_id, v_secretary_role_id, 'COMPANY'),
        (v_company_id, v_partner_user_id, v_partner_role_id, 'COMPANY'),
        (v_company_id, v_driver_user_id, v_driver_role_id, 'COMPANY')
    ON CONFLICT (company_id, user_id, role_id) DO NOTHING;

    -- Perfil socio vinculado a su usuario y tenant.
    SELECT partner_id INTO v_partner_id
    FROM partner_table
    WHERE company_id = v_company_id AND user_id = v_partner_user_id
    ORDER BY partner_id
    LIMIT 1;

    IF v_partner_id IS NULL THEN
        INSERT INTO partner_table (company_id, user_id, partner_type, partner_name, partner_state)
        VALUES (v_company_id, v_partner_user_id, 'PERSON', 'Socio KenTrukers', 'ACTIVE')
        RETURNING partner_id INTO v_partner_id;
    ELSE
        UPDATE partner_table
        SET partner_type = 'PERSON', partner_name = 'Socio KenTrukers', partner_state = 'ACTIVE'
        WHERE partner_id = v_partner_id;
    END IF;

    -- Perfil conductor independiente del socio (person_partner_id queda NULL).
    INSERT INTO driver_table (company_id, user_id, driver_name, driver_license, driver_state)
    VALUES (v_company_id, v_driver_user_id, 'Conductor KenTrukers', 'KENTRUKERS-DEMO-001', 'ACTIVE')
    ON CONFLICT (company_id, user_id) DO UPDATE SET
        driver_name = EXCLUDED.driver_name,
        driver_license = EXCLUDED.driver_license,
        driver_state = 'ACTIVE';
END $$;

COMMIT;

-- Cuentas de acceso (las cuatro contraseñas son `kentrukers`):
-- superadmin@kentrukers.com  -> SUPER_ADMIN (PLATFORM)
-- secretaria@kentrukers.com -> SECRETARY (KenTrukers)
-- socio@kentrukers.com      -> PARTNER (KenTrukers, perfil partner)
-- conductor@kentrukers.com  -> DRIVER (KenTrukers, perfil driver)
