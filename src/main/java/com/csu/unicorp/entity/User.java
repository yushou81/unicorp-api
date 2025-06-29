package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类，对应users表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User {
    /**
     * 用户ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 登录账号，唯一
     */
    private String account;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 电子邮件
     */
    private String email;
    
    /**
     * 手机号码
     */
    private String phone;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像URL
     */
    //private String avatarUrl;
    
    /**
     * 用户状态：active-活跃，inactive-不活跃，pending_approval-等待审批
     */
    private String status;
    
    /**
     * 所属组织ID
     */
    private Integer organizationId;
    
    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Boolean isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
