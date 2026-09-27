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
 * 物料批次
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("material_lot")
public class MaterialLot implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "material_id", type = IdType.AUTO)
    private Long materialId;

    @TableField("material_code")
    private String materialCode;

    @TableField("material_name")
    private String materialName;

    @TableField("material_type")
    private String materialType;

    @TableField("color_code")
    private String colorCode;

    @TableField("lot_no")
    private String lotNo;

    @TableField("unit")
    private String unit;

    @TableField("qty_available")
    private BigDecimal qtyAvailable;

    @TableField("quality_status")
    private String qualityStatus;

    @TableField("supplier_code")
    private String supplierCode;

    @TableField("received_at")
    private LocalDateTime receivedAt;
}
