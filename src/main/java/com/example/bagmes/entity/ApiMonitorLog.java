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
 * 接口性能与异常监控日志
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-10-14
 */
@Getter
@Setter
@ToString
@TableName("api_monitor_log")
public class ApiMonitorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口监控日志主键
     */
    @TableId(value = "monitor_id", type = IdType.AUTO)
    private Long monitorId;

    /**
     * 请求链路标识
     */
    @TableField("trace_id")
    private String traceId;

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
     * 处理器方法
     */
    @TableField("handler_method")
    private String handlerMethod;

    /**
     * 响应状态码
     */
    @TableField("status_code")
    private Integer statusCode;

    /**
     * 耗时毫秒
     */
    @TableField("duration_ms")
    private Long durationMs;

    /**
     * 是否慢请求
     */
    @TableField("slow_flag")
    private Boolean slowFlag;

    /**
     * 异常类型
     */
    @TableField("exception_class")
    private String exceptionClass;

    /**
     * 异常信息
     */
    @TableField("exception_message")
    private String exceptionMessage;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
