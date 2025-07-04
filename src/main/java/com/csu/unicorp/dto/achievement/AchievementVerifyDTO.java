package com.csu.unicorp.dto.achievement;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 成果认证请求DTO
 */
@Data
public class AchievementVerifyDTO {
    
    /**
     * 是否通过认证
     */
    @NotNull(message = "认证结果不能为空")
    private Boolean isVerified;
    
    /**
     * 拒绝原因（当不通过认证时需要提供）
     */
    @Size(max = 500, message = "拒绝原因不能超过500个字符")
    private String rejectReason;
} 