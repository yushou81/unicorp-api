package com.csu.linkneiapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录数据传输对象
 */
@Data
@Schema(description = "用户登录数据传输对象")
public class LoginDTO {
    
    @Schema(description = "用户名或手机号", example = "johndoe", required = true)
    private String username;
    
    @Schema(description = "密码", example = "password123", required = true)
    private String password;
    
    @Schema(description = "登录类型: USERNAME-用户名登录, PHONE-手机号登录", example = "USERNAME", defaultValue = "USERNAME")
    private String loginType = "USERNAME"; // 默认使用用户名登录
} 