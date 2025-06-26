package com.csu.unicorp.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于表示业务逻辑错误
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 构造函数
     * 
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误信息
     * @param cause 原始异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 