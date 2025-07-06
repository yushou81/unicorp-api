package com.csu.unicorp.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2调试过滤器
 * 用于记录OAuth2相关请求的详细信息，帮助调试OAuth2流程
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OAuth2DebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 只记录OAuth2相关的请求
        String uri = request.getRequestURI();
        if (uri.contains("/oauth2/") || uri.contains("/login/oauth2")) {
            log.info("=== OAuth2 请求开始 ===");
            log.info("请求方法: {}", request.getMethod());
            log.info("请求URI: {}", uri);
            log.info("完整URL: {}", getFullURL(request));
            log.info("查询参数: {}", request.getQueryString());
            log.info("请求头: {}", getHeadersInfo(request));
            log.info("=== OAuth2 请求详情结束 ===");
        }
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (uri.contains("/oauth2/") || uri.contains("/login/oauth2")) {
                log.info("=== OAuth2 响应信息 ===");
                log.info("响应状态码: {}", response.getStatus());
                log.info("响应头: {}", getResponseHeadersInfo(response));
                log.info("=== OAuth2 响应信息结束 ===");
            }
        }
    }
    
    private String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();
        
        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }
    
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            // 避免记录敏感信息
            if (key.toLowerCase().contains("authorization") || 
                key.toLowerCase().contains("cookie")) {
                value = "[REDACTED]";
            }
            map.put(key, value);
        }
        return map;
    }
    
    private Map<String, String> getResponseHeadersInfo(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String key : headerNames) {
            String value = response.getHeader(key);
            // 避免记录敏感信息
            if (key.toLowerCase().contains("authorization") || 
                key.toLowerCase().contains("cookie")) {
                value = "[REDACTED]";
            }
            map.put(key, value);
        }
        return map;
    }
} 