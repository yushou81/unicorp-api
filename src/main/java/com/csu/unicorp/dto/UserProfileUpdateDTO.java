package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户个人信息更新DTO
 */
@Data
@Schema(description = "用户个人信息更新请求")
public class UserProfileUpdateDTO {
    
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称", example = "张三")
    @Size(min = 2, max = 50, message = "昵称长度必须在2-50个字符之间")
    private String nickname;
    
    /**
     * 电子邮件
     */
    @Schema(description = "电子邮件", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
} 