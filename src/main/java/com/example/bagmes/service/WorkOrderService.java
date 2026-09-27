package com.example.bagmes.service;

import com.example.bagmes.entity.WorkOrder;
import com.example.bagmes.dto.WorkOrderQueryDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * <p>
 * 生产工单主表 服务类
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
public interface WorkOrderService extends IService<WorkOrder> {

    IPage<WorkOrder> pageQuery(WorkOrderQueryDTO query);

}
