package com.example.bagmes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bagmes.entity.ApiMonitorLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 接口性能与异常监控日志 Mapper 接口
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-10-14
 */
@Mapper
public interface ApiMonitorLogMapper extends BaseMapper<ApiMonitorLog> {

}
