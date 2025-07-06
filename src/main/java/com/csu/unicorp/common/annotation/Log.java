package com.csu.unicorp.common.annotation;

import com.csu.unicorp.common.constants.LogActionType;
import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    
    /**
     * 操作类型
     */
    LogActionType value();
    
    /**
     * 模块名称
     */
    String module() default "";
    
    /**
     * 操作描述
     */
    String description() default "";
} 