package com.example.bagmes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bagmes.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 业务操作日志 Mapper 接口
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-10-14
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

}
