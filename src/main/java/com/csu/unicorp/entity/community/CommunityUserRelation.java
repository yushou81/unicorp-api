package com.csu.unicorp.entity.community;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社区用户关系实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("community_user_relation")
public class CommunityUserRelation {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 目标用户ID
     */
    private Long targetId;
    
    /**
     * 关系类型：FOLLOW-关注，BLOCK-拉黑
     */
    private String relationType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 