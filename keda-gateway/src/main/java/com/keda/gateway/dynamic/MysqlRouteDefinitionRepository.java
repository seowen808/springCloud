package com.keda.gateway.dynamic;

import com.alibaba.fastjson.JSONArray;
import com.keda.gateway.entity.DynamicRouteVo;
import com.keda.gateway.entity.GatewayDynamicRoute;
import com.keda.gateway.service.IGatewayDynamicRouteService;
import com.keda.gateway.struts.DynamicRouteStruts;
import com.keda.util.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: seowen
 * @Date: 2019/12/19 15:27
 * @Version 1.0
 */
@Component
public class MysqlRouteDefinitionRepository implements RouteDefinitionRepository {

    public static final String  GATEWAY_ROUTES = "geteway_routes";
    @Autowired
    private IGatewayDynamicRouteService routeService;
    @Autowired
    private RedisService redisService;

    public static final Logger LOGGER = LoggerFactory.getLogger(MysqlRouteDefinitionRepository.class);

    /**
     * Gateway启动的时候，会加载这个方法
     * @return
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        //从数据库中，获取我们自定义的 路由信息数据
        Object obj = redisService.hmGetAll(GATEWAY_ROUTES);
        //LOGGER.info("读取redis中key为{}，的数据{}",GATEWAY_ROUTES,JSON.toJSONString(obj));
        List<RouteDefinition> routeList = null;
        if (Objects.isNull(obj) || MapUtils.isEmpty((Map<?, ?>) obj)) {
            List<GatewayDynamicRoute> listByEnable = routeService.getListByEnable(true);
           // LOGGER.info("读取数据库中的路由信息为{}",JSON.toJSONString(listByEnable));
            if (CollectionUtils.isNotEmpty(listByEnable)){
                //转换成 RouteDefinition 集合后，返回
                routeList = this.toRouteList(redisService,listByEnable);
            }else {

            }
        }else {
            // map转list
            routeList = ((Map<String,RouteDefinition>)obj).entrySet().parallelStream().map(map->map.getValue()).collect(Collectors.toList());
        }
 //       LOGGER.info("路由数据转换后的信息为{}",JSON.toJSONString(routeList));
//        if (CollectionUtils.isNotEmpty(routeList)){

              return Flux.fromIterable(routeList);
//        }
//        //如果 数据库为 空，则返回一个 空的集合
//        return Flux.fromIterable(new ArrayList<RouteDefinition>());
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }

    /**
     * 转换成 List<RouteDefinition>
     * @param redisService
     * @param listByEnable
     * @return
     */
    private List<RouteDefinition> toRouteList(RedisService redisService,List<GatewayDynamicRoute> listByEnable){
        List<RouteDefinition> routeList = new ArrayList<>();
        /**
         * 循环转换：
         * 因为数据库中，Predicates 和 Filters 存储的 json字符串。所以，得先转换成 对应的 vo.
         * 然后在转换成 List<PredicateDefinition>和 List<FilterDefinition>
         */

        listByEnable.stream().forEach(gw->{
            RouteDefinition r = this.setRouteDefinition(gw);
            routeList.add(r);
            redisService.hmSet(GATEWAY_ROUTES,r.getId(),r);
        });
        return routeList;
    }

    public RouteDefinition setRouteDefinition(GatewayDynamicRoute gw){
        RouteDefinition r = new RouteDefinition();
        r.setUri(DynamicUtil.getUri(gw.getUri()));
        r.setOrder(gw.getOrder());
        r.setId(gw.getRouteId());
        r.setPredicates(DynamicRouteStruts.INSTANCES.toPredicateDefinition(JSONArray.parseArray(gw.getPredicates(), DynamicRouteVo.PredicateDefinitionVo.class)));
        r.setFilters(DynamicRouteStruts.INSTANCES.toFilterDefinition(JSONArray.parseArray(gw.getFilters(),DynamicRouteVo.FilterDefinitionVo.class)));
        return r;
    }

}
