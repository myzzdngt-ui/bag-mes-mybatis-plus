package com.example.bagmes.service.impl;

import com.example.bagmes.entity.WorkOrder;
import com.example.bagmes.dto.WorkOrderQueryDTO;
import com.example.bagmes.mapper.WorkOrderMapper;
import com.example.bagmes.service.WorkOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 生产工单主表 服务实现类
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {

    @Override
    public IPage<WorkOrder> pageQuery(WorkOrderQueryDTO query) {
        long pageNum = Math.max(query.getPageNum(), 1);
        long pageSize = Math.min(Math.max(query.getPageSize(), 1), 100);
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<WorkOrder>()
                .like(query.getWorkOrderNo() != null && !query.getWorkOrderNo().isBlank(), WorkOrder::getWorkOrderNo, query.getWorkOrderNo())
                .eq(query.getStatus() != null && !query.getStatus().isBlank(), WorkOrder::getStatus, query.getStatus())
                .eq(query.getSkuId() != null, WorkOrder::getSkuId, query.getSkuId())
                .eq(query.getPriority() != null, WorkOrder::getPriority, query.getPriority())
                .ge(query.getPlanStartTimeBegin() != null, WorkOrder::getPlanStartTime, query.getPlanStartTimeBegin())
                .le(query.getPlanStartTimeEnd() != null, WorkOrder::getPlanStartTime, query.getPlanStartTimeEnd())
                .orderByDesc(WorkOrder::getPriority)
                .orderByAsc(WorkOrder::getPlanStartTime);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

}
