package com.csu.unicorp.service;

/**
 * 系统日志服务接口
 */
public interface SystemLogService {
    
    /**
     * 记录信息级别日志
     * 
     * @param module 模块名称
     * @param message 日志消息
     */
    void info(String module, String message);
    
    /**
     * 记录警告级别日志
     * 
     * @param module 模块名称
     * @param message 日志消息
     */
    void warning(String module, String message);
    
    /**
     * 记录错误级别日志
     * 
     * @param module 模块名称
     * @param message 日志消息
     * @param e 异常信息
     */
    void error(String module, String message, Throwable e);
} 