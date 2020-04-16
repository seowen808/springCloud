package com.keda.gateway.config;

import avro.shaded.com.google.common.collect.Lists;
import cn.hutool.core.lang.UUID;
import com.keda.gateway.entity.GatewayFilterUrl;
import com.keda.gateway.service.IGatewayFilterUrlService;
import com.keda.gateway.service.impl.GatewayFilterUrlServiceImpl;
import com.keda.util.oss.OssClientUtil;
import com.keda.util.redis.RedisService;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: seowen
 * @Date: 2020/3/4 10:41
 * @Version 1.0
 */

@Data
@Configuration
@Component
public class TokenFilter  implements GlobalFilter, Ordered {

    private final static Logger LOGGER = LoggerFactory.getLogger(TokenFilter.class);
    @Autowired
    private RedisService redisService;
    @Autowired
    private IGatewayFilterUrlService service;

    private static TokenFilter tokenFilter;

    private static final String skipKey="/getRsaPrivateKey";
    private static final String skipLogin="/login";
    private static final String app="/main/app";
    private static final String admin="/main/admin";

    private static final String TOKEN="token";
    private static final String GATEWAY_TOKEN="gateway_token_";
    private static final String GATEWAY_TOKEN_IP="gateway_token_ip";
    private static final String TOKEN_TIME_OFFICE="token_time_office";
    private static final String TOKEN_TIME_USER="token_time_user";

    private static final List<String> againSkipUrk= new ArrayList<>();
    private static final List<String> sunSkipTokenUrls= new ArrayList<>();
    private static final List<String> skipTokenUrls= new ArrayList<>();

    @PostConstruct
    public void init() {
        tokenFilter = this;
        skipTokenUrls.addAll(service.getUrl(1));
        againSkipUrk.addAll(service.getUrl(2));
        sunSkipTokenUrls.addAll(service.getUrl(3));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String notCheck = exchange.getRequest().getHeaders().getFirst("notCheck");
        if (StringUtils.isEmpty(notCheck) || "false".equals(notCheck)) {
            String url = exchange.getRequest().getURI().getPath();
            if (this.skipTokenO(url)){
                return chain.filter(exchange);
            } else if (this.skipToken(url)) {
                if (url.endsWith(skipLogin)) {
                    String tk;
                    String ip = this.getIpAddress( exchange.getRequest());
                    if (url.startsWith(admin)) {
                        tk = "+" + UUID.randomUUID().toString().replace("-", "");
                        LOGGER.info("admin登录请求，生成的token为{},登录Ip 源为{}", tk,ip);
                    } else {
                        tk = "-" + UUID.randomUUID().toString().replace("-", "");
                        LOGGER.info("app登录请求，生成的token为{},登录Ip 源为{}", tk,ip);
                    }
                    if (StringUtils.isNotEmpty(ip)) {
                        this.setTokenIp(tk, ip);
                    }
                    return chain.filter(this.build(exchange, tk));
                } else {
         //           LOGGER.info("gateway 过滤结束，时间为{},线程Id为{}", System.currentTimeMillis(), Thread.currentThread().getId());
                    return chain.filter(exchange);
                }
            }
            String userId = exchange.getRequest().getHeaders().getFirst("userId");
            String token = exchange.getRequest().getHeaders().getFirst(TOKEN);
            String ip = this.getIpAddress( exchange.getRequest());
            Object oldIp = this.getTokenIp(token);
            LOGGER.info("请求的userId为{},token为{},请求源Ip为{}，上一次请求Ip为{}", userId,token,ip,oldIp);
            //如果 wei null ,返回401
            if (StringUtils.isEmpty(token) || StringUtils.isEmpty(userId)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }else {
                String newToken = this.checkToken(userId, token);
                LOGGER.info("userId为{},token为{},的验证结果为{}", userId,token,newToken);
                if ("401".equals(newToken)){
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }else if ("403".equals(newToken)){
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            }
            exchange = this.build(exchange, token);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 是否满足跳过 验证token，前提条件
     * @param url
     * @return true: 跳过
     */
    public boolean skipToken(String url){
        if (againSkipUrk.parallelStream().anyMatch(s->url.startsWith(s))){
            return sunSkipTokenUrls.parallelStream().anyMatch(s->url.endsWith(s));
        }
        return false;
    }
    /**
     * 是否 完全 跳过 验证token
     * @param url
     * @return true: 跳过
     */
    public boolean skipTokenO(String url){
          return skipTokenUrls.parallelStream().anyMatch(s->url.startsWith(s));
    }

    /**
     * 检查token 时效性
     * @param userId
     * @param tokenId
     */
    private String checkToken(String userId, String tokenId){

        Object ob = this.redisService.hmGet(GATEWAY_TOKEN, userId);
        LOGGER.info("userId为{},上一次token为{},请求带来的token为{}", userId,ob,tokenId);
        try {
            if (Objects.isNull(ob) || StringUtils.isEmpty((String) ob)) {
                return "401";
            } else {
                try {
                    // 如果不同，代表 该用户，在其他地方又登陆了，造成原有的token 失效了
                    return ob.equals(tokenId)?tokenId:"403";
                }catch (Exception e){
                    return "401";
                }
            }
        }catch (Exception e){
            LOGGER.error("验证token 异常{}",e);
            return "401";
        }

    }
    private ServerWebExchange build(ServerWebExchange exchange,String tk){
        ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> {
            if (StringUtils.isNotEmpty(httpHeaders.getFirst(TOKEN))){
                httpHeaders.remove(TOKEN);
            }
            httpHeaders.add(TOKEN, tk);
        }).build();
        return exchange.mutate().request(request).build();
    }
    public String getIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return ip;
    }

    private boolean setTokenIp(String tokenId,String ip){
        try {
            redisService.hmSet(GATEWAY_TOKEN_IP,tokenId,ip,1200L);
            return true;
        }catch (Exception e){
            LOGGER.error("gateway redis set getTokenIp exception",e);
            // 就算异常，也往redis set token, 保证 用户能顺利登录
            redisService.hmSet(GATEWAY_TOKEN_IP,tokenId,ip,1200L);
            return false;
        }
    }

    private Object getTokenIp(String tokenId){
        if (StringUtils.isEmpty(tokenId)){
            return null;
        }
        try {

            return redisService.hmGet(GATEWAY_TOKEN_IP,tokenId);
        }catch (Exception e){
            LOGGER.error("gateway redis get getTokenIp exception",e);
            // 就算异常，也往redis set token, 保证 用户能顺利登录
            return redisService.hmGet(GATEWAY_TOKEN_IP,tokenId);
        }
    }
}
