package com.example.bagmes.service.impl;

import com.example.bagmes.entity.ProductionReport;
import com.example.bagmes.mapper.ProductionReportMapper;
import com.example.bagmes.service.ProductionReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 按月分区的工序报工事实表 服务实现类
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Service
public class ProductionReportServiceImpl extends ServiceImpl<ProductionReportMapper, ProductionReport> implements ProductionReportService {

}
