package com.example.bagmes.service.impl;

import com.example.bagmes.entity.ApiMonitorLog;
import com.example.bagmes.mapper.ApiMonitorLogMapper;
import com.example.bagmes.service.ApiMonitorLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 接口性能与异常监控日志 服务实现类
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-10-14
 */
@Service
public class ApiMonitorLogServiceImpl extends ServiceImpl<ApiMonitorLogMapper, ApiMonitorLog> implements ApiMonitorLogService {

}
