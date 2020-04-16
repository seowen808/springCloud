package com.keda.gateway.struts;

import com.keda.gateway.entity.DynamicRouteVo;
import com.keda.gateway.entity.GatewayDynamicRoute;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

/**
 * @Author: seowen
 * @Date: 2019/12/20 11:15
 * @Version 1.0
 */
@Mapper(uses = {StringToMapMapper.class})
public interface DynamicRouteStruts {

    DynamicRouteStruts INSTANCES = Mappers.getMapper(DynamicRouteStruts.class);

    FilterDefinition toFilterDefinition(DynamicRouteVo.FilterDefinitionVo filterDefinitionVo);

    List<FilterDefinition> toFilterDefinition(List<DynamicRouteVo.FilterDefinitionVo> filterDefinitionVo);

    PredicateDefinition toPredicateDefinition(DynamicRouteVo.PredicateDefinitionVo predicateDefinitionVo);

    List<PredicateDefinition> toPredicateDefinition(List<DynamicRouteVo.PredicateDefinitionVo> predicateDefinitionVos);


    GatewayDynamicRoute toGatewayDynamicRoute(DynamicRouteVo.RouteBean routeBean);

    List<GatewayDynamicRoute> toGatewayDynamicRoute(List<DynamicRouteVo.RouteBean> routeBean);
}
