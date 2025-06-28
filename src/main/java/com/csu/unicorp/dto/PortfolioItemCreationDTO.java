package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 作品集项目创建DTO
 */
@Data
@Schema(description = "作品集项目创建DTO")
public class PortfolioItemCreationDTO {
    
    /**
     * 作品或项目标题
     */
    @Schema(description = "作品或项目标题", required = true)
    @NotBlank(message = "标题不能为空")
    private String title;
    
    /**
     * 详细描述
     */
    @Schema(description = "详细描述")
    private String description;
    
    /**
     * 项目链接
     */
    @Schema(description = "项目链接 (如GitHub、在线演示地址)")
    private String projectUrl;
    
    /**
     * 封面图URL
     */
    @Schema(description = "封面图URL")
    private String coverImageUrl;
} 