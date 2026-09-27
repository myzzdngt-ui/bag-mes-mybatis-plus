-- 箱包 MES 数据库核心表 DDL
CREATE DATABASE IF NOT EXISTS bag_mes DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE bag_mes;

CREATE TABLE product_sku (
  sku_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, sku_code VARCHAR(64) NOT NULL,
  style_code VARCHAR(64) NOT NULL, color_code VARCHAR(32) NOT NULL, spec_code VARCHAR(32) NOT NULL,
  product_category ENUM('LEATHER','SOFT_BAG','HARD_CASE','ACCESSORY') NOT NULL,
  unit VARCHAR(16) NOT NULL DEFAULT 'PCS', status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (sku_id), UNIQUE KEY uk_sku_code (sku_code), KEY idx_sku_style (style_code,color_code)
) ENGINE=InnoDB COMMENT='产品SKU主数据';

CREATE TABLE material_lot (
  material_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, material_code VARCHAR(64) NOT NULL,
  material_name VARCHAR(128) NOT NULL, material_type VARCHAR(32) NOT NULL, color_code VARCHAR(32),
  lot_no VARCHAR(64) NOT NULL, unit VARCHAR(16) NOT NULL, qty_available DECIMAL(18,4) NOT NULL DEFAULT 0,
  quality_status ENUM('AVAILABLE','HOLD','REJECTED','USED_UP') NOT NULL DEFAULT 'AVAILABLE', supplier_code VARCHAR(64),
  received_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(material_id), UNIQUE KEY uk_material_lot(material_code,lot_no), KEY idx_material_status(color_code,quality_status)
) ENGINE=InnoDB COMMENT='物料批次';

CREATE TABLE bom_version (
  bom_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, sku_id BIGINT UNSIGNED NOT NULL, bom_version VARCHAR(32) NOT NULL,
  status ENUM('DRAFT','APPROVED','OBSOLETE') NOT NULL DEFAULT 'DRAFT', effective_from DATETIME, effective_to DATETIME,
  approved_by BIGINT UNSIGNED, approved_at DATETIME, PRIMARY KEY(bom_id), UNIQUE KEY uk_bom(sku_id,bom_version), KEY idx_bom_effective(sku_id,effective_from),
  CONSTRAINT fk_bom_sku FOREIGN KEY(sku_id) REFERENCES product_sku(sku_id)
) ENGINE=InnoDB COMMENT='BOM版本';

CREATE TABLE bom_item (
  bom_item_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, bom_id BIGINT UNSIGNED NOT NULL, material_code VARCHAR(64) NOT NULL,
  qty_per_unit DECIMAL(18,6) NOT NULL, loss_rate DECIMAL(8,5) NOT NULL DEFAULT 0, color_same_lot TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY(bom_item_id), UNIQUE KEY uk_bom_item(bom_id,material_code), CONSTRAINT fk_bom_item_bom FOREIGN KEY(bom_id) REFERENCES bom_version(bom_id)
) ENGINE=InnoDB COMMENT='BOM明细';

CREATE TABLE routing_version (
  routing_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, sku_id BIGINT UNSIGNED NOT NULL, routing_version VARCHAR(32) NOT NULL,
  status ENUM('DRAFT','APPROVED','OBSOLETE') NOT NULL DEFAULT 'DRAFT', effective_from DATETIME, effective_to DATETIME,
  PRIMARY KEY(routing_id), UNIQUE KEY uk_routing(sku_id,routing_version), CONSTRAINT fk_routing_sku FOREIGN KEY(sku_id) REFERENCES product_sku(sku_id)
) ENGINE=InnoDB COMMENT='工艺路线版本';

CREATE TABLE routing_operation (
  operation_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, routing_id BIGINT UNSIGNED NOT NULL, operation_seq INT NOT NULL,
  operation_code VARCHAR(32) NOT NULL, operation_name VARCHAR(64) NOT NULL, work_center VARCHAR(64), required_inspection TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY(operation_id), UNIQUE KEY uk_routing_seq(routing_id,operation_seq), CONSTRAINT fk_operation_routing FOREIGN KEY(routing_id) REFERENCES routing_version(routing_id)
) ENGINE=InnoDB COMMENT='工艺工序';

CREATE TABLE work_order (
  work_order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, work_order_no VARCHAR(64) NOT NULL, sku_id BIGINT UNSIGNED NOT NULL,
  bom_id BIGINT UNSIGNED NOT NULL, routing_id BIGINT UNSIGNED NOT NULL, plan_qty DECIMAL(18,4) NOT NULL,
  status ENUM('PLANNED','RELEASED','RUNNING','HOLD','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PLANNED', priority TINYINT NOT NULL DEFAULT 0,
  plan_start_time DATETIME NOT NULL, plan_end_time DATETIME NOT NULL, actual_start_time DATETIME, actual_end_time DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(work_order_id), UNIQUE KEY uk_work_order_no(work_order_no), KEY idx_work_order_plan(plan_start_time,status), KEY idx_work_order_sku(sku_id),
  CONSTRAINT fk_work_sku FOREIGN KEY(sku_id) REFERENCES product_sku(sku_id), CONSTRAINT fk_work_bom FOREIGN KEY(bom_id) REFERENCES bom_version(bom_id), CONSTRAINT fk_work_routing FOREIGN KEY(routing_id) REFERENCES routing_version(routing_id)
) ENGINE=InnoDB COMMENT='生产工单主表';

CREATE TABLE material_issue (
  issue_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, work_order_id BIGINT UNSIGNED NOT NULL, material_id BIGINT UNSIGNED NOT NULL,
  issue_type ENUM('ISSUE','RETURN','SUPPLEMENT') NOT NULL, qty DECIMAL(18,4) NOT NULL, operator_id BIGINT UNSIGNED NOT NULL, issue_time DATETIME NOT NULL,
  PRIMARY KEY(issue_id), KEY idx_issue_work(work_order_id,issue_time), KEY idx_issue_material(material_id),
  CONSTRAINT fk_issue_work FOREIGN KEY(work_order_id) REFERENCES work_order(work_order_id), CONSTRAINT fk_issue_material FOREIGN KEY(material_id) REFERENCES material_lot(material_id)
) ENGINE=InnoDB COMMENT='领料退料补料流水';

CREATE TABLE production_report (
  report_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, report_event_id CHAR(36) NOT NULL, work_order_id BIGINT UNSIGNED NOT NULL,
  operation_id BIGINT UNSIGNED NOT NULL, report_time DATETIME NOT NULL, input_qty DECIMAL(18,4) NOT NULL DEFAULT 0,
  good_qty DECIMAL(18,4) NOT NULL DEFAULT 0, rework_qty DECIMAL(18,4) NOT NULL DEFAULT 0, scrap_qty DECIMAL(18,4) NOT NULL DEFAULT 0,
  operator_id BIGINT UNSIGNED NOT NULL, batch_no VARCHAR(64), remark VARCHAR(255),
  PRIMARY KEY(report_id,report_time), UNIQUE KEY uk_report_event(report_event_id,report_time), KEY idx_report_work_time(work_order_id,report_time), KEY idx_report_batch(batch_no,report_time)
) ENGINE=InnoDB COMMENT='按月分区的工序报工事实表'
PARTITION BY RANGE COLUMNS(report_time)(PARTITION p202601 VALUES LESS THAN ('2026-02-01'),PARTITION p202602 VALUES LESS THAN ('2026-03-01'),PARTITION pmax VALUES LESS THAN (MAXVALUE));

CREATE TABLE quality_inspection (
  inspection_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, work_order_id BIGINT UNSIGNED NOT NULL, report_id BIGINT UNSIGNED,
  inspection_type ENUM('FIRST','PATROL','FINAL','REWORK') NOT NULL, result ENUM('PASS','FAIL','HOLD') NOT NULL, defect_code VARCHAR(64),
  inspected_qty DECIMAL(18,4) NOT NULL, failed_qty DECIMAL(18,4) NOT NULL DEFAULT 0, inspector_id BIGINT UNSIGNED NOT NULL, inspected_at DATETIME NOT NULL,
  disposition ENUM('RELEASE','REWORK','SCRAP','QUARANTINE') NOT NULL, PRIMARY KEY(inspection_id), KEY idx_quality_work(work_order_id,inspected_at), KEY idx_quality_result(result,inspected_at),
  CONSTRAINT fk_quality_work FOREIGN KEY(work_order_id) REFERENCES work_order(work_order_id)
) ENGINE=InnoDB COMMENT='质量检验与处置';

CREATE TABLE packing_box (
  box_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, box_no VARCHAR(64) NOT NULL, work_order_id BIGINT UNSIGNED NOT NULL,
  sku_id BIGINT UNSIGNED NOT NULL, packed_qty DECIMAL(18,4) NOT NULL, status ENUM('OPEN','SEALED','HANDED_OVER','CANCELLED') NOT NULL DEFAULT 'OPEN',
  packed_at DATETIME, handed_over_at DATETIME, PRIMARY KEY(box_id), UNIQUE KEY uk_box_no(box_no), KEY idx_box_work(work_order_id),
  CONSTRAINT fk_box_work FOREIGN KEY(work_order_id) REFERENCES work_order(work_order_id), CONSTRAINT fk_box_sku FOREIGN KEY(sku_id) REFERENCES product_sku(sku_id)
) ENGINE=InnoDB COMMENT='装箱与交接';

CREATE TABLE trace_link (
  trace_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT, source_type VARCHAR(32) NOT NULL, source_id BIGINT UNSIGNED NOT NULL,
  target_type VARCHAR(32) NOT NULL, target_id BIGINT UNSIGNED NOT NULL, link_qty DECIMAL(18,4), created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(trace_id), KEY idx_trace_source(source_type,source_id), KEY idx_trace_target(target_type,target_id)
) ENGINE=InnoDB COMMENT='跨物料 工单 报工 成品 箱号的追溯关系';
