package com.keda.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keda.gateway.dynamic.MysqlRouteDefinitionRepository;
import com.keda.gateway.entity.GatewayDynamicRoute;
import com.keda.gateway.mapper.GatewayDynamicRouteMapper;
import com.keda.gateway.service.IGatewayDynamicRouteService;
import com.keda.util.common.BeanUtil;
import com.keda.util.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * gateway动态路由配置表 服务实现类
 * </p>
 *
 * @author seowen
 * @since 2019-12-20
 */
@Service
public class GatewayDynamicRouteServiceImpl extends ServiceImpl<GatewayDynamicRouteMapper, GatewayDynamicRoute> implements IGatewayDynamicRouteService {

    private final Logger LOGGER = LoggerFactory.getLogger(GatewayDynamicRouteServiceImpl.class);

    @Autowired
    private GatewayDynamicRouteMapper mapper;
    @Autowired
    private RedisService redisService;

    private final static long userId = 1L;

    /**
     * 获取所有 启用的 路由配置信息
     * @param enable
     * @return
     */
    @Override
    public List<GatewayDynamicRoute> getListByEnable(Boolean enable) {

        if (Objects.isNull(enable)) {
            return super.list();
        }
        QueryWrapper<GatewayDynamicRoute> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enable", enable);
        return mapper.selectList(queryWrapper);
    }

    /**
     * 禁用指定 路由Id
     * @param routeId
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String disabled(String routeId) {
        return this.updateEnable(routeId, false);
    }

    /**
     * 启用指定路由Id
     * @param routeId
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String enable(String routeId) {
        return this.updateEnable(routeId, true);
    }

    /**
     * 设置 指定路由Id的 状态
     * @param routeId
     * @param enable
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String updateEnable(String routeId, boolean enable) {
        GatewayDynamicRoute entity = new GatewayDynamicRoute();
        entity.setEnable(enable);
        return this.updateT(entity, routeId);
    }

    /**
     * 更新指定的路由配置
     * @param entity
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String update(GatewayDynamicRoute entity) {
        return this.updateT(entity, entity.getRouteId());
    }

    /**
     * 新增路由配置
     * @param entity
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String saveOne(GatewayDynamicRoute entity) {
        BeanUtil.initPojo(entity, userId, userId);
        entity.setEnable(Objects.isNull(entity.getEnable())?true:entity.getEnable());
        if (super.save(entity)) {
            /**
             * 数据库新增成功后，新增到redis中
             */
            this.saveOrUpdateToRedis(entity);
            return entity.getRouteId();
        }
        return null;
    }

    /**
     * 批量新增路由信息
     * @param entityList
     * @return
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String saveList(List<GatewayDynamicRoute> entityList) {

        if (CollectionUtils.isEmpty(entityList)){
            return null;
        }
        List<String> strList = new ArrayList<>();
        entityList.stream().forEach(entity->{
            BeanUtil.initPojo(entity, userId, userId);
            entity.setEnable(Objects.isNull(entity.getEnable())?true:entity.getEnable());
            strList.add(entity.getRouteId());
        });

        if (super.saveBatch(entityList)) {
            //添加在 redis 中
            entityList.stream().forEach(gdr->this.saveOrUpdateToRedis(gdr));
            return String.join(",",strList);
        }
        return null;
    }

    /**
     * 更新路由信息
     * @param entity
     * @param routeId
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    private String updateT(GatewayDynamicRoute entity, String routeId) {
        BeanUtil.initPojoForUpdate(entity, userId);
        entity.setEnable(Objects.isNull(entity.getEnable())?true:entity.getEnable());
        UpdateWrapper<GatewayDynamicRoute> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("route_id", routeId);
        if (super.update(entity, updateWrapper)) {
            if (entity.getEnable()) {
                GatewayDynamicRoute gdr = mapper.selectOne(updateWrapper);
                if (Objects.nonNull(gdr)) {
                    this.saveOrUpdateToRedis(gdr);
                }
            }else {
                this.delFromRedis(routeId);
            }
            return routeId;
        }
        return null;
    }

    /**
     * 新增或更新路由信息 至 redis 中
     * @param entity
     * @return
     */
    private boolean saveOrUpdateToRedis(GatewayDynamicRoute entity){
        try {
            RouteDefinition r = new MysqlRouteDefinitionRepository().setRouteDefinition(entity);
            LOGGER.info("添加或更新(saveOrUpdateToRedis)路由信息到redis,数据为{}", JSON.toJSONString(r));
            redisService.hmSet(MysqlRouteDefinitionRepository.GATEWAY_ROUTES,r.getId(),r);
            return true;
        }catch (Exception e){
            LOGGER.error("添加或更新(saveOrUpdateToRedis)路由信息到redis,发生错误，信息为{}",e.getMessage());
            return false;
        }

    }

    /**
     * 从redis中删除 路由信息
     * @param routeId
     * @return
     */
    private boolean delFromRedis(String  routeId){
        try {
            redisService.delhm(MysqlRouteDefinitionRepository.GATEWAY_ROUTES,routeId);
            LOGGER.info("删除(delFromRedis)路由信息到redis,routeId 数据为{}", routeId);
            return true;
        }catch (Exception e){
            LOGGER.error("删除(delFromRedis)路由信息到redis,发生错误，信息为{}",e.getMessage());
            return false;
        }
    }
}
