package com.csu.unicorp.entity.achievement;

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
 * 作品项目实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("portfolio_items")
public class PortfolioItem {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 所属学生ID
     */
    private Integer userId;
    
    /**
     * 作品标题
     */
    private String title;
    
    /**
     * 作品描述
     */
    private String description;
    
    /**
     * 项目链接
     */
    private String projectUrl;
    
    /**
     * 封面图片URL
     */
    private String coverImageUrl;
    
    /**
     * 作品分类
     */
    private String category;
    
    /**
     * 标签，以逗号分隔
     */
    private String tags;
    
    /**
     * 团队成员，以逗号分隔
     */
    private String teamMembers;
    
    /**
     * 是否公开
     */
    private Boolean isPublic;
    
    /**
     * 查看次数
     */
    private Integer viewCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否已删除
     */
    @TableLogic
    private Boolean isDeleted;
} 