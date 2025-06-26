package com.csu.unicorp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 登录凭证DTO
 */
@Data
public class LoginCredentialsDTO {
    /**
     * 登录类型：account-账号、email-邮箱、phone-手机号
     */
    @NotNull(message = "登录类型不能为空")
    private String loginType;
    
    /**
     * 登录凭证（根据loginType对应账号、邮箱或手机号）
     */
    @NotBlank(message = "登录凭证不能为空")
    private String principal;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
} 