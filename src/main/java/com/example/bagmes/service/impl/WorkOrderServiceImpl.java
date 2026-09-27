package com.example.bagmes.service.impl;

import com.example.bagmes.entity.WorkOrder;
import com.example.bagmes.mapper.WorkOrderMapper;
import com.example.bagmes.service.WorkOrderService;
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

}
