package com.csu.unicorp.common.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * JWT认证异常类
 */
public class JwtAuthenticationException extends AuthenticationException {
    
    public JwtAuthenticationException(String msg) {
        super(msg);
    }
    
    public JwtAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
} 