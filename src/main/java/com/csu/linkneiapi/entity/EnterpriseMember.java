package com.csu.linkneiapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 企业成员实体类
 */
@Data
@TableName("enterprise_member")
public class EnterpriseMember {

    /**
     * 企业ID
     */
    private Long enterpriseId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 成员角色: ADMIN-企业管理员, HR-招聘人员
     */
    private String role;
    
    /**
     * 加入时间
     */
    private LocalDateTime joinTime;
    
    /**
     * 关联的用户信息（非数据库字段）
     */
    @TableField(exist = false)
    private User user;
    
    /**
     * 关联的企业信息（非数据库字段）
     */
    @TableField(exist = false)
    private Enterprise enterprise;
} 