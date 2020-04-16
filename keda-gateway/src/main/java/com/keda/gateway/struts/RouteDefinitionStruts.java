package com.keda.gateway.struts;

import com.keda.gateway.dynamic.GatewayFilterDefinition;
import com.keda.gateway.dynamic.GatewayPredicateDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.util.List;

/**
 * @Author: seowen
 * @Date: 2019/12/19 16:15
 * @Version 1.0
 */
@Mapper
public interface RouteDefinitionStruts {
    RouteDefinitionStruts INSTANCES = Mappers.getMapper(RouteDefinitionStruts.class);

    PredicateDefinition toPredicate(GatewayPredicateDefinition definition);

    List<PredicateDefinition> toPredicate(List<GatewayPredicateDefinition> definition);

    FilterDefinition toFilter(GatewayFilterDefinition definition);

    List<FilterDefinition> toFilter(List<GatewayFilterDefinition> definition);
}
