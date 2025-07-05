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
 * 社区话题实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("community_topic")
public class CommunityTopic {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 话题标题
     */
    private String title;
    
    /**
     * 话题内容
     */
    private String content;
    
    /**
     * 发布用户ID
     */
    private Long userId;
    
    /**
     * 所属板块ID
     */
    private Long categoryId;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 评论数量
     */
    private Integer commentCount;
    
    /**
     * 点赞数量
     */
    private Integer likeCount;
    
    /**
     * 是否置顶
     */
    private Boolean isSticky;
    
    /**
     * 是否精华
     */
    private Boolean isEssence;
    
    /**
     * 状态：NORMAL-正常，PENDING-待审核，DELETED-已删除
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