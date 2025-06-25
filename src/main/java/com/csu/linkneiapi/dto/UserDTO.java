package com.csu.linkneiapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户注册数据传输对象 (Data Transfer Object)
 * 用于接收前端传递的注册信息
 */
@Data
@Schema(description = "用户注册数据传输对象")
public class UserDTO {

    @Schema(description = "用户名", example = "johndoe", required = true)
    private String username;

    @Schema(description = "密码", example = "password123", required = true)
    private String password;

    @Schema(description = "手机号码", example = "13800138000", required = true)
    private String phone;

    // 我们可以添加更多的字段，比如 email, verificationCode等
    // DTO的好处是，它与数据库实体解耦，可以根据业务需求灵活变化。
}