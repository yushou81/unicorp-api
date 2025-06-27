package com.csu.unicorp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 企业注册DTO
 */
@Data
public class EnterpriseRegistrationDTO {
    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空")
    private String organizationName;
    
    /**
     * 企业描述
     */
    private String description;
    
    /**
     * 企业地址
     */
    private String address;
    
    /**
     * 企业网站
     */
    private String website;
    
    /**
     * 所属行业
     */
    private String industry;
    
    /**
     * 公司规模
     */
    private String companySize;
    
    /**
     * 营业执照URL
     */
    @NotBlank(message = "营业执照URL不能为空")
    private String businessLicenseUrl;
    
    /**
     * 企业管理员昵称
     */
    private String adminNickname;
    
    /**
     * 企业管理员密码
     */
    @NotBlank(message = "管理员密码不能为空")
    private String adminPassword;
    
    /**
     * 企业管理员邮箱
     */
    @NotBlank(message = "管理员邮箱不能为空")
    @Email(message = "管理员邮箱格式不正确")
    private String adminEmail;
    
    /**
     * 企业管理员手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String adminPhone;
} 