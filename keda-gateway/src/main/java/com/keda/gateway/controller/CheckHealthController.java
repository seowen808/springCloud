package com.keda.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: seowen
 * @date: 2019-02-28 13:36
 * @description:
 * @version:
 */
@RestController
public class CheckHealthController {
    /**
     * 负载均衡健康检测
     */
    @GetMapping(value = "checkHealth")
    public void checkHealth() {
    }
}
