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
 * 工艺路线版本
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("routing_version")
public class RoutingVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "routing_id", type = IdType.AUTO)
    private Long routingId;

    @TableField("sku_id")
    private Long skuId;

    @TableField("routing_version")
    private String routingVersion;

    @TableField("status")
    private String status;

    @TableField("effective_from")
    private LocalDateTime effectiveFrom;

    @TableField("effective_to")
    private LocalDateTime effectiveTo;
}
