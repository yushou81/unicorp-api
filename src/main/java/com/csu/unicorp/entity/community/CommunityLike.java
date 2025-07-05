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
 * 社区点赞实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("community_like")
public class CommunityLike implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
     * 内容类型：TOPIC-话题，COMMENT-评论，QUESTION-问题，ANSWER-回答
     */
    private String contentType;
    
    /**
     * 内容ID
     */
    private Long contentId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 