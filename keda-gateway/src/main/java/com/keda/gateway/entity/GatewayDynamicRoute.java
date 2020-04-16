package com.keda.gateway.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.keda.bean.common.BasePojo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * gateway动态路由配置表
 * </p>
 *
 * @author seowen
 * @since 2019-12-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class GatewayDynamicRoute extends BasePojo<GatewayDynamicRoute> {

    private static final long serialVersionUID = 1L;

    /**
     * 路由Id
     */
    @TableField("route_id")
    private String routeId;

    /**
     * 路由规则转发的uri
     */
    @TableField("`uri`")
    private String uri;

    /**
     * 路由的执行顺序
     */
    @TableField("`order`")
    private Integer order;

    /**
     * 路由断言集合配置json串
     */
    @TableField("predicate_json")
    private String predicates;

    /**
     * 路由过滤器集合配置json串
     */
    @TableField("filter_json")
    private String filters;

    /**
     * 状态：0,"不可用")；1,"可用")
     */
    @TableField("`enable`")
    private Boolean enable;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
