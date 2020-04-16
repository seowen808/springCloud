package com.keda.gateway.struts;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.cloud.gateway.route.RouteDefinition;

/**
 * @Author: seowen
 * @Date: 2019/12/20 10:46
 * @Version 1.0
 */
@Mapper
public interface GatewayDynamicRouteStruts {

    GatewayDynamicRouteStruts INSTANCES = Mappers.getMapper(GatewayDynamicRouteStruts.class);


}
