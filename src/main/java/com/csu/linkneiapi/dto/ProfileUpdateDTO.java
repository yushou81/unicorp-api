package com.csu.linkneiapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户个人资料更新数据传输对象
 */
@Data
@Schema(description = "用户个人资料更新DTO")
public class ProfileUpdateDTO {
    
    /**
     * 新的用户昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "新的用户昵称", example = "爱吃火锅的张三")
    private String nickname;
    
    /**
     * 新的用户头像URL
     */
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    @Schema(description = "新的用户头像URL", example = "https://example.com/new_avatar.jpg")
    private String avatarUrl;
    
    /**
     * 新的手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "新的手机号码", example = "13999999999")
    private String phone;
} 