package com.example.bagmes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bagmes.dto.OperationLogQueryDTO;
import com.example.bagmes.entity.OperationLog;
import com.example.bagmes.service.OperationLogService;
import com.example.bagmes.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 业务操作日志 前端控制器
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-10-14
 */
@RestController
@RequestMapping("/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping("/page")
    public ApiResponse<IPage<OperationLog>> page(@ModelAttribute OperationLogQueryDTO query) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .eq(query.getSuccess() != null, OperationLog::getSuccess, query.getSuccess())
                .ge(query.getCreatedAtBegin() != null, OperationLog::getCreatedAt, query.getCreatedAtBegin())
                .le(query.getCreatedAtEnd() != null, OperationLog::getCreatedAt, query.getCreatedAtEnd())
                .orderByDesc(OperationLog::getCreatedAt)
                .orderByDesc(OperationLog::getLogId);
        addLike(wrapper, query.getModule(), OperationLog::getModuleName);
        addLike(wrapper, query.getOperationType(), OperationLog::getOperationType);
        addLike(wrapper, query.getRequestUri(), OperationLog::getRequestUri);
        return ApiResponse.success(operationLogService.page(
                new Page<>(query.normalizedPageNum(), query.normalizedPageSize()), wrapper));
    }

    private <T> void addLike(LambdaQueryWrapper<OperationLog> wrapper, String value,
                             com.baomidou.mybatisplus.core.toolkit.support.SFunction<OperationLog, T> column) {
        if (value != null && !value.isBlank()) {
            wrapper.like(column, value.trim());
        }
    }
}
