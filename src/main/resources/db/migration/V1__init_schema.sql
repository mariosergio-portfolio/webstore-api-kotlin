-- V1__init_schema.sql
-- Web Store initial schema

CREATE TABLE customer (
    id            UUID         PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    full_name     VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT true,
    created_at    TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE customer_address (
    id          UUID         PRIMARY KEY,
    customer_id UUID         NOT NULL REFERENCES customer(id),
    street      VARCHAR(255) NOT NULL,
    city        VARCHAR(100) NOT NULL,
    state       VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20)  NOT NULL,
    country     VARCHAR(100) NOT NULL,
    is_default  BOOLEAN      NOT NULL DEFAULT false
);

CREATE TABLE category (
    id         UUID         PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    slug       VARCHAR(255) NOT NULL UNIQUE,
    parent_id  UUID         REFERENCES category(id),
    sort_order INT          NOT NULL DEFAULT 0
);

CREATE TABLE product (
    id             UUID           PRIMARY KEY,
    sku            VARCHAR(100)   NOT NULL UNIQUE,
    name           VARCHAR(255)   NOT NULL,
    description    TEXT,
    price          NUMERIC(19,4)  NOT NULL CHECK (price > 0),
    currency       CHAR(3)        NOT NULL DEFAULT 'USD',
    stock_quantity INT            NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    version        BIGINT         NOT NULL DEFAULT 0,
    category_id    UUID           REFERENCES category(id),
    status         VARCHAR(20)    NOT NULL CHECK (status IN ('ACTIVE','DRAFT','ARCHIVED')),
    reorder_point  INT            NOT NULL DEFAULT 5,
    created_at     TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE TABLE product_image (
    id         UUID         PRIMARY KEY,
    product_id UUID         NOT NULL REFERENCES product(id),
    url        VARCHAR(500) NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0
);

CREATE TABLE shipping_method (
    id             UUID          PRIMARY KEY,
    name           VARCHAR(100)  NOT NULL,
    cost           NUMERIC(19,4) NOT NULL CHECK (cost >= 0),
    currency       CHAR(3)       NOT NULL DEFAULT 'USD',
    estimated_days INT           NOT NULL,
    active         BOOLEAN       NOT NULL DEFAULT true
);

CREATE TABLE coupon (
    id               UUID          PRIMARY KEY,
    code             VARCHAR(50)   NOT NULL UNIQUE,
    discount_type    VARCHAR(20)   NOT NULL CHECK (discount_type IN ('PERCENTAGE','FIXED_AMOUNT')),
    value            NUMERIC(19,4) NOT NULL CHECK (value > 0),
    min_order_amount NUMERIC(19,4),
    max_uses         INT,
    used_count       INT           NOT NULL DEFAULT 0,
    expires_at       TIMESTAMP,
    active           BOOLEAN       NOT NULL DEFAULT true
);

CREATE TABLE cart (
    id          UUID         PRIMARY KEY,
    customer_id UUID         UNIQUE REFERENCES customer(id),
    session_id  VARCHAR(128) UNIQUE,
    coupon_id   UUID         REFERENCES coupon(id),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT cart_identity CHECK (
        (customer_id IS NOT NULL AND session_id IS NULL) OR
        (customer_id IS NULL     AND session_id IS NOT NULL)
    )
);

CREATE TABLE cart_item (
    id                  UUID          PRIMARY KEY,
    cart_id             UUID          NOT NULL REFERENCES cart(id),
    product_id          UUID          NOT NULL REFERENCES product(id),
    quantity            INT           NOT NULL CHECK (quantity >= 1),
    unit_price          NUMERIC(19,4) NOT NULL,
    unit_price_currency CHAR(3)       NOT NULL DEFAULT 'USD',
    UNIQUE (cart_id, product_id)
);

CREATE TABLE "order" (
    id                   UUID          PRIMARY KEY,
    customer_id          UUID          NOT NULL REFERENCES customer(id),
    status               VARCHAR(20)   NOT NULL CHECK (status IN ('PENDING','PAID','PROCESSING','SHIPPED','DELIVERED','CANCELLED')),
    subtotal             NUMERIC(19,4) NOT NULL,
    discount_amount      NUMERIC(19,4) NOT NULL DEFAULT 0,
    shipping_cost        NUMERIC(19,4) NOT NULL DEFAULT 0,
    total                NUMERIC(19,4) NOT NULL,
    currency             CHAR(3)       NOT NULL DEFAULT 'USD',
    shipping_street      VARCHAR(255)  NOT NULL,
    shipping_city        VARCHAR(100)  NOT NULL,
    shipping_state       VARCHAR(100)  NOT NULL,
    shipping_postal_code VARCHAR(20)   NOT NULL,
    shipping_country     VARCHAR(100)  NOT NULL,
    billing_street       VARCHAR(255),
    billing_city         VARCHAR(100),
    billing_state        VARCHAR(100),
    billing_postal_code  VARCHAR(20),
    billing_country      VARCHAR(100),
    shipping_method_id   UUID          REFERENCES shipping_method(id),
    tracking_carrier     VARCHAR(100),
    tracking_number      VARCHAR(100),
    payment_id           UUID,
    placed_at            TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at           TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE order_item (
    id                  UUID          PRIMARY KEY,
    order_id            UUID          NOT NULL REFERENCES "order"(id),
    product_id          UUID          NOT NULL REFERENCES product(id),
    sku                 VARCHAR(100)  NOT NULL,
    name                VARCHAR(255)  NOT NULL,
    quantity            INT           NOT NULL CHECK (quantity >= 1),
    unit_price          NUMERIC(19,4) NOT NULL,
    unit_price_currency CHAR(3)       NOT NULL DEFAULT 'USD',
    line_total          NUMERIC(19,4) NOT NULL
);

CREATE TABLE payment (
    id                UUID          PRIMARY KEY,
    order_id          UUID          NOT NULL UNIQUE REFERENCES "order"(id),
    gateway           VARCHAR(50)   NOT NULL,
    gateway_reference VARCHAR(255)  UNIQUE,
    idempotency_key   UUID          NOT NULL UNIQUE,
    amount            NUMERIC(19,4) NOT NULL,
    currency          CHAR(3)       NOT NULL,
    status            VARCHAR(20)   NOT NULL CHECK (status IN ('PENDING','SUCCEEDED','FAILED','REFUNDED')),
    created_at        TIMESTAMP     NOT NULL DEFAULT now(),
    updated_at        TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE inventory_audit_log (
    id          UUID         PRIMARY KEY,
    product_id  UUID         NOT NULL REFERENCES product(id),
    change_type VARCHAR(20)  NOT NULL CHECK (change_type IN ('DEDUCTION','RESTORATION','ADJUSTMENT')),
    delta       INT          NOT NULL,
    reason      VARCHAR(255),
    actor       VARCHAR(255),
    order_id    UUID         REFERENCES "order"(id),
    created_at  TIMESTAMP    NOT NULL DEFAULT now()
);

-- Indexes
CREATE INDEX idx_product_category   ON product(category_id);
CREATE INDEX idx_product_status     ON product(status);
CREATE INDEX idx_cart_item_cart     ON cart_item(cart_id);
CREATE INDEX idx_order_customer     ON "order"(customer_id);
CREATE INDEX idx_order_status       ON "order"(status);
CREATE INDEX idx_order_placed_at    ON "order"(placed_at);
CREATE INDEX idx_order_item_order   ON order_item(order_id);
CREATE INDEX idx_payment_order      ON payment(order_id);
CREATE INDEX idx_inv_audit_product  ON inventory_audit_log(product_id);
CREATE INDEX idx_inv_audit_order    ON inventory_audit_log(order_id);

-- Seed: default shipping methods
INSERT INTO shipping_method (id, name, cost, currency, estimated_days, active) VALUES
    ('00000000-0000-0000-0000-000000000001', 'Standard Shipping', 5.99, 'USD', 7,  true),
    ('00000000-0000-0000-0000-000000000002', 'Express Shipping',  14.99, 'USD', 2, true),
    ('00000000-0000-0000-0000-000000000003', 'Free Shipping',      0.00, 'USD', 14, true);
