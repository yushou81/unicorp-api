package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JWT令牌视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT令牌及用户基本信息")
public class TokenVO {
    
    /**
     * JWT令牌
     */
    @Schema(description = "JWT认证令牌")
    private String token;
    
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;
    
    /**
     * 用户角色
     */
    @Schema(description = "用户角色")
    private String role;
    
    /**
     * 用户头像URL
     */
    @Schema(description = "用户头像URL")
    private String avatar;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer id;

    /**
     * 用户状态
     */
    @Schema(description = "用户状态")
    private String status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
} 