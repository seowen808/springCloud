package com.keda.gateway.struts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.keda.gateway.entity.DynamicRouteVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.integration.history.MessageHistory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author seowen
 * @since 2019/7/17
 */
public class StringToMapMapper implements Serializable {


    public Map<String,String> toMap(String args) {
        Map<String,String> map=new HashMap<>(2);
        if (StringUtils.isEmpty(args)){
            return map;
        }
        String[] strings = args.split(",");
        int i =0;
        for (String str:strings){
            map.put(NameUtils.generateName(i),str);
            i++;
        }
        return map;
    }

    public String toString(Map<String,String> map) {
       if (MapUtils.isEmpty(map)){
           return null;
       }
        List<String> listStr = new ArrayList<>();
       for (Map.Entry<String,String> entry:map.entrySet()){
           listStr.add(entry.getValue());
       }
       return String.join(",",listStr);
    }


    public List<DynamicRouteVo.FilterDefinitionVo> toFList(String filters) {

        if (StringUtils.isEmpty(filters)){
            return null;
        }
        return JSONArray.parseArray(filters,DynamicRouteVo.FilterDefinitionVo.class);
    }

    public String toFString(List<DynamicRouteVo.FilterDefinitionVo> filters) {
        if (CollectionUtils.isEmpty(filters)){
            return null;
        }

        return JSON.toJSONString(filters);
    }

    public List<DynamicRouteVo.PredicateDefinitionVo> toPList(String predicates) {

        if (StringUtils.isEmpty(predicates)){
            return null;
        }
        return JSONArray.parseArray(predicates,DynamicRouteVo.PredicateDefinitionVo.class);
    }

    public String toPString(List<DynamicRouteVo.PredicateDefinitionVo> predicates) {
        if (CollectionUtils.isEmpty(predicates)){
            return null;
        }

        return JSON.toJSONString(predicates);
    }

}
