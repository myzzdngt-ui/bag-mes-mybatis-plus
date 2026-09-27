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
 * 质量检验与处置
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("quality_inspection")
public class QualityInspection implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "inspection_id", type = IdType.AUTO)
    private Long inspectionId;

    @TableField("work_order_id")
    private Long workOrderId;

    @TableField("report_id")
    private Long reportId;

    @TableField("inspection_type")
    private String inspectionType;

    @TableField("result")
    private String result;

    @TableField("defect_code")
    private String defectCode;

    @TableField("inspected_qty")
    private BigDecimal inspectedQty;

    @TableField("failed_qty")
    private BigDecimal failedQty;

    @TableField("inspector_id")
    private Long inspectorId;

    @TableField("inspected_at")
    private LocalDateTime inspectedAt;

    @TableField("disposition")
    private String disposition;
}
