USE bag_mes;

INSERT IGNORE INTO product_sku
    (sku_id, sku_code, style_code, color_code, spec_code, product_category)
VALUES
    (1, 'SKU-BAG-001', 'STYLE-001', 'BLACK', 'M', 'SOFT_BAG');

INSERT IGNORE INTO bom_version (bom_id, sku_id, bom_version, status)
VALUES (1, 1, 'V1.0', 'APPROVED');

INSERT IGNORE INTO routing_version (routing_id, sku_id, routing_version, status)
VALUES (1, 1, 'V1.0', 'APPROVED');

DELETE FROM work_order
WHERE work_order_no IN ('WO20260901', 'WO20260902', 'WO20260903');

INSERT INTO work_order
    (work_order_no, sku_id, bom_id, routing_id, plan_qty, status, priority, plan_start_time, plan_end_time)
VALUES
    ('WO20260901', 1, 1, 1, 1000, 'RELEASED', 2, '2026-09-01 08:00:00', '2026-09-05 18:00:00'),
    ('WO20260902', 1, 1, 1, 500, 'RUNNING', 1, '2026-09-06 08:00:00', '2026-09-10 18:00:00'),
    ('WO20260903', 1, 1, 1, 300, 'RELEASED', 1, '2026-09-11 08:00:00', '2026-09-12 18:00:00');
