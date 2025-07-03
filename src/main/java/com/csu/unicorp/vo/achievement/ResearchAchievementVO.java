package com.csu.unicorp.vo.achievement;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 科研成果VO
 */
@Data
public class ResearchAchievementVO {
    
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
     * 成果标题
     */
    private String title;
    
    /**
     * 成果类型（如：论文、专利、项目等）
     */
    private String type;
    
    /**
     * 作者列表，以逗号分隔
     */
    private String authors;
    
    /**
     * 发表/获得日期
     */
    private LocalDate publicationDate;
    
    /**
     * 发表机构/期刊
     */
    private String publisher;
    
    /**
     * 成果描述
     */
    private String description;
    
    /**
     * 成果文件URL
     */
    private String fileUrl;
    
    /**
     * 封面图片URL
     */
    private String coverImageUrl;
    
    /**
     * 是否已认证
     */
    private Boolean isVerified;
    
    /**
     * 认证人ID
     */
    private Integer verifierId;
    
    /**
     * 认证人姓名
     */
    private String verifierName;
    
    /**
     * 认证日期
     */
    private LocalDateTime verifyDate;
    
    /**
     * 是否公开
     */
    private Boolean isPublic;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}