package com.keda.gateway.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: seowen
 * @Date: 2019/12/20 11:08
 * @Version 1.0
 */
@Data
public class DynamicRouteVo  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 过滤器vo
     */
    @Data
    public static class FilterDefinitionVo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private Map<String, String> args = new LinkedHashMap();
    }

    /**
     * 过滤器vo
     */
    @Data
    public static class PredicateDefinitionVo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private Map<String, String> args = new LinkedHashMap();
    }

    @Data
    public static class RouteBean implements Serializable {
        private static final long serialVersionUID = 1L;

        private String routeId;

        private String uri;

        private Integer order;

        private List<PredicateDefinitionVo> predicates;

        private List<FilterDefinitionVo> filters;

        private Boolean enable;
    }

    @Data
    public static class RouteBeanList implements Serializable {
        private static final long serialVersionUID = 1L;

        private List<RouteBean> list;
    }

}
