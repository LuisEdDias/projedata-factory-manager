INSERT INTO raw_materials (code, name, stock_quantity, unit, unit_cost)
VALUES ('MTL-A01', 'Chapa de Aço Carbono 5mm', 1000.00, 'KG', 8.50),
       ('MTL-I02', 'Chapa de Aço Inox 304', 1200.00, 'KG', 35.00),
       ('MTL-AL3', 'Perfil de Alumínio Estrutural', 800.00, 'KG', 22.00),
       ('MTL-CB4', 'Bobina de Cobre Puro', 300.00, 'KG', 65.00);

INSERT INTO raw_materials (code, name, stock_quantity, unit, unit_cost)
VALUES ('QUI-T01', 'Tinta Industrial Epóxi Cinza', 50.00, 'L', 45.00),
       ('QUI-S02', 'Solvente PU', 150.00, 'L', 18.00),
       ('QUI-O03', 'Óleo Lubrificante de Máquina', 400.00, 'L', 25.00),
       ('QUI-R04', 'Resina Isolante Elétrica', 100.00, 'L', 85.00);

INSERT INTO raw_materials (code, name, stock_quantity, unit, unit_cost)
VALUES ('CAB-E01', 'Cabo Elétrico Flexível 4mm', 3000.00, 'M', 3.20),
       ('CAB-E02', 'Cabo de Dados Blindado', 1500.00, 'M', 5.50),
       ('CAB-A03', 'Cabo de Aço 8mm', 1000.00, 'M', 12.00);

INSERT INTO raw_materials (code, name, stock_quantity, unit, unit_cost)
VALUES ('PEC-M01', 'Motor Elétrico Trifásico 5CV', 50.00, 'UN', 1200.00),
       ('PEC-M02', 'Motor de Passo NEMA 23', 120.00, 'UN', 150.00),
       ('PEC-C03', 'Controlador Lógico Programável (CLP)', 40.00, 'UN', 3500.00),
       ('PEC-R04', 'Rolamento Esférico SKF', 500.00, 'UN', 45.00),
       ('PEC-S05', 'Sensor de Presença Indutivo', 200.00, 'UN', 85.00),
       ('PEC-B06', 'Botão de Emergência Cogumelo', 150.00, 'UN', 35.00),
       ('PEC-D07', 'Display IHM Touch 7"', 60.00, 'UN', 850.00),
       ('PEC-F08', 'Fonte Chaveada 24V 10A', 100.00, 'UN', 120.00),
       ('PEC-V09', 'Válvula Pneumática 5/2 Vias', 180.00, 'UN', 210.00),
       ('PEC-P10', 'Parafuso M8 Aço Inox (Caixa c/ 100)', 80.00, 'UN', 65.00),
       ('PEC-E11', 'Engrenagem de Transmissão Z40', 90.00, 'UN', 180.00),
       ('PEC-L12', 'Lâmpada Sinalizadora LED 24V', 300.00, 'UN', 15.00);

INSERT INTO raw_materials (code, name, stock_quantity, unit, unit_cost)
VALUES ('EMB-P01', 'Palete de Madeira Padrão PBR', 200.00, 'UN', 40.00),
       ('EMB-C02', 'Caixa de Papelão Duplo Reforçado', 500.00, 'UN', 12.00),
       ('EMB-B03', 'Plástico Bolha de Alta Densidade', 2000.00, 'M', 1.50);

INSERT INTO raw_materials (code, name, stock_quantity, unit, unit_cost)
VALUES ('HOR-M01', 'Hora Máquina CNC (Corte a Laser)', 800.00, 'HR', 150.00),
       ('HOR-S02', 'Hora Soldador Especializado TIG', 1000.00, 'HR', 80.00),
       ('HOR-M03', 'Hora Montador Eletromecânico', 1200.00, 'HR', 60.00),
       ('HOR-Q04', 'Hora Engenharia/Teste de Qualidade', 80.00, 'HR', 120.00);

INSERT INTO products (code, name, price)
VALUES ('PROD-01', 'Esteira Transportadora Industrial 5m', 11000.00),
       ('PROD-02', 'Painel de Automação CLP Premium', 9800.00),
       ('PROD-03', 'Bomba Hidráulica de Alta Pressão', 4200.00),
       ('PROD-04', 'Mesa de Inspeção Inox c/ Iluminação', 2900.00),
       ('PROD-05', 'Kit Atuador Pneumático Simples', 850.00),
       ('PROD-06', 'Painel Compacto Automação Light', 9800.00);

INSERT INTO product_materials (product_id, raw_material_id, quantity_required)
VALUES ((SELECT id FROM products WHERE code = 'PROD-01'), (SELECT id FROM raw_materials WHERE code = 'MTL-A01'), 200.00),
       ((SELECT id FROM products WHERE code = 'PROD-01'), (SELECT id FROM raw_materials WHERE code = 'PEC-M01'), 2.00),
       ((SELECT id FROM products WHERE code = 'PROD-01'), (SELECT id FROM raw_materials WHERE code = 'PEC-R04'), 20.00),
       ((SELECT id FROM products WHERE code = 'PROD-01'), (SELECT id FROM raw_materials WHERE code = 'CAB-E01'), 30.00),
       ((SELECT id FROM products WHERE code = 'PROD-01'), (SELECT id FROM raw_materials WHERE code = 'QUI-T01'), 15.00),
       ((SELECT id FROM products WHERE code = 'PROD-01'), (SELECT id FROM raw_materials WHERE code = 'HOR-M01'), 8.00),
       ((SELECT id FROM products WHERE code = 'PROD-01'), (SELECT id FROM raw_materials WHERE code = 'HOR-S02'), 12.00),
       ((SELECT id FROM products WHERE code = 'PROD-01'), (SELECT id FROM raw_materials WHERE code = 'EMB-P01'), 2.00);

INSERT INTO product_materials (product_id, raw_material_id, quantity_required)
VALUES ((SELECT id FROM products WHERE code = 'PROD-02'), (SELECT id FROM raw_materials WHERE code = 'MTL-A01'), 30.00),
       ((SELECT id FROM products WHERE code = 'PROD-02'), (SELECT id FROM raw_materials WHERE code = 'PEC-C03'), 1.00),
       ((SELECT id FROM products WHERE code = 'PROD-02'), (SELECT id FROM raw_materials WHERE code = 'PEC-D07'), 1.00),
       ((SELECT id FROM products WHERE code = 'PROD-02'), (SELECT id FROM raw_materials WHERE code = 'PEC-F08'), 2.00),
       ((SELECT id FROM products WHERE code = 'PROD-02'), (SELECT id FROM raw_materials WHERE code = 'CAB-E02'), 50.00),
       ((SELECT id FROM products WHERE code = 'PROD-02'), (SELECT id FROM raw_materials WHERE code = 'PEC-B06'), 4.00),
       ((SELECT id FROM products WHERE code = 'PROD-02'), (SELECT id FROM raw_materials WHERE code = 'HOR-M03'), 6.00),
       ((SELECT id FROM products WHERE code = 'PROD-02'), (SELECT id FROM raw_materials WHERE code = 'HOR-Q04'), 4.00);

INSERT INTO product_materials (product_id, raw_material_id, quantity_required)
VALUES ((SELECT id FROM products WHERE code = 'PROD-03'), (SELECT id FROM raw_materials WHERE code = 'MTL-AL3'), 45.00),
       ((SELECT id FROM products WHERE code = 'PROD-03'), (SELECT id FROM raw_materials WHERE code = 'PEC-M01'), 1.00),
       ((SELECT id FROM products WHERE code = 'PROD-03'), (SELECT id FROM raw_materials WHERE code = 'QUI-O03'), 4.00),
       ((SELECT id FROM products WHERE code = 'PROD-03'), (SELECT id FROM raw_materials WHERE code = 'HOR-M01'), 5.00),
       ((SELECT id FROM products WHERE code = 'PROD-03'), (SELECT id FROM raw_materials WHERE code = 'HOR-M03'), 3.00);

INSERT INTO product_materials (product_id, raw_material_id, quantity_required)
VALUES ((SELECT id FROM products WHERE code = 'PROD-04'), (SELECT id FROM raw_materials WHERE code = 'MTL-I02'), 60.00),
       ((SELECT id FROM products WHERE code = 'PROD-04'), (SELECT id FROM raw_materials WHERE code = 'PEC-L12'), 4.00),
       ((SELECT id FROM products WHERE code = 'PROD-04'), (SELECT id FROM raw_materials WHERE code = 'CAB-E01'), 15.00),
       ((SELECT id FROM products WHERE code = 'PROD-04'), (SELECT id FROM raw_materials WHERE code = 'HOR-S02'), 8.00);

INSERT INTO product_materials (product_id, raw_material_id, quantity_required)
VALUES ((SELECT id FROM products WHERE code = 'PROD-05'), (SELECT id FROM raw_materials WHERE code = 'PEC-V09'), 2.00),
       ((SELECT id FROM products WHERE code = 'PROD-05'), (SELECT id FROM raw_materials WHERE code = 'MTL-AL3'), 5.00),
       ((SELECT id FROM products WHERE code = 'PROD-05'), (SELECT id FROM raw_materials WHERE code = 'EMB-C02'), 1.00),
       ((SELECT id FROM products WHERE code = 'PROD-05'), (SELECT id FROM raw_materials WHERE code = 'HOR-M03'), 1.00);

INSERT INTO product_materials (product_id, raw_material_id, quantity_required)
VALUES ((SELECT id FROM products WHERE code = 'PROD-06'), (SELECT id FROM raw_materials WHERE code = 'MTL-A01'), 40.00),
        ((SELECT id FROM products WHERE code = 'PROD-06'), (SELECT id FROM raw_materials WHERE code = 'PEC-M02'), 2.00),
        ((SELECT id FROM products WHERE code = 'PROD-06'), (SELECT id FROM raw_materials WHERE code = 'CAB-E02'), 30.00),
        ((SELECT id FROM products WHERE code = 'PROD-06'), (SELECT id FROM raw_materials WHERE code = 'HOR-M03'), 4.00);