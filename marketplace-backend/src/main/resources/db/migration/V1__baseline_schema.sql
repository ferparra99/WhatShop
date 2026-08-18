-- ============================================================
--  WhatShop Marketplace — V1: Esquema base (Flyway)
--  Reemplaza init.sql: a partir de ahora Flyway gestiona el schema.
--  Compatible con: PostgreSQL 15+
--  Las BD existentes creadas con init.sql se tratan como "ya en V1"
--  (baseline-on-migrate + baseline-version=1).
-- ============================================================

-- ============================================================
--  EXTENSIONES
-- ============================================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- para gen_random_uuid()

-- ============================================================
--  TABLA: users
--  Entidad: com.whatshop.marketplace.auth.entity.User
--  Roles:   ROLE_BUYER | ROLE_SELLER | ROLE_ADMIN
-- ============================================================
CREATE TABLE users (
    id          UUID          NOT NULL DEFAULT gen_random_uuid(),
    email       VARCHAR(255)  NOT NULL,
    password    VARCHAR(255)  NOT NULL,        -- bcrypt hash
    full_name   VARCHAR(255)  NOT NULL,
    phone       VARCHAR(15),
    role        VARCHAR(20)   NOT NULL,        -- enum: ROLE_BUYER | ROLE_SELLER | ROLE_ADMIN
    enabled     BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP     NOT NULL,
    updated_at  TIMESTAMP     NOT NULL,

    CONSTRAINT pk_users        PRIMARY KEY (id),
    CONSTRAINT uq_users_email  UNIQUE (email),
    CONSTRAINT ck_users_role   CHECK (role IN ('ROLE_BUYER', 'ROLE_SELLER', 'ROLE_ADMIN'))
);

COMMENT ON TABLE  users            IS 'Usuarios del sistema: compradores, vendedores y administradores';
COMMENT ON COLUMN users.password   IS 'Hash BCrypt — nunca almacenar en texto plano';
COMMENT ON COLUMN users.role       IS 'Rol único por usuario — determina permisos en Spring Security';
COMMENT ON COLUMN users.enabled    IS 'false = cuenta deshabilitada, no puede hacer login';

-- ============================================================
--  TABLA: sellers
--  Entidad: com.whatshop.marketplace.sellers.entity.Seller
--  Relación: OneToOne con users (user_id)
-- ============================================================
CREATE TABLE sellers (
    id          UUID          NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL,        -- FK → users.id (OneToOne)
    store_name  VARCHAR(255)  NOT NULL,
    description TEXT,
    nit         VARCHAR(255),
    logo_url    VARCHAR(255),
    rating      DOUBLE PRECISION  NOT NULL DEFAULT 0.0,
    total_sales INTEGER           NOT NULL DEFAULT 0,
    active      BOOLEAN           NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP         NOT NULL,

    CONSTRAINT pk_sellers           PRIMARY KEY (id),
    CONSTRAINT uq_sellers_user_id   UNIQUE (user_id),
    CONSTRAINT uq_sellers_storename UNIQUE (store_name),
    CONSTRAINT fk_sellers_user      FOREIGN KEY (user_id)
                                    REFERENCES users (id)
                                    ON DELETE CASCADE,
    CONSTRAINT ck_sellers_rating    CHECK (rating >= 0.0 AND rating <= 5.0),
    CONSTRAINT ck_sellers_sales     CHECK (total_sales >= 0)
);

COMMENT ON TABLE  sellers             IS 'Perfil extendido de vendedores — solo usuarios con ROLE_SELLER';
COMMENT ON COLUMN sellers.user_id     IS 'Relación OneToOne con users — un user solo puede tener un seller';
COMMENT ON COLUMN sellers.nit         IS 'NIT o RUT del vendedor — opcional, para facturación';
COMMENT ON COLUMN sellers.rating      IS 'Promedio de calificaciones 0.0–5.0';
COMMENT ON COLUMN sellers.total_sales IS 'Contador acumulado de ventas completadas';
COMMENT ON COLUMN sellers.active      IS 'false = tienda pausada, no aparece en el catálogo';

-- ============================================================
--  TABLA: categories
--  Entidad: com.whatshop.marketplace.products.entity.Category
-- ============================================================
CREATE TABLE categories (
    id        UUID          NOT NULL DEFAULT gen_random_uuid(),
    name      VARCHAR(255)  NOT NULL,
    slug      VARCHAR(255)  NOT NULL,
    image_url VARCHAR(255),

    CONSTRAINT pk_categories       PRIMARY KEY (id),
    CONSTRAINT uq_categories_name  UNIQUE (name),
    CONSTRAINT uq_categories_slug  UNIQUE (slug)
);

COMMENT ON TABLE  categories       IS 'Categorías del catálogo de productos';
COMMENT ON COLUMN categories.slug  IS 'URL amigable — ej: electronica, ropa-mujer. Solo minúsculas y guiones';

-- ============================================================
--  TABLA: products
--  Entidad: com.whatshop.marketplace.products.entity.Product
--  Status:  ACTIVE | PAUSED | DELETED (soft delete)
-- ============================================================
CREATE TABLE products (
    id          UUID             NOT NULL DEFAULT gen_random_uuid(),
    seller_id   UUID             NOT NULL,     -- FK → sellers.id (ManyToOne)
    category_id UUID,                          -- FK → categories.id (nullable)
    name        VARCHAR(255)     NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2)   NOT NULL,     -- BigDecimal(precision=10, scale=2)
    stock       INTEGER          NOT NULL,
    image_url   VARCHAR(255),
    status      VARCHAR(10)      NOT NULL DEFAULT 'ACTIVE',
    rating      DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at  TIMESTAMP        NOT NULL,
    updated_at  TIMESTAMP        NOT NULL,

    CONSTRAINT pk_products          PRIMARY KEY (id),
    CONSTRAINT fk_products_seller   FOREIGN KEY (seller_id)
                                    REFERENCES sellers (id)
                                    ON DELETE CASCADE,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
                                    REFERENCES categories (id)
                                    ON DELETE SET NULL,
    CONSTRAINT ck_products_price    CHECK (price >= 0),
    CONSTRAINT ck_products_stock    CHECK (stock >= 0),
    CONSTRAINT ck_products_status   CHECK (status IN ('ACTIVE', 'PAUSED', 'DELETED')),
    CONSTRAINT ck_products_rating   CHECK (rating >= 0.0 AND rating <= 5.0)
);

COMMENT ON TABLE  products             IS 'Catálogo de productos publicados por vendedores';
COMMENT ON COLUMN products.price       IS 'Precio en COP — NUMERIC(10,2) para evitar errores de punto flotante';
COMMENT ON COLUMN products.status      IS 'ACTIVE=visible, PAUSED=oculto, DELETED=soft delete (no borrar físicamente)';
COMMENT ON COLUMN products.category_id IS 'Nullable — producto sin categoría asignada es válido';

-- ============================================================
--  ÍNDICES — mejoran performance en queries frecuentes
-- ============================================================

-- Búsqueda de productos por vendedor (panel del seller)
CREATE INDEX idx_products_seller_id   ON products (seller_id);

-- Filtro por categoría en el catálogo
CREATE INDEX idx_products_category_id ON products (category_id);

-- Filtro por estado (ACTIVE es el más consultado)
CREATE INDEX idx_products_status      ON products (status);

-- Búsqueda por nombre (ILIKE '%texto%')
CREATE INDEX idx_products_name        ON products USING gin (to_tsvector('spanish', name));

-- Login: búsqueda por email (el más frecuente en auth)
CREATE INDEX idx_users_email          ON users (email);

-- Filtro de sellers activos
CREATE INDEX idx_sellers_active       ON sellers (active);

-- ============================================================
--  DATOS INICIALES — categorías del marketplace
-- ============================================================
INSERT INTO categories (id, name, slug, image_url) VALUES
    (gen_random_uuid(), 'Electrónica',  'electronica',  NULL),
    (gen_random_uuid(), 'Ropa',         'ropa',         NULL),
    (gen_random_uuid(), 'Hogar',        'hogar',        NULL),
    (gen_random_uuid(), 'Alimentos',    'alimentos',    NULL),
    (gen_random_uuid(), 'Deportes',     'deportes',     NULL),
    (gen_random_uuid(), 'Libros',       'libros',       NULL),
    (gen_random_uuid(), 'Juguetes',     'juguetes',     NULL),
    (gen_random_uuid(), 'Salud',        'salud',        NULL);

-- ============================================================
--  USUARIO ADMIN INICIAL
--  Password: Admin1234! (hash BCrypt generado con strength=10)
--  ⚠️  CAMBIAR en producción — solo para desarrollo local
-- ============================================================
INSERT INTO users (id, email, password, full_name, phone, role, enabled, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'admin@whatshop.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Administrador WhatShop',
    '+573000000000',
    'ROLE_ADMIN',
    TRUE,
    NOW(),
    NOW()
);