-- ============================================================
--  SpringBootSecurityVD3 - SCRIPT TAO DATABASE + DU LIEU MAU
--  Du an: Spring Boot 4.1.1 + Spring Security 7 + Thymeleaf
--         + MapStruct + Cloudinary + SQL Server
--
--  Cach dung (SQL Server Management Studio - SSMS):
--    1. Mo file nay bang SSMS, bam Execute (F5).
--    2. Chay ung dung Spring Boot (mvnw spring-boot:run).
--    3. Dang nhap: admin / 123456  (ADMIN) hoac trang / 123456 (USER).
--
--  Script co the chay lai nhieu lan (da co kiem tra ton tai).
-- ============================================================

USE master;
GO

IF DB_ID(N'SpringBootSecurityVD3') IS NULL
    CREATE DATABASE SpringBootSecurityVD3;
GO

USE SpringBootSecurityVD3;
GO

-- ============================================================
-- 1. TAO BANG (bo qua neu da ton tai)
-- ============================================================

IF OBJECT_ID(N'dbo.roles', N'U') IS NULL
CREATE TABLE dbo.roles (
    role_id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_roles PRIMARY KEY,
    name VARCHAR(30) NOT NULL
);
GO
IF OBJECT_ID(N'dbo.roles', N'U') IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'uk_roles_name' AND object_id = OBJECT_ID(N'dbo.roles'))
    CREATE UNIQUE INDEX uk_roles_name ON dbo.roles(name);
GO

IF OBJECT_ID(N'dbo.users', N'U') IS NULL
CREATE TABLE dbo.users (
    user_id    BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_users PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(150) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    full_name  NVARCHAR(255) NULL,
    enabled    BIT NOT NULL CONSTRAINT df_users_enabled DEFAULT 0,
    role_id    BIGINT NOT NULL CONSTRAINT fk_users_role REFERENCES dbo.roles(role_id),
    created_at DATETIME2 NULL
);
GO
IF OBJECT_ID(N'dbo.users', N'U') IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'uk_users_username' AND object_id = OBJECT_ID(N'dbo.users'))
    CREATE UNIQUE INDEX uk_users_username ON dbo.users(username);
GO
IF OBJECT_ID(N'dbo.users', N'U') IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'uk_users_email' AND object_id = OBJECT_ID(N'dbo.users'))
    CREATE UNIQUE INDEX uk_users_email ON dbo.users(email);
GO

IF OBJECT_ID(N'dbo.otp_tokens', N'U') IS NULL
CREATE TABLE dbo.otp_tokens (
    id         BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT pk_otp_tokens PRIMARY KEY,
    email      VARCHAR(150) NOT NULL,
    otp_hash   VARCHAR(100) NOT NULL,
    type       VARCHAR(30)  NOT NULL,
    expires_at DATETIME2 NOT NULL,
    attempts   INT NOT NULL,
    used       BIT NOT NULL CONSTRAINT df_otp_used DEFAULT 0,
    created_at DATETIME2 NOT NULL
);
GO
IF OBJECT_ID(N'dbo.otp_tokens', N'U') IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_otp_email_type' AND object_id = OBJECT_ID(N'dbo.otp_tokens'))
    CREATE INDEX idx_otp_email_type ON dbo.otp_tokens(email, type);
GO

IF OBJECT_ID(N'dbo.products', N'U') IS NULL
CREATE TABLE dbo.products (
    product_id   VARCHAR(10) NOT NULL CONSTRAINT pk_products PRIMARY KEY,
    product_name NVARCHAR(255) NULL,
    price        FLOAT NOT NULL,
    description  NVARCHAR(4000) NULL,
    images       NVARCHAR(1000) NULL,
    created_at   DATETIME2 NULL,
    user_id      BIGINT NULL CONSTRAINT fk_products_user REFERENCES dbo.users(user_id)
);
GO
IF OBJECT_ID(N'dbo.products', N'U') IS NOT NULL
   AND NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_products_name' AND object_id = OBJECT_ID(N'dbo.products'))
    CREATE INDEX idx_products_name ON dbo.products(product_name);
GO

-- ============================================================
-- 2. DU LIEU MAU
-- ============================================================

-- ---------- ROLES ----------
IF NOT EXISTS (SELECT 1 FROM dbo.roles)
BEGIN
    SET IDENTITY_INSERT dbo.roles ON;
    INSERT INTO dbo.roles (role_id, name) VALUES (1, 'ROLE_USER');
    INSERT INTO dbo.roles (role_id, name) VALUES (2, 'ROLE_ADMIN');
    SET IDENTITY_INSERT dbo.roles OFF;
END
GO

-- ---------- USERS ----------
IF NOT EXISTS (SELECT 1 FROM dbo.users)
BEGIN
    SET IDENTITY_INSERT dbo.users ON;
INSERT INTO dbo.users (user_id, username, email, password, full_name, enabled, role_id, created_at) VALUES (1, 'admin', 'admin@shop.com', '$2a$10$UFOAtfiexfEeZbHbtKi19.mRStzz4CkBQS1LXPiiPEi3tlbrqPfvC', N'Quan tri vien', 1, 2, '2026-09-12 19:48:27');
INSERT INTO dbo.users (user_id, username, email, password, full_name, enabled, role_id, created_at) VALUES (2, 'trang', 'tranthiphuongtrang2711@gmail.com', '$2a$10$UFOAtfiexfEeZbHbtKi19.mRStzz4CkBQS1LXPiiPEi3tlbrqPfvC', N'Phuong Trang', 1, 1, '2026-09-13 09:00:24');
    SET IDENTITY_INSERT dbo.users OFF;
END
GO

-- ---------- PRODUCTS (1 user - n product: SP01-SP08 thuoc admin, SP09-SP17 thuoc trang) ----------
IF NOT EXISTS (SELECT 1 FROM dbo.products)
BEGIN
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP01', N'Ao thun soc 3 duong - xanh duong', 400000.00, N'CHAT LIEU 64% COTTON 36% TENCEL DINH LUONG 180GSM

FORM BOXY

HINH IN LOGO KEO LUA TRUOC NGUC', 'products/1788242562163_triple_striped_tee.png', '2026-09-12 18:33:41', 1);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP02', N'Ao thun soc 3 duong - hong', 400000.00, N'CHAT LIEU 64% COTTON 36% TENCEL DINH LUONG 180GSM

FORM BOXY

HINH IN LOGO KEO LUA TRUOC NGUC', 'products/1788242802583_pink_triple_striped_tee.png', '2026-09-12 18:33:41', 1);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP03', N'Ao thun form regular hinh trai tim', 400000.00, N'CHAT LIEU COTTON XUOC DINH LUONG 170GSM

FORM REGULAR FIT

HINH IN KEO LUA TRUOC NGUC', 'products/1788242888567_regularfit01.png', '2026-09-12 18:33:41', 1);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP04', N'Vi da deo day - den', 300000.00, N'CHAT LIEU DA BO

LOGO KIM LOAI

DAY DEO CO THE THAO DOI

NAP GAI NAM CHAM

2 NGAN DUNG

KICH THUOC: CAO 8CM x NGANG 11CM', 'products/1788242993835_leather_wallet.png', '2026-09-12 18:33:41', 1);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP05', N'Tui tote da canvas truyen thong', 400000.00, N'CHAT LIEU DA BO PHOI CANVAS

LOGO DAP CHIM, 1 TUI AN BEN TRONG

KICH THUOC (NGANG,CAO,RONG): 47cm x 33cm x 5cm', 'products/1788243055298_totebag.png', '2026-09-12 18:33:41', 1);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP06', N'Quan jean ong dung nut kim loai - xanh nhat', 2000000.00, N'CHAT LIEU DENIM 13.5Oz

FORM RELAXED STRAIGHT

HAI TUI TRUOC, HAI TUI SAU

HANG NUT KIM LOAI KHAC LOGO DONG GIA KHUY, CHI TIET NUT DONG KHAC LOGO

TAG DA MAY LUNG QUAN', 'products/1788243139039_jeans.png', '2026-09-12 18:33:41', 1);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP07', N'Ao hoodie logo rua mau - khoa keo', 2000000.00, N'CHAT LIEU NI BONG COTTON 490GSM DA QUA XU LY WASH BAC MAU.

FORM BOXY

AO DUOC DON VAI BEN TRONG, CHU DAP VAI NOI VOI HIEU UNG TUA VIEN VA DUONG CHI TUONG PHAN, MANG CAM HUNG VINTAGE, ZIP YKK 2 DAU CHINH HANG, 2 TUI NGOAI, 1 TUI AN BEN TRONG AO.', 'products/1788243287656_hoodie_zip.png', '2026-09-12 18:33:41', 1);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP08', N'Ao thun raglan tay dai - vang', 500000.00, N'CHAT LIEU: COTTON 2 CHIEU 170GSM MONG NHE PHU HOP VOI MUA HE

FORM RAGLAN, DETAIL NUT GAI O SUON AO

HINH IN KEO LUA THAM VAI TRUOC NGUC VA SAU LUNG', 'products/1788243415932_orange_raglan.png', '2026-09-12 18:33:41', 1);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP09', N'Ao khoac nylon - trang', 1000000.00, N'CHAT LIEU POLYESTER

FORM BOXY FIT

HINH IN KEO LUA TRUOC NGUC VA SAU LUNG, NUT BAM KIM LOAI KHAC LOGO, KHOA ZIP 2 DAU', 'products/1788243546966_nylon_jacket.png', '2026-09-12 18:33:41', 2);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP10', N'Quan jean ong loe rua da', 1000000.00, N'DENIM 100% COTTON - 13.5 Oz

FORM FLARE FIT

NUT KIM LOAI KHAC LOGO

TAG DA MAY LUNG QUAN, LOGO THEU TUI SAU', 'products/1788243696588_flare.png', '2026-09-12 18:33:41', 2);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP11', N'Ao thun logo nut vo - xanh dam', 450000.00, N'100% COTTON 2 CHIEU 300GSM DA XU LY CO RUT

FORM BOXY

LOGO IN KEO LUA TRUOC NGUC, HIEU UNG MAI RACH O THAN TRUOC.', 'products/1788243790881_blue_tee.png', '2026-09-12 18:33:41', 2);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP12', N'Ao thun form body - xam', 400000.00, N'CHAT LIEU: 100% COTTON 4 CHIEU

FORM BODY FIT.

LOGO ALFAER CLUB CAO THANH UI NHIET TRUOC NGUC.', 'products/1788243995893_grey_fit.png', '2026-09-12 18:33:41', 2);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP13', N'Quan short jean ong rong mau coral', 900000.00, N'DENIM 100% COTTON - 14 Oz DA XU LY WASH VAI VA XIT HIEU UNG

FORM BAGGY SHORT

HAI TUI TRUOC, HAI TUI SAU

HANG NUT KIM LOAI KHAC LOGO DONG GIA KHUY, 6 NUT RIVET KHAC LOGO TUI TRUOC, 4 NUT RIVET KHAC LOGO O TUI SAU

TAG DA MAY LUNG QUAN', 'products/1788244084789_short.png', '2026-09-12 18:33:41', 2);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP14', N'Ao thun raglan waffle dai tay - xam nhat', 800000.00, N'CHAT LIEU VAI COTTON TO ONG 360GSM

FORM RAGLAN BOXY

HINH IN GRAPHIC KEO LUA TRUOC NGUC, HINH THEU LOGO 3D SAU AO', 'products/1788244199840_waffle_longsleeve.png', '2026-09-12 18:33:41', 2);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP15', N'Mu luoi trai den rua mau', 300000.00, N'VAI KHAKI 100% COTTON HIEU UNG WASH XAO DA.

LOGO THEU 3D MAT TRUOC.

TANG DO TANG GIAM DO RONG PHIA SAU NON.', 'products/1788244314014_cap.png', '2026-09-12 18:33:41', 2);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP16', N'Sandal da Rima', 700000.00, N'CHIEU CAO DE: 2CM

CHAT LIEU: DA BO DAP VAN RAN, DE CAO SU.', 'products/1788244396888_slipper.png', '2026-09-12 18:33:41', 2);
INSERT INTO dbo.products (product_id, product_name, price, description, images, created_at, user_id) VALUES ('SP17', N'Khan bandana', 200000.00, N'KICH THUOC: 65X65CM

CHAT LIEU: LINEN CAO CAP

HINH IN NUOC THAM VAI', 'products/1788949164620_bandana.png', '2026-09-12 18:33:41', 2);
END
GO

PRINT N'===== HOAN TAT: database SpringBootSecurityVD3 da san sang (admin/123456, trang/123456) =====';
GO
