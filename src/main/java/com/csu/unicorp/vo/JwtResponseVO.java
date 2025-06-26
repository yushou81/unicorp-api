package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JWT登录响应视图对象
 */
@Data
@AllArgsConstructor
@Schema(description = "JWT认证响应对象")
public class JwtResponseVO {
    
    /**
     * JWT令牌
     */
    @Schema(description = "JWT令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    /**
     * 令牌类型
     */
    @Schema(description = "令牌类型", example = "Bearer")
    private String type;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "johndoe")
    private String username;
} 