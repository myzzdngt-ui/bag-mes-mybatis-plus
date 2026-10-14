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
 * 业务操作日志
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-10-14
 */
@Getter
@Setter
@ToString
@TableName("operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作日志主键
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 请求链路标识
     */
    @TableField("trace_id")
    private String traceId;

    /**
     * 业务模块
     */
    @TableField("module_name")
    private String moduleName;

    /**
     * 操作类型
     */
    @TableField("operation_type")
    private String operationType;

    /**
     * 操作名称
     */
    @TableField("operation_name")
    private String operationName;

    /**
     * 请求方法
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 请求 URI
     */
    @TableField("request_uri")
    private String requestUri;

    /**
     * 请求参数
     */
    @TableField("request_params")
    private String requestParams;

    /**
     * 操作人 ID
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 操作人名称
     */
    @TableField("operator_name")
    private String operatorName;

    /**
     * 是否成功
     */
    @TableField("success")
    private Boolean success;

    /**
     * 响应状态码
     */
    @TableField("response_code")
    private Integer responseCode;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 客户端 IP
     */
    @TableField("client_ip")
    private String clientIp;

    /**
     * 客户端 User-Agent
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
