-- Insert demo users for testing/demo purposes
-- Password for all users: demo123 (BCrypt hashed)

-- Admin user
INSERT INTO users (id, name, email, password, role, is_active, join_date, version)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'Admin Demo',
    'admin@growup.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdum7CC2', -- demo123
    'ADMIN',
    true,
    CURRENT_TIMESTAMP,
    0
);

-- Student user 1
INSERT INTO users (id, name, email, password, role, is_active, join_date, version)
VALUES (
    'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22',
    'Juan Perez',
    'student@growup.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdum7CC2', -- demo123
    'STUDENT',
    true,
    CURRENT_TIMESTAMP,
    0
);

-- Student user 2
INSERT INTO users (id, name, email, password, role, is_active, join_date, version)
VALUES (
    'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33',
    'Maria Lopez',
    'maria@growup.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdum7CC2', -- demo123
    'STUDENT',
    true,
    CURRENT_TIMESTAMP,
    0
);
