package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 教师创建DTO
 */
@Data
@Schema(description = "教师创建DTO")
public class TeacherCreationDTO {
    
    @Schema(description = "教师邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Schema(description = "教师昵称")
    private String nickname;
    
    @Schema(description = "教师密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @Schema(description = "教师手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
} 