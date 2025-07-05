package com.csu.unicorp.common.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.csu.unicorp.vo.ResultVO;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

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
    public ResultVO<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限异常: {}", e.getMessage());
        return ResultVO.error("权限不足");
    }
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ResultVO.error(e.getMessage());
    }
    
    /**
     * 处理课程相关异常
     */
    @ExceptionHandler(CourseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleCourseException(CourseException e) {
        log.error("课程异常: {}", e.getMessage());
        return ResultVO.error(400, e.getMessage());
    }
    
    /**
     * 处理学习进度相关异常
     */
    @ExceptionHandler(LearningProgressException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleLearningProgressException(LearningProgressException e) {
        log.error("学习进度异常: {}", e.getMessage());
        return ResultVO.error(400, e.getMessage());
    }
    
    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultVO<Void> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.error("资源不存在异常: {}", e.getMessage());
        return ResultVO.error(404, e.getMessage());
    }

    /**
     * 处理Redis连接异常
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO<Void> handleRedisConnectionFailureException(RedisConnectionFailureException e) {
        log.warn("Redis连接异常，将使用本地缓存降级: {}", e.getMessage());
        // 返回200状态码，让客户端正常处理
        return ResultVO.success("使用本地缓存降级处理");
    }

    /**
     * 处理数据完整性违规异常（如外键约束、唯一约束等）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据完整性异常: ", e);
        String message = "数据操作失败，可能是引用了不存在的记录或违反了数据约束";
        
        // 尝试提取更具体的错误信息
        String errorMessage = e.getMessage();
        if (errorMessage != null) {
            if (errorMessage.contains("foreign key constraint fails")) {
                message = "操作失败：引用了不存在的记录";
            } else if (errorMessage.contains("Duplicate entry")) {
                message = "操作失败：该记录已存在";
            }
        }
        
        return ResultVO.error(400, message);
    }
    
    /**
     * 处理请求体参数校验异常 (@Valid注解引起的异常)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMsg = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.error("参数校验异常: {}", errorMsg);
        return ResultVO.error(errorMsg);
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleBindException(BindException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMsg = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.error("绑定异常: {}", errorMsg);
        return ResultVO.error(errorMsg);
    }
    
    /**
     * 处理认证异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultVO<Void> handleAuthenticationException(BadCredentialsException e) {
        log.error("认证异常: {}", e.getMessage());
        return ResultVO.error(401, e.getMessage());
    }
    
    /**
     * 处理账户锁定异常
     */
    @ExceptionHandler(org.springframework.security.authentication.LockedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultVO<Void> handleLockedException(org.springframework.security.authentication.LockedException e) {
        log.error("账户锁定异常: {}", e.getMessage());
        return ResultVO.error(401, e.getMessage());
    }
    
    /**
     * 处理文件上传异常
     */
    @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleMultipartException(org.springframework.web.multipart.MultipartException e) {
        log.error("文件上传异常: {}", e.getMessage());
        String msg = e.getMessage();
        if (msg != null && msg.contains("Maximum upload size")) {
            return ResultVO.error("文件上传失败：文件大小超出最大限制（10MB）");
        }
        return ResultVO.error("文件上传失败：请确保请求格式为multipart/form-data，并且文件大小不超过限制");
    }
    
    /**
     * 处理运行时异常，避免返回500状态码
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return ResultVO.error(400, e.getMessage() != null ? e.getMessage() : "操作失败，请检查参数");
    }
    
    /**
     * 处理其他所有未明确处理的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ResultVO<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultVO.serverError("系统异常，请联系管理员");
    }
} 