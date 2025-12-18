-- SQL Script để tạo 2 admin mới (PostgreSQL)
-- Email: admin2@gmail.com và admin3@gmail.com
-- Password: 123456 (đã được hash bằng BCrypt)

-- Admin 2
INSERT INTO admins (user_id, full_name, email, password, phone_number, address, role, is_banned, created_date)
VALUES (
    gen_random_uuid()::text,
    'Admin 2',
    'admin2@gmail.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- BCrypt hash của "123456"
    '0912345678',
    'Địa chỉ Admin 2',
    'ADMIN',
    false,
    CURRENT_TIMESTAMP
);

-- Admin 3
INSERT INTO admins (user_id, full_name, email, password, phone_number, address, role, is_banned, created_date)
VALUES (
    gen_random_uuid()::text,
    'Admin 3',
    'admin3@gmail.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- BCrypt hash của "123456"
    '0912345679',
    'Địa chỉ Admin 3',
    'ADMIN',
    false,
    CURRENT_TIMESTAMP
);

-- Kiểm tra kết quả
SELECT user_id, full_name, email, role, created_date 
FROM admins 
WHERE email IN ('admin2@gmail.com', 'admin3@gmail.com');
