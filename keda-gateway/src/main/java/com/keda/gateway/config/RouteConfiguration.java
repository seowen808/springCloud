package com.keda.gateway.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.ProxyProvider;

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Date;

/**
 * @author: seowen
 * @date: 2019-9-10 9:52
 * @description:
 * @version:
 */
@Configuration
public class RouteConfiguration {

    //这里为支持的请求头，如果有自定义的header字段请自己添加（不能使用*）
    private static final String ALLOWED_HEADERS = "x-requested-with, authorization, Content-Type, " +
            "Authorization, credential, X-XSRF-TOKEN, token, userId, encrypt, notCheck, check,Access-Control-Allow-Origin";

    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                ServerHttpResponse response = ctx.getResponse();
                HttpHeaders headers = response.getHeaders();
                HttpHeaders requestHeaders = request.getHeaders();
                HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();

                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS);
                if (requestMethod != null) {
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
                }
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                Date date = new Date();
                headers.add(HttpHeaders.DATE,date.toString());
                headers.add(HttpHeaders.VARY,"Origin");
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }

//    @Bean
//    public HttpClient gatewayHttpClient(HttpClientProperties properties) {
//
//        // configure pool resources
//        HttpClientProperties.Pool pool = properties.getPool();
//
//        ConnectionProvider connectionProvider;
//        if (pool.getType() == HttpClientProperties.Pool.PoolType.DISABLED) {
//            connectionProvider = ConnectionProvider.newConnection();
//        }
//        else if (pool.getType() == HttpClientProperties.Pool.PoolType.FIXED) {
//            connectionProvider = ConnectionProvider.fixed(pool.getName(),
//                    pool.getMaxConnections(), pool.getAcquireTimeout(),
//                    Duration.ofSeconds(120));
//        }
//        else {
//            connectionProvider = ConnectionProvider.elastic(pool.getName(),Duration.ofSeconds(120));
//        }
//
//        HttpClient httpClient = HttpClient.create(connectionProvider)
//                .tcpConfiguration(tcpClient -> {
//
//                    if (properties.getConnectTimeout() != null) {
//                        tcpClient = tcpClient.option(
//                                ChannelOption.CONNECT_TIMEOUT_MILLIS,
//                                properties.getConnectTimeout());
//                    }
//
//                    // configure proxy if proxy host is set.
//                    HttpClientProperties.Proxy proxy = properties.getProxy();
//
//                    if (StringUtils.hasText(proxy.getHost())) {
//
//                        tcpClient = tcpClient.proxy(proxySpec -> {
//                            ProxyProvider.Builder builder = proxySpec
//                                    .type(ProxyProvider.Proxy.HTTP)
//                                    .host(proxy.getHost());
//
//                            PropertyMapper map = PropertyMapper.get();
//
//                            map.from(proxy::getPort).whenNonNull().to(builder::port);
//                            map.from(proxy::getUsername).whenHasText()
//                                    .to(builder::username);
//                            map.from(proxy::getPassword).whenHasText()
//                                    .to(password -> builder.password(s -> password));
//                            map.from(proxy::getNonProxyHostsPattern).whenHasText()
//                                    .to(builder::nonProxyHosts);
//                        });
//                    }
//                    return tcpClient;
//                });
//
//        HttpClientProperties.Ssl ssl = properties.getSsl();
//        if ((ssl.getKeyStore() != null && ssl.getKeyStore().length() > 0)
//                || ssl.getTrustedX509CertificatesForTrustManager().length > 0
//                || ssl.isUseInsecureTrustManager()) {
//            httpClient = httpClient.secure(sslContextSpec -> {
//                // configure ssl
//                SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
//
//                X509Certificate[] trustedX509Certificates = ssl
//                        .getTrustedX509CertificatesForTrustManager();
//                if (trustedX509Certificates.length > 0) {
//                    sslContextBuilder = sslContextBuilder
//                            .trustManager(trustedX509Certificates);
//                }
//                else if (ssl.isUseInsecureTrustManager()) {
//                    sslContextBuilder = sslContextBuilder
//                            .trustManager(InsecureTrustManagerFactory.INSTANCE);
//                }
//
//                try {
//                    sslContextBuilder = sslContextBuilder
//                            .keyManager(ssl.getKeyManagerFactory());
//                }
//                catch (Exception e) {
//                    logger.error(e);
//                }
//
//                sslContextSpec.sslContext(sslContextBuilder)
//                        .defaultConfiguration(ssl.getDefaultConfigurationType())
//                        .handshakeTimeout(ssl.getHandshakeTimeout())
//                        .closeNotifyFlushTimeout(ssl.getCloseNotifyFlushTimeout())
//                        .closeNotifyReadTimeout(ssl.getCloseNotifyReadTimeout());
//            });
//        }
//
//        if (properties.isWiretap()) {
//            httpClient = httpClient.wiretap(true);
//        }
//
//        return httpClient;
//    }
}
