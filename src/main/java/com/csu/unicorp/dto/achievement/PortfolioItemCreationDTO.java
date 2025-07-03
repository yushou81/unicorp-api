package com.csu.unicorp.dto.achievement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 作品项目创建DTO
 */
@Data
public class PortfolioItemCreationDTO {
    
    /**
     * 作品标题
     */
    @NotBlank(message = "作品标题不能为空")
    @Size(max = 100, message = "作品标题不能超过100个字符")
    private String title;
    
    /**
     * 作品描述
     */
    @Size(max = 2000, message = "作品描述不能超过2000个字符")
    private String description;
    
    /**
     * 项目链接
     */
    @Size(max = 255, message = "项目链接不能超过255个字符")
    private String projectUrl;
    
    /**
     * 封面图片URL
     */
    @Size(max = 255, message = "封面图片URL不能超过255个字符")
    private String coverImageUrl;
    
    /**
     * 作品分类
     */
    @Size(max = 50, message = "作品分类不能超过50个字符")
    private String category;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 团队成员，以逗号分隔
     */
    @Size(max = 255, message = "团队成员不能超过255个字符")
    private String teamMembers;
    
    /**
     * 是否公开
     */
    private Boolean isPublic = true;
} 