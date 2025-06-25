package com.csu.linkneiapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户注册数据传输对象 (Data Transfer Object)
 * 用于接收前端传递的注册信息
 */
@Data
@Schema(description = "用户注册数据传输对象")
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Schema(description = "登录用户名", example = "chenqigang", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "登录密码", example = "password123", required = true)
    private String password;

    @Schema(description = "初始昵称 (可选)", example = "起刚")
    private String nickname;
} 