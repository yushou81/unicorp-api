package com.csu.unicorp.interceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.csu.unicorp.service.ResourceService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 资源图片访问权限验证拦截器
 * 确保只有有权限查看资源的用户才能访问相应的资源图片
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceImageInterceptor implements HandlerInterceptor {
    
    private final ResourceService resourceService;
    
    // 文件名格式：UUID_timestamp.ext
    private static final Pattern FILE_PATTERN = Pattern.compile("([a-f0-9]{8}_\\d{14}\\.[a-zA-Z0-9]+)");
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.debug("拦截资源图片请求: {}", requestURI);
        
        // 提取文件名
        Matcher matcher = FILE_PATTERN.matcher(requestURI);
        if (matcher.find()) {
            String filename = matcher.group(1);
            log.debug("提取的文件名: {}", filename);
            
            // 检查该文件对应的资源是否存在，以及当前用户是否有权限访问
            boolean hasAccess = resourceService.checkImageAccessPermission(filename);
            
            if (!hasAccess) {
                log.warn("用户无权访问资源图片: {}", filename);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "您没有权限访问此资源图片");
                return false;
            }
            
            log.debug("用户有权访问资源图片: {}", filename);
            return true;
        }
        
        // 如果无法提取文件名，则拒绝访问
        log.warn("无法从URI提取文件名: {}", requestURI);
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "无效的资源图片请求");
        return false;
    }
} 