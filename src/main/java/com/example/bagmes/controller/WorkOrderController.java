package com.example.bagmes.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.bagmes.dto.WorkOrderQueryDTO;
import com.example.bagmes.entity.WorkOrder;
import com.example.bagmes.service.WorkOrderService;
import com.example.bagmes.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 生产工单主表 前端控制器
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@RestController
@RequestMapping("/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @GetMapping("/page")
    public ApiResponse<IPage<WorkOrder>> pageQuery(@ModelAttribute WorkOrderQueryDTO query) {
        return ApiResponse.success(workOrderService.pageQuery(query));
    }
}
