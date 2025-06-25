package com.csu.linkneiapi.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户个人资料视图对象
 */
@Data
@Schema(description = "用户个人资料")
public class UserProfileVO {
    
    /**
     * 用户名 (不可修改)
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;
    
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称", example = "爱吃烧烤的张三")
    private String nickname;
    
    /**
     * 用户头像URL
     */
    @Schema(description = "用户头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;
    
    /**
     * 手机号码 (部分脱敏处理)
     */
    @Schema(description = "手机号码", example = "138****8888")
    private String phone;
    
    /**
     * 用户角色
     */
    @Schema(description = "用户角色", example = "USER")
    private String role;
} 