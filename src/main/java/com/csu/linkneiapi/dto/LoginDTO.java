package com.csu.linkneiapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录数据传输对象
 */
@Data
@Schema(description = "用户登录数据传输对象")
public class LoginDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "chenqigang", required = true)
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "password123", required = true)
    private String password;
} 