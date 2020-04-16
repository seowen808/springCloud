package com.keda.gateway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.keda.gateway.entity.GatewayFilterUrl;
import com.keda.gateway.mapper.GatewayFilterUrlMapper;
import com.keda.gateway.service.IGatewayFilterUrlService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author seowen
 * @since 2020-03-20
 */
@Service
public class GatewayFilterUrlServiceImpl extends ServiceImpl<GatewayFilterUrlMapper, GatewayFilterUrl> implements IGatewayFilterUrlService {

    @Autowired
    private GatewayFilterUrlMapper mapper;
    /**
     * 获取指定类型的 url 地址集合
     * @param type
     * @return
     */
    public List<String> getUrl(Integer type){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("filter_url_type",type);
        List<GatewayFilterUrl> list=super.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)){
            return list.stream().map(u->u.getFilterUrl()).collect(Collectors.toList());
        }
        return null;
    }
}
