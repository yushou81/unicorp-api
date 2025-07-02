package com.csu.unicorp.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * 请求日志过滤器
 * 输出请求的路径、参数等详细信息到控制台
 */
@Component
@Slf4j
public class RequestLogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 只处理HTTP请求
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            
            // 输出请求信息
            System.out.println("\n===== 请求信息开始 =====");
            System.out.println("请求方法: " + httpRequest.getMethod());
            System.out.println("请求URL: " + httpRequest.getRequestURL());
            System.out.println("请求URI: " + httpRequest.getRequestURI());
            
            // 输出查询参数
            String queryString = httpRequest.getQueryString();
            if (queryString != null) {
                System.out.println("查询参数: " + queryString);
            }
            
            // 输出请求参数
            Map<String, String[]> parameterMap = httpRequest.getParameterMap();
            if (!parameterMap.isEmpty()) {
                System.out.println("请求参数:");
                parameterMap.forEach((key, values) -> {
                    System.out.print("  " + key + ": ");
                    for (String value : values) {
                        System.out.print(value + ", ");
                    }
                    System.out.println();
                });
            }
            
            // 输出客户端信息
            System.out.println("客户端IP: " + httpRequest.getRemoteAddr());
            System.out.println("===== 请求信息结束 =====\n");
        }
        
        // 继续过滤器链
        chain.doFilter(request, response);
    }
} 