package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户基本信息更新DTO
 */
@Data
@Schema(description = "用户基本信息更新DTO")
public class UserUpdateDTO {
    
    /**
     * 用户昵称
     */
    @Size(min = 2, max = 50, message = "昵称长度应在2-50个字符之间")
    @Schema(description = "用户昵称", example = "张三")
    private String nickname;
    
    /**
     * 电子邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Schema(description = "电子邮箱", example = "zhangsan@example.com")
    private String email;
    
    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;
} 