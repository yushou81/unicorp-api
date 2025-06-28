package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户密码更新DTO
 */
@Data
@Schema(description = "密码更新请求")
public class PasswordUpdateDTO {
    
    /**
     * 原密码
     */
    @Schema(description = "原密码", example = "oldPassword123")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    /**
     * 新密码
     */
    @Schema(description = "新密码", example = "newPassword123")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", 
             message = "密码必须包含至少一个小写字母、一个大写字母和一个数字")
    private String newPassword;
    
    /**
     * 确认新密码
     */
    @Schema(description = "确认新密码", example = "newPassword123")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
} 