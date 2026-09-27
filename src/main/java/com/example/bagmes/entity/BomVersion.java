package com.example.bagmes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * BOM版本
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("bom_version")
public class BomVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "bom_id", type = IdType.AUTO)
    private Long bomId;

    @TableField("sku_id")
    private Long skuId;

    @TableField("bom_version")
    private String bomVersion;

    @TableField("status")
    private String status;

    @TableField("effective_from")
    private LocalDateTime effectiveFrom;

    @TableField("effective_to")
    private LocalDateTime effectiveTo;

    @TableField("approved_by")
    private Long approvedBy;

    @TableField("approved_at")
    private LocalDateTime approvedAt;
}
