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
 * 内容标签关联实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("community_content_tag")
public class CommunityContentTag {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 内容类型：TOPIC-话题，QUESTION-问题，RESOURCE-资源
     */
    private String contentType;
    
    /**
     * 内容ID
     */
    private Long contentId;
    
    /**
     * 标签ID
     */
    private Long tagId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 