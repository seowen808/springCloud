package com.keda.gateway.service;

import com.keda.gateway.entity.GatewayDynamicRoute;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * gateway动态路由配置表 服务类
 * </p>
 *
 * @author seowen
 * @since 2019-12-20
 */
public interface IGatewayDynamicRouteService extends IService<GatewayDynamicRoute> {


    /**
     * 获取所有 启用的 路由配置信息
     * @param enable
     * @return
     */
    List<GatewayDynamicRoute> getListByEnable(Boolean enable);

    /**
     * 禁用指定 路由Id
     * @param routeId
     * @return
     */
    String disabled(String routeId);

    /**
     * 启用指定路由Id
     * @param routeId
     * @return
     */
    String enable(String routeId);

    /**
     * 设置 指定路由Id的 状态
     * @param routeId
     * @param enable
     * @return
     */
    String updateEnable(String routeId,boolean enable);

    /**
     * 更新指定的路由配置
     * @param entity
     * @return
     */
    String update(GatewayDynamicRoute entity);

    /**
     * 新增路由配置
     * @param entity
     * @return
     */
    String saveOne(GatewayDynamicRoute entity);

    /**
     * 批量新增路由规则
     * @param entityList
     * @return
     */
    String saveList(List<GatewayDynamicRoute> entityList);
}
