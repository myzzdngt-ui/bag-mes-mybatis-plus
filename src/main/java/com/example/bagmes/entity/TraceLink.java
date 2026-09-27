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
 * 跨物料 工单 报工 成品 箱号的追溯关系
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("trace_link")
public class TraceLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "trace_id", type = IdType.AUTO)
    private Long traceId;

    @TableField("source_type")
    private String sourceType;

    @TableField("source_id")
    private Long sourceId;

    @TableField("target_type")
    private String targetType;

    @TableField("target_id")
    private Long targetId;

    @TableField("link_qty")
    private BigDecimal linkQty;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
