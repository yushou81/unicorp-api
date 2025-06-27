package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
} 