package com.csu.unicorp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 刷新令牌DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDTO {

    /**
     * 刷新令牌
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
} 