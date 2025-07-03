package com.csu.unicorp.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 课程相关异常
 * 当课程操作过程中出现业务逻辑错误时抛出此异常
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CourseException extends RuntimeException {
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public CourseException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原始异常
     */
    public CourseException(String message, Throwable cause) {
        super(message, cause);
    }
} 