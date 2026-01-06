-- Insert default roles
INSERT INTO roles (name, description) VALUES
    ('ROLE_ADMIN', 'Administrator with full system access'),
    ('ROLE_USER', 'Regular customer user'),
    ('ROLE_SUPPORT', 'Customer support representative'),
    ('ROLE_MERCHANT', 'Merchant or seller'),
    ('ROLE_GUEST', 'Guest user with limited access')
ON CONFLICT (name) DO NOTHING;

-- Insert default permissions
INSERT INTO permissions (name, description, resource, action) VALUES
    -- User permissions
    ('USER_READ', 'Read user information', 'USER', 'READ'),
    ('USER_CREATE', 'Create new users', 'USER', 'CREATE'),
    ('USER_UPDATE', 'Update user information', 'USER', 'UPDATE'),
    ('USER_DELETE', 'Delete users', 'USER', 'DELETE'),
    
    -- Order permissions
    ('ORDER_READ', 'View orders', 'ORDER', 'READ'),
    ('ORDER_CREATE', 'Create new orders', 'ORDER', 'CREATE'),
    ('ORDER_UPDATE', 'Update orders', 'ORDER', 'UPDATE'),
    ('ORDER_DELETE', 'Delete orders', 'ORDER', 'DELETE'),
    
    -- Report permissions
    ('REPORT_READ', 'View reports', 'REPORT', 'READ'),
    ('REPORT_CREATE', 'Generate reports', 'REPORT', 'CREATE'),
    
    -- Admin permissions
    ('SYSTEM_ADMIN', 'Full system administration', 'SYSTEM', 'ALL')
ON CONFLICT (name) DO NOTHING;

-- Assign permissions to roles
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN' AND p.name IN (
    'USER_READ', 'USER_CREATE', 'USER_UPDATE', 'USER_DELETE',
    'ORDER_READ', 'ORDER_CREATE', 'ORDER_UPDATE', 'ORDER_DELETE',
    'REPORT_READ', 'REPORT_CREATE', 'SYSTEM_ADMIN'
)
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_USER' AND p.name IN (
    'USER_READ', 'ORDER_READ', 'ORDER_CREATE'
)
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_SUPPORT' AND p.name IN (
    'USER_READ', 'USER_UPDATE', 'ORDER_READ', 'ORDER_UPDATE'
)
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_MERCHANT' AND p.name IN (
    'ORDER_READ', 'REPORT_READ'
)
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_GUEST' AND p.name IN (
    'ORDER_READ'
)
ON CONFLICT DO NOTHING;

-- Create default admin user
-- Password: Admin@123 (hashed with BCrypt)
INSERT INTO users (id, username, email, password, first_name, last_name, enabled, created_at)
VALUES (
    gen_random_uuid(),
    'admin',
    'admin@ecommerce.com',
    '$2a$10$XvLVvP7u4y9C8mGqL5YlDO6rOqWY.4T7p7HqVvLvL7hVXDmJ3qrBG',
    'System',
    'Administrator',
    true,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;






