package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT令牌视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT令牌")
public class TokenVO {
    
    /**
     * JWT令牌
     */
    @Schema(description = "JWT认证令牌")
    private String token;
} 