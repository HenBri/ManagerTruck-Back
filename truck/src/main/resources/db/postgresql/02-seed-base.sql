-- Roles base del esquema Truckv3.
INSERT INTO role_table (role_id, role_name, role_description, state, is_delete, date_create)
VALUES
    (1, 'ADMIN', 'Administración completa del sistema', 'ACTIVE', false, CURRENT_TIMESTAMP),
    (2, 'SECRETARY', 'Gestión operativa y administrativa', 'ACTIVE', false, CURRENT_TIMESTAMP),
    (3, 'PARTNER', 'Consulta de camiones y viajes autorizados', 'ACTIVE', false, CURRENT_TIMESTAMP),
    (4, 'DRIVER', 'Consulta y actualización de viajes propios', 'ACTIVE', false, CURRENT_TIMESTAMP)
    ON CONFLICT (role_name) DO UPDATE
                                   SET role_description = EXCLUDED.role_description,
                                   state = 'ACTIVE',
                                   date_update = CURRENT_TIMESTAMP;