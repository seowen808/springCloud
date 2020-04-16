package com.keda.gateway.controller;


import com.keda.gateway.dynamic.DynamicRouteServiceImpl;
import com.keda.gateway.dynamic.DynamicUtil;
import com.keda.gateway.dynamic.GatewayRouteDefinition;
import com.keda.gateway.entity.DynamicRouteVo;
import com.keda.gateway.entity.GatewayDynamicRoute;
import com.keda.gateway.service.IGatewayDynamicRouteService;
import com.keda.gateway.struts.DynamicRouteStruts;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * gateway动态路由配置表 前端控制器
 * </p>
 *
 * @author seowen
 * @since 2019-12-20
 */
@RestController
@RequestMapping("/gateway-dynamic-route")
public class GatewayDynamicRouteController {


    @Autowired
    private IGatewayDynamicRouteService service;
    @Autowired
    private DynamicRouteServiceImpl dynamicRouteService;
    /**
     * 增加路由
     * @param routeBean
     * @return
     */
    @PostMapping("/add")
    public String add(@RequestBody DynamicRouteVo.RouteBean routeBean){
        service.saveOne(DynamicRouteStruts.INSTANCES.toGatewayDynamicRoute(routeBean));
        return this.dynamicRouteService.doLoad();
    }

    @PostMapping("/addList")
    public String addList(@RequestBody DynamicRouteVo.RouteBeanList routeBeanList){
        service.saveList(DynamicRouteStruts.INSTANCES.toGatewayDynamicRoute(routeBeanList.getList()));
        return this.dynamicRouteService.doLoad();
    }

    /**
     * 删除路由
     * @param id
     * @return
     */
    @PostMapping("/del/{id}")
    public String delete(@PathVariable String id){
        service.disabled(id);
        return this.dynamicRouteService.doLoad();
    }

    /**
     * 更新路由
     * @param routeBean
     * @return
     */
    @PostMapping("/update")
    public String update(@RequestBody DynamicRouteVo.RouteBean routeBean){
        service.update(DynamicRouteStruts.INSTANCES.toGatewayDynamicRoute(routeBean));
        return this.dynamicRouteService.doLoad();
    }


}
