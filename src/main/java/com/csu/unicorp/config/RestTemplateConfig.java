package com.csu.unicorp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 * 用于创建和配置RestTemplate，提供给其他服务使用
 */
@Configuration
public class RestTemplateConfig {
    
    /**
     * 创建RestTemplate Bean
     *
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }
    
    /**
     * 创建ClientHttpRequestFactory
     * 用于配置连接超时和读取超时时间
     *
     * @return ClientHttpRequestFactory实例
     */
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 连接超时设置为5秒
        factory.setConnectTimeout(5000);
        // 读取超时设置为15秒
        factory.setReadTimeout(15000);
        return factory;
    }
} 