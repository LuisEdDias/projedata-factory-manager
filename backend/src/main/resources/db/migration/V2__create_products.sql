CREATE TABLE products
(
    id    BIGSERIAL PRIMARY KEY,

    code  VARCHAR(50)    NOT NULL,
    name  VARCHAR(255)   NOT NULL,
    price NUMERIC(14, 2) NOT NULL,

    CONSTRAINT uk_products_code UNIQUE (code),

    CONSTRAINT ck_products_code_not_blank CHECK (btrim(code) <> ''),
    CONSTRAINT ck_products_name_not_blank CHECK (btrim(name) <> ''),
    CONSTRAINT ck_products_price_positive CHECK (price > 0)
);