package com.keda.gateway.dynamic;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: seowen
 * @Date: 2019/12/19 15:38
 * @Version 1.0
 */
@Data
public class GatewayRouteDefinition implements Serializable {

    private static final long serialVersionUID = 1116611165045260463L;

    /**
     * 路由的Id
     */
    private String id;

    /**
     * 路由断言集合配置
     */
    private List<GatewayPredicateDefinition> predicates = new ArrayList<>();

    /**
     * 路由过滤器集合配置
     */
    private List<GatewayFilterDefinition> filters = new ArrayList<>();

    /**
     * 路由规则转发的目标uri
     */
    private String uri;

    /**
     * 路由执行的顺序
     */
    private int order = 0;


}
