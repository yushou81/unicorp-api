package com.csu.unicorp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学生注册DTO
 */
@Data
public class StudentRegistrationDTO {
    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String account;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 电子邮件
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 所属组织ID（学校ID）
     */
    @NotNull(message = "学校ID不能为空")
    private Integer organizationId;
    
    /**
     * 实名认证的真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    /**
     * 实名认证的身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    private String idCard;
} 