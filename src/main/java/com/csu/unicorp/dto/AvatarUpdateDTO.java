package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 头像更新DTO
 */
@Data
@Schema(description = "头像更新请求")
public class AvatarUpdateDTO {
    
    /**
     * 头像URL
     */
    @NotBlank(message = "头像URL不能为空")
    @Pattern(regexp = "^https?://.*$", message = "头像URL格式不正确")
    @Schema(description = "头像文件URL", example = "http://example.com/avatars/user123.jpg")
    private String avatarUrl;
} 