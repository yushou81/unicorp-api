package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 个人档案更新DTO
 */
@Data
@Schema(description = "个人档案更新DTO")
public class ProfileUpdateDTO {
    
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;
    
    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatar;
} 