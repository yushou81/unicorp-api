package com.csu.unicorp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录凭证DTO
 */
@Data
public class LoginCredentialsDTO {
    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
} 