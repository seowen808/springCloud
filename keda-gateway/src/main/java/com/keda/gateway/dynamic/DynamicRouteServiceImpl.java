package com.keda.gateway.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @Author: seowen
 * @Date: 2019/12/19 15:49
 * @Version 1.0
 */
@Service
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 增加路由
     * @param routeDefinition
     * @return
     */
    public String add(RouteDefinition routeDefinition){
        routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
        this.doLoad();
        return "success";
    }

    /**
     * 更新路由
     */
    public String update(RouteDefinition definition) {
        try {
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        } catch (Exception e) {
            return "update fail,not find route  routeId: " + definition.getId();
        }
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.doLoad();
            return "success";
        } catch (Exception e) {
            return "update route  fail";
        }
    }


    /**
     * 删除路由
     *
     */
    public String delete(String id) {
//        return this.routeDefinitionWriter.delete(Mono.just(id)).then(Mono.defer(()->Mono.just(ResponseEntity.ok().build())))
//                .onErrorResume(t -> t instanceof NotFoundException,t -> Mono.just(ResponseEntity.notFound().build()));
        try {
            this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
            this.doLoad();
        } catch (Exception e) {
            e.printStackTrace();
            return "delete fail,not find route  routeId: " + id;
        }
        return "delete success";
    }

    /**
     * 重新刷新 路由
     */
    public String doLoad() {
        try {
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
        }catch (Exception e){
            e.printStackTrace();
            return "load fail";
        }
        return "load success";
    }


}
