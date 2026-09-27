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
 * 生产工单主表
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("work_order")
public class WorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "work_order_id", type = IdType.AUTO)
    private Long workOrderId;

    @TableField("work_order_no")
    private String workOrderNo;

    @TableField("sku_id")
    private Long skuId;

    @TableField("bom_id")
    private Long bomId;

    @TableField("routing_id")
    private Long routingId;

    @TableField("plan_qty")
    private BigDecimal planQty;

    @TableField("status")
    private String status;

    @TableField("priority")
    private Byte priority;

    @TableField("plan_start_time")
    private LocalDateTime planStartTime;

    @TableField("plan_end_time")
    private LocalDateTime planEndTime;

    @TableField("actual_start_time")
    private LocalDateTime actualStartTime;

    @TableField("actual_end_time")
    private LocalDateTime actualEndTime;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
