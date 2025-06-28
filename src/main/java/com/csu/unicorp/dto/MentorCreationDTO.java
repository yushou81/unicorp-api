package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 企业导师创建DTO
 */
@Data
@Schema(description = "企业导师创建DTO")
public class MentorCreationDTO {
    
    @Schema(description = "导师邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Schema(description = "导师昵称")
    private String nickname;
    
    @Schema(description = "导师密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @Schema(description = "导师手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
} 