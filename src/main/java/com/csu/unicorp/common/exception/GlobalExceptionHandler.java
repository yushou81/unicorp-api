package com.csu.unicorp.common.exception;

import com.csu.unicorp.vo.ResultVO;
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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 用于统一处理系统中抛出的各类异常，并转换为统一的响应格式
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
     * 处理请求体参数校验异常 (@Valid注解引起的异常)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
    public ResultVO<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        String errorMsg = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
                
        return ResultVO.error(422, "输入数据验证失败: " + errorMsg);
    }
    
    /**
     * 处理路径参数、请求参数校验异常 (@Validated注解引起的异常)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ResultVO<?> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMsg = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
                
        return ResultVO.error(400, "请求参数验证失败: " + errorMsg);
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ResultVO<?> handleBindException(BindException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
                
        return ResultVO.error(400, "参数绑定失败: " + errorMsg);
    }
    
    /**
     * 处理其他所有未明确处理的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ResultVO<?> handleAllUncaughtException(Exception ex) {
        return ResultVO.error(500, "服务器内部错误: " + ex.getMessage());
    }
} 