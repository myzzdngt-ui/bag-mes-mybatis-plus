package com.example.bagmes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bagmes.entity.ProductionReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 按月分区的工序报工事实表 Mapper 接口
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Mapper
public interface ProductionReportMapper extends BaseMapper<ProductionReport> {

}
