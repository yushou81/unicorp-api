package com.csu.linkneiapi.common.exception;

import com.csu.linkneiapi.vo.ResultVO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理JWT相关异常
     */
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultVO<?> handleJwtException(JwtException e) {
        String message = "无效的认证令牌";
        if (e instanceof ExpiredJwtException) {
            message = "认证令牌已过期，请重新登录";
        }
        return ResultVO.error(401, message);
    }
    
    /**
     * 处理Spring Security的认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultVO<?> handleAuthenticationException(AuthenticationException e) {
        String message = "认证失败";
        if (e instanceof BadCredentialsException) {
            message = "用户名或密码错误";
        } else if (e instanceof JwtAuthenticationException) {
            message = e.getMessage();
        }
        return ResultVO.error(401, message);
    }
    
    /**
     * 处理访问被拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResultVO<?> handleAccessDeniedException(AccessDeniedException e) {
        return ResultVO.error(403, "没有权限访问该资源");
    }
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ResultVO.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ResultVO.error(500, "服务器内部错误");
    }
} 