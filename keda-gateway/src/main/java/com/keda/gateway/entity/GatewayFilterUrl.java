package com.keda.gateway.entity;

import com.keda.bean.common.BasePojo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author seowen
 * @since 2020-03-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class GatewayFilterUrl extends BasePojo<GatewayFilterUrl> {

    private static final long serialVersionUID = 1L;

    /**
     * 过滤的url类型，1:直接跳过的前缀，2：跳过的前缀的前提、3：在前提满足后，跳过的后缀
     */
    @TableField("filter_url_type")
    private Integer filterUrlType;

    /**
     * url地址
     */
    @TableField("filter_url")
    private String filterUrl;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
