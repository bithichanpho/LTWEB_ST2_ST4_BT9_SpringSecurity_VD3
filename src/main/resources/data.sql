-- ============================================================
-- Seed du lieu toi thieu: 2 vai tro ROLE_USER / ROLE_ADMIN
-- (chay sau khi Hibernate tao bang, an toan khi chay lai nhieu lan)
-- ============================================================
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER')
    INSERT INTO roles (name) VALUES ('ROLE_USER');

IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN')
    INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
