package com.csu.unicorp.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 学校创建DTO
 */
@Data
public class SchoolCreationDTO {
    /**
     * 学校名称
     */
    @NotBlank(message = "学校名称不能为空")
    private String organizationName;
    
    /**
     * 学校描述
     */
    private String description;
    
    /**
     * 学校地址
     */
    private String address;
    
    /**
     * 学校网站
     */
    private String website;
    
    /**
     * 学校logo文件（前端通过multipart/form-data上传）
     * 此字段在API中不需要填充，只用于接收上传的文件
     */
    private MultipartFile logoFile;
    
    /**
     * 学校管理员昵称
     */
    private String adminNickname;
    
    /**
     * 学校管理员密码
     */
    @NotBlank(message = "管理员密码不能为空")
    private String adminPassword;
    
    /**
     * 学校管理员邮箱
     */
    @NotBlank(message = "管理员邮箱不能为空")
    @Email(message = "管理员邮箱格式不正确")
    private String adminEmail;
} 