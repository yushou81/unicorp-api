package com.csu.unicorp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织成员创建DTO
 * 用于学校管理员创建教师账号和企业管理员创建企业导师账号
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgMemberCreationDTO {
    
    /**
     * 成员邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 成员昵称
     */
    private String nickname;
    
    /**
     * 成员密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 成员手机号
     */
    private String phone;
} 