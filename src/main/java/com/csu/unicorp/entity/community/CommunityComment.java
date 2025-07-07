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
 * 社区评论实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("community_comment")
public class CommunityComment implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 评论用户ID
     */
    private Long userId;
    
    /**
     * 所属话题ID
     */
    private Long topicId;
    
    /**
     * 父评论ID
     */
    private Long parentId;
    
    /**
     * 点赞数量
     */
    private Integer likeCount;
    
    /**
     * 状态：NORMAL-正常，DELETED-已删除
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