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
 * 社区回答实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("community_answer")
public class CommunityAnswer {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 回答内容
     */
    private String content;
    
    /**
     * 回答用户ID
     */
    private Long userId;
    
    /**
     * 所属问题ID
     */
    private Long questionId;
    
    /**
     * 点赞数量
     */
    private Integer likeCount;
    
    /**
     * 是否被采纳
     */
    private Boolean isAccepted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 