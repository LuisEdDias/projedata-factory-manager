CREATE TABLE product_materials
(
    id                BIGSERIAL PRIMARY KEY,

    product_id        BIGINT         NOT NULL,
    raw_material_id   BIGINT         NOT NULL,
    quantity_required NUMERIC(14, 4) NOT NULL,

    CONSTRAINT fk_product_materials_product
        FOREIGN KEY (product_id)
            REFERENCES products (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_product_materials_raw_material
        FOREIGN KEY (raw_material_id)
            REFERENCES raw_materials (id),

    CONSTRAINT uk_product_materials_product_raw_material
        UNIQUE (product_id, raw_material_id),

    CONSTRAINT ck_product_materials_quantity_positive
        CHECK (quantity_required > 0)
);

CREATE INDEX ix_product_materials_raw_material_id ON product_materials (raw_material_id);