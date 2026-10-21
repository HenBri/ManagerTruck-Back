-- Semillas de catálogo para instalación nueva. Seguro de ejecutar varias veces.
-- No crea usuarios ni asigna SUPER_ADMIN: esa asignación requiere aprovisionamiento auditado.
INSERT INTO role_table (role_name, role_scope, role_description) VALUES
    ('SUPER_ADMIN', 'PLATFORM', 'Control global del SaaS; uso exclusivo del administrador de la plataforma'),
    ('SECRETARY', 'COMPANY', 'Gestion operativa y administrativa de su empresa'),
    ('PARTNER', 'COMPANY', 'Consulta de sus camiones y viajes autorizados'),
    ('DRIVER', 'COMPANY', 'Consulta y actualizacion permitida de sus viajes')
ON CONFLICT (role_name) DO UPDATE
SET role_scope = EXCLUDED.role_scope,
    role_description = EXCLUDED.role_description;
