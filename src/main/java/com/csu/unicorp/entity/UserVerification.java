package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实名认证实体类，对应user_verifications表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_verifications")
public class UserVerification {
    /**
     * 用户ID，主键
     */
    @TableId
    private Integer userId;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 身份证号（应加密存储）
     */
    private String idCard;
    
    /**
     * 认证状态: unverified-未认证, pending-认证中, verified-已认证, failed-认证失败
     */
    private String verificationStatus;
    
    /**
     * 认证通过时间
     */
    private LocalDateTime verifiedAt;
} 