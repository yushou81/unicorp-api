package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象
 */
@Data
@Schema(description = "用户信息")
public class UserVO {
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer id;
    
    /**
     * 用户账号
     */
    @Schema(description = "用户账号", example = "zhangsan2025")
    private String account;
    
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称", example = "三哥")
    private String nickname;
    
    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱")
    private String email;
    
    /**
     * 用户手机号
     */
    @Schema(description = "用户手机号")
    private String phone;
    
    /**
     * 用户状态
     */
    @Schema(description = "用户状态")
    private String status;
    
    /**
     * 所属组织ID
     */
    @Schema(description = "所属组织ID")
    private Integer organizationId;
    
    /**
     * 用户角色列表
     */
    @Schema(description = "用户角色列表")
    private List<String> roles;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
} 