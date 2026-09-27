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
 * 领料退料补料流水
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("material_issue")
public class MaterialIssue implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "issue_id", type = IdType.AUTO)
    private Long issueId;

    @TableField("work_order_id")
    private Long workOrderId;

    @TableField("material_id")
    private Long materialId;

    @TableField("issue_type")
    private String issueType;

    @TableField("qty")
    private BigDecimal qty;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("issue_time")
    private LocalDateTime issueTime;
}
