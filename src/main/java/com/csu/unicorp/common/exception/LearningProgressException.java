package com.csu.unicorp.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 学习进度相关异常
 * 当学习进度操作过程中出现业务逻辑错误时抛出此异常
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LearningProgressException extends RuntimeException {
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public LearningProgressException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原始异常
     */
    public LearningProgressException(String message, Throwable cause) {
        super(message, cause);
    }
} 