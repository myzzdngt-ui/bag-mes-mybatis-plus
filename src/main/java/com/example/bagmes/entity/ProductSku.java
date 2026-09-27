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
 * 产品SKU主数据
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("product_sku")
public class ProductSku implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sku_id", type = IdType.AUTO)
    private Long skuId;

    @TableField("sku_code")
    private String skuCode;

    @TableField("style_code")
    private String styleCode;

    @TableField("color_code")
    private String colorCode;

    @TableField("spec_code")
    private String specCode;

    @TableField("product_category")
    private String productCategory;

    @TableField("unit")
    private String unit;

    @TableField("status")
    private Byte status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
