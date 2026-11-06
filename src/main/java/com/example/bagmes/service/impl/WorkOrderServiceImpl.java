package com.example.bagmes.service.impl;

import com.example.bagmes.entity.WorkOrder;
import com.example.bagmes.dto.WorkOrderQueryDTO;
import com.example.bagmes.mapper.WorkOrderMapper;
import com.example.bagmes.service.WorkOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(WorkOrderServiceImpl.class);

    @Override
    public IPage<WorkOrder> pageQuery(WorkOrderQueryDTO query) {
        long pageNum = Math.max(query.getPageNum(), 1);
        long pageSize = Math.min(Math.max(query.getPageSize(), 1), 100);
        long startedAt = System.nanoTime();
        log.debug("工单分页查询开始 pageNum={} pageSize={} status={} priority={}",
                pageNum, pageSize, query.getStatus(), query.getPriority());
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<WorkOrder>()
                .like(query.getWorkOrderNo() != null && !query.getWorkOrderNo().isBlank(), WorkOrder::getWorkOrderNo, query.getWorkOrderNo())
                .eq(query.getStatus() != null && !query.getStatus().isBlank(), WorkOrder::getStatus, query.getStatus())
                .eq(query.getSkuId() != null, WorkOrder::getSkuId, query.getSkuId())
                .eq(query.getPriority() != null, WorkOrder::getPriority, query.getPriority())
                .ge(query.getPlanStartTimeBegin() != null, WorkOrder::getPlanStartTime, query.getPlanStartTimeBegin())
                .le(query.getPlanStartTimeEnd() != null, WorkOrder::getPlanStartTime, query.getPlanStartTimeEnd())
                .orderByDesc(WorkOrder::getPriority)
                .orderByAsc(WorkOrder::getPlanStartTime);
        try {
            IPage<WorkOrder> result = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
            long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            log.info("工单分页查询完成 pageNum={} pageSize={} resultCount={} durationMs={}",
                    pageNum, pageSize, result.getRecords().size(), durationMs);
            return result;
        } catch (RuntimeException exception) {
            long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            log.error("工单分页查询失败 pageNum={} pageSize={} durationMs={}",
                    pageNum, pageSize, durationMs, exception);
            throw exception;
        }
    }

}
