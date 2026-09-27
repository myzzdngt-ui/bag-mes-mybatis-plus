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

/**
 * <p>
 * BOM明细
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("bom_item")
public class BomItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "bom_item_id", type = IdType.AUTO)
    private Long bomItemId;

    @TableField("bom_id")
    private Long bomId;

    @TableField("material_code")
    private String materialCode;

    @TableField("qty_per_unit")
    private BigDecimal qtyPerUnit;

    @TableField("loss_rate")
    private BigDecimal lossRate;

    @TableField("color_same_lot")
    private Byte colorSameLot;
}
