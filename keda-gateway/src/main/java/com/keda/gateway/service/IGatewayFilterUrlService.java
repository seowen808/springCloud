package com.keda.gateway.service;

import com.keda.gateway.entity.GatewayFilterUrl;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author seowen
 * @since 2020-03-20
 */
public interface IGatewayFilterUrlService extends IService<GatewayFilterUrl> {

    /**
     * 获取指定类型的 url 地址集合
     * @param type
     * @return
     */
    List<String>  getUrl(Integer type);
}
