package com.example.bagmes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bagmes.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 生产工单主表 Mapper 接口
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

}
