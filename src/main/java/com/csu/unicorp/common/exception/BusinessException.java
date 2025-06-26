package com.csu.unicorp.common.exception;

/**
 * 业务异常类
 * 用于表示业务逻辑错误
 */
public class BusinessException extends RuntimeException {
    
    private Integer code;
    
    /**
     * 默认构造方法，错误码为400
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
    
    /**
     * 带错误码的构造方法
     * @param message 错误信息
     * @param code 错误码
     */
    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code; 
    }
    
    /**
     * 获取错误码
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }
} 