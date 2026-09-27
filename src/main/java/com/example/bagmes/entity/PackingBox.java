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
 * 装箱与交接
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("packing_box")
public class PackingBox implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "box_id", type = IdType.AUTO)
    private Long boxId;

    @TableField("box_no")
    private String boxNo;

    @TableField("work_order_id")
    private Long workOrderId;

    @TableField("sku_id")
    private Long skuId;

    @TableField("packed_qty")
    private BigDecimal packedQty;

    @TableField("status")
    private String status;

    @TableField("packed_at")
    private LocalDateTime packedAt;

    @TableField("handed_over_at")
    private LocalDateTime handedOverAt;
}
