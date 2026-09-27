package com.example.bagmes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bagmes.entity.TraceLink;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 跨物料 工单 报工 成品 箱号的追溯关系 Mapper 接口
 * </p>
 *
 * @author bag-mes 小组
 * @since 2026-09-27
 */
@Mapper
public interface TraceLinkMapper extends BaseMapper<TraceLink> {

}
