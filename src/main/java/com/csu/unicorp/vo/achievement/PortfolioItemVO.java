package com.csu.unicorp.vo.achievement;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作品项目VO
 */
@Data
public class PortfolioItemVO {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 所属学生ID
     */
    private Integer userId;
    
    /**
     * 学生姓名
     */
    private String userName;
    
    /**
     * 学生所属组织
     */
    private String organizationName;
    
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
     * 标签列表
     */
    private List<String> tags;
    
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
     * 作品资源列表
     */
    private List<PortfolioResourceVO> resources;
} 