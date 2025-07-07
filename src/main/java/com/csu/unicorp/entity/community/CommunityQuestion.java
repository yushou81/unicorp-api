package com.csu.unicorp.entity.community;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社区问题实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("community_question")
public class CommunityQuestion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 问题标题
     */
    private String title;
    
    /**
     * 问题描述
     */
    private String content;
    
    /**
     * 提问用户ID
     */
    private Long userId;
    
    /**
     * 所属分类ID
     */
    private Long categoryId;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 回答数量
     */
    private Integer answerCount;
    
    /**
     * 悬赏积分
     */
    private Integer bountyPoints;
    
    /**
     * 最佳答案ID
     */
    private Long bestAnswerId;
    
    /**
     * 状态：UNSOLVED-未解决，SOLVED-已解决，CLOSED-已关闭
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 