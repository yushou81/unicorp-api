package com.csu.unicorp.dto.achievement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 科研成果创建DTO
 */
@Data
public class ResearchAchievementCreationDTO {
    
    /**
     * 成果标题
     */
    @NotBlank(message = "成果标题不能为空")
    @Size(max = 100, message = "成果标题不能超过100个字符")
    private String title;
    
    /**
     * 成果类型（如：论文、专利、项目等）
     */
    @NotBlank(message = "成果类型不能为空")
    @Size(max = 50, message = "成果类型不能超过50个字符")
    private String type;
    
    /**
     * 作者列表，以逗号分隔
     */
    @NotBlank(message = "作者列表不能为空")
    @Size(max = 255, message = "作者列表不能超过255个字符")
    private String authors;
    
    /**
     * 发表/获得日期
     */
    @Past(message = "发表/获得日期必须是过去的日期")
    private LocalDate publicationDate;
    
    /**
     * 发表机构/期刊
     */
    @Size(max = 100, message = "发表机构/期刊不能超过100个字符")
    private String publisher;
    
    /**
     * 成果描述
     */
    @Size(max = 2000, message = "成果描述不能超过2000个字符")
    private String description;
    
    /**
     * 成果文件URL
     */
    @Size(max = 255, message = "成果文件URL不能超过255个字符")
    private String fileUrl;
    
    /**
     * 封面图片URL
     */
    @Size(max = 255, message = "封面图片URL不能超过255个字符")
    private String coverImageUrl;
    
    /**
     * 是否公开
     */
    private Boolean isPublic = true;
} 