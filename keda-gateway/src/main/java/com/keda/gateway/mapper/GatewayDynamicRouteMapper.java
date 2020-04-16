package com.keda.gateway.mapper;

import com.keda.gateway.entity.GatewayDynamicRoute;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * gateway动态路由配置表 Mapper 接口
 * </p>
 *
 * @author seowen
 * @since 2019-12-20
 */
@Mapper
public interface GatewayDynamicRouteMapper extends BaseMapper<GatewayDynamicRoute> {

}
