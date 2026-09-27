package com.example.bagmes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 工艺工序
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Getter
@Setter
@ToString
@TableName("routing_operation")
public class RoutingOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "operation_id", type = IdType.AUTO)
    private Long operationId;

    @TableField("routing_id")
    private Long routingId;

    @TableField("operation_seq")
    private Integer operationSeq;

    @TableField("operation_code")
    private String operationCode;

    @TableField("operation_name")
    private String operationName;

    @TableField("work_center")
    private String workCenter;

    @TableField("required_inspection")
    private Byte requiredInspection;
}
