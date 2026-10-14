package com.example.bagmes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bagmes.dto.ApiMonitorLogQueryDTO;
import com.example.bagmes.entity.ApiMonitorLog;
import com.example.bagmes.service.ApiMonitorLogService;
import com.example.bagmes.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 接口性能与异常监控日志 前端控制器
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-10-14
 */
@RestController
@RequestMapping("/api-monitor-logs")
public class ApiMonitorLogController {

    private final ApiMonitorLogService apiMonitorLogService;

    public ApiMonitorLogController(ApiMonitorLogService apiMonitorLogService) {
        this.apiMonitorLogService = apiMonitorLogService;
    }

    @GetMapping("/page")
    public ApiResponse<IPage<ApiMonitorLog>> page(@ModelAttribute ApiMonitorLogQueryDTO query) {
        LambdaQueryWrapper<ApiMonitorLog> wrapper = new LambdaQueryWrapper<ApiMonitorLog>()
                .eq(query.getStatusCode() != null, ApiMonitorLog::getStatusCode, query.getStatusCode())
                .eq(query.getSlowFlag() != null, ApiMonitorLog::getSlowFlag, query.getSlowFlag())
                .ge(query.getCreatedAtBegin() != null, ApiMonitorLog::getCreatedAt, query.getCreatedAtBegin())
                .le(query.getCreatedAtEnd() != null, ApiMonitorLog::getCreatedAt, query.getCreatedAtEnd())
                .orderByDesc(ApiMonitorLog::getCreatedAt)
                .orderByDesc(ApiMonitorLog::getMonitorId);
        addLike(wrapper, query.getRequestUri(), ApiMonitorLog::getRequestUri);
        addLike(wrapper, query.getRequestMethod(), ApiMonitorLog::getRequestMethod);
        return ApiResponse.success(apiMonitorLogService.page(
                new Page<>(query.normalizedPageNum(), query.normalizedPageSize()), wrapper));
    }

    private <T> void addLike(LambdaQueryWrapper<ApiMonitorLog> wrapper, String value,
                             com.baomidou.mybatisplus.core.toolkit.support.SFunction<ApiMonitorLog, T> column) {
        if (value != null && !value.isBlank()) {
            wrapper.like(column, value.trim());
        }
    }
}
