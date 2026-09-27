package com.example.bagmes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 按月分区的工序报工事实表
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("production_report")
public class ProductionReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "report_id", type = IdType.AUTO)
    private Long reportId;

    @TableField("report_event_id")
    private String reportEventId;

    @TableField("work_order_id")
    private Long workOrderId;

    @TableField("operation_id")
    private Long operationId;

    @TableId("report_time")
    private LocalDateTime reportTime;

    @TableField("input_qty")
    private BigDecimal inputQty;

    @TableField("good_qty")
    private BigDecimal goodQty;

    @TableField("rework_qty")
    private BigDecimal reworkQty;

    @TableField("scrap_qty")
    private BigDecimal scrapQty;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("batch_no")
    private String batchNo;

    @TableField("remark")
    private String remark;
}
