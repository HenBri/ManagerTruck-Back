-- Cuentas demo para el esquema actual.
-- Contraseña de todas las cuentas: kentrukers
BEGIN;

INSERT INTO person (first_name, last_name, email, state)
SELECT 'Super', 'Admin', 'superadmin@kentrukers.com', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM person WHERE lower(email) = lower('superadmin@kentrukers.com') AND is_delete = false);

INSERT INTO person (first_name, last_name, email, state)
SELECT 'Secretaria', 'KenTrukers', 'secretaria@kentrukers.com', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM person WHERE lower(email) = lower('secretaria@kentrukers.com') AND is_delete = false);

INSERT INTO person (first_name, last_name, email, state)
SELECT 'Socio', 'KenTrukers', 'socio@kentrukers.com', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM person WHERE lower(email) = lower('socio@kentrukers.com') AND is_delete = false);

INSERT INTO person (first_name, last_name, email, state)
SELECT 'Conductor', 'KenTrukers', 'conductor@kentrukers.com', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM person WHERE lower(email) = lower('conductor@kentrukers.com') AND is_delete = false);

-- BCrypt de: kentrukers
INSERT INTO user_table (person_id, password_hash, state)
SELECT p.person_id, '$2a$10$f08iselxXvYKiVH79DpfPed3duDxCwHqCaPflDcrtZ4giQoQ39FHi', 'ACTIVE'
FROM person p
WHERE lower(p.email) IN (
    'superadmin@kentrukers.com', 'secretaria@kentrukers.com',
    'socio@kentrukers.com', 'conductor@kentrukers.com'
)
ON CONFLICT (person_id) DO UPDATE
SET password_hash = EXCLUDED.password_hash, state = 'ACTIVE', is_delete = false;

INSERT INTO user_role (user_id, role_id, state)
SELECT u.user_id, r.role_id, 'ACTIVE'
FROM user_table u
JOIN person p ON p.person_id = u.person_id
JOIN role_table r ON r.role_name = CASE lower(p.email)
    WHEN 'superadmin@kentrukers.com' THEN 'ADMIN'
    WHEN 'secretaria@kentrukers.com' THEN 'SECRETARY'
    WHEN 'socio@kentrukers.com' THEN 'PARTNER'
    WHEN 'conductor@kentrukers.com' THEN 'DRIVER'
END
WHERE lower(p.email) IN (
    'superadmin@kentrukers.com', 'secretaria@kentrukers.com',
    'socio@kentrukers.com', 'conductor@kentrukers.com'
)
ON CONFLICT (user_id, role_id) DO UPDATE SET state = 'ACTIVE', is_delete = false;

COMMIT;

-- superadmin@kentrukers.com / kentrukers -> ADMIN
-- secretaria@kentrukers.com / kentrukers -> SECRETARY
-- socio@kentrukers.com / kentrukers -> PARTNER
-- conductor@kentrukers.com / kentrukers -> DRIVER
