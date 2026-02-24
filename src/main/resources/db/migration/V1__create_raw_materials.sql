CREATE TABLE raw_materials
(
    id             BIGSERIAL PRIMARY KEY,

    code           VARCHAR(50)    NOT NULL,
    name           VARCHAR(255)   NOT NULL,
    stock_quantity NUMERIC(14, 4) NOT NULL,
    unit           VARCHAR(10)    NOT NULL,

    CONSTRAINT uk_raw_materials_code UNIQUE (code),

    CONSTRAINT ck_raw_materials_code_not_blank CHECK (btrim(code) <> ''),
    CONSTRAINT ck_raw_materials_name_not_blank CHECK (btrim(name) <> ''),
    CONSTRAINT ck_raw_materials_stock_non_negative CHECK (stock_quantity >= 0),
    CONSTRAINT ck_raw_materials_unit_valid CHECK (unit IN ('UN', 'KG', 'L', 'M', 'HR'))
);