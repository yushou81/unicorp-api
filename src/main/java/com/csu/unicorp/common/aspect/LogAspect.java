package com.csu.unicorp.common.aspect;

import com.csu.unicorp.common.annotation.Log;
import com.csu.unicorp.common.constants.LogActionType;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 操作日志切面
 * 拦截添加了@Log注解的方法，自动记录操作日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    
    private final AuditLogService auditLogService;
    
    /**
     * 定义切点 - 所有添加了@Log注解的方法
     */
    @Pointcut("@annotation(com.csu.unicorp.common.annotation.Log)")
    public void logPointCut() {}
    
    /**
     * 处理完请求后执行
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        handleLog(joinPoint, null, result);
    }
    
    /**
     * 拦截异常操作
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }
    
    /**
     * 处理日志
     */
    private void handleLog(JoinPoint joinPoint, Exception e, Object result) {
        try {
            // 获取注解信息
            Log logAnnotation = getLogAnnotation(joinPoint);
            if (logAnnotation == null) {
                return;
            }
            
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer userId = null;
            String userAccount = "";
            String userName = "";
            
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                userId = userDetails.getUserId();
                userAccount = userDetails.getUsername();
                userName = userDetails.getUser().getNickname();
            }
            
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String ip = "unknown";
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ip = getIpAddress(request);
            }
            
            // 操作结果
            String operationResult = e != null ? "失败" : "成功";
            
            // 操作详情
            String details = getDetails(joinPoint, e);
            
            // 记录日志
            auditLogService.log(
                    userId, 
                    userAccount, 
                    userName, 
                    logAnnotation.value(), 
                    logAnnotation.module(), 
                    details, 
                    operationResult, 
                    ip
            );
        } catch (Exception ex) {
            log.error("记录操作日志异常", ex);
        }
    }
    
    /**
     * 获取注解信息
     */
    private Log getLogAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(Log.class);
    }
    
    /**
     * 获取操作详情
     */
    private String getDetails(JoinPoint joinPoint, Exception e) {
        StringBuilder details = new StringBuilder();
        
        // 添加方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        details.append("方法: ").append(className).append(".").append(methodName).append("()\n");
        
        // 添加参数信息
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            details.append("参数: ");
            // 过滤掉敏感参数
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    if (args[i].toString().contains("password") || 
                        args[i].toString().contains("token")) {
                        details.append("[敏感信息已过滤]");
                    } else {
                        details.append(args[i].toString());
                    }
                    if (i < args.length - 1) {
                        details.append(", ");
                    }
                }
            }
            details.append("\n");
        }
        
        // 添加异常信息
        if (e != null) {
            details.append("异常: ").append(e.getMessage());
        }
        
        return details.toString();
    }
    
    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 