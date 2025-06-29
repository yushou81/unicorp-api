package com.csu.unicorp.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作品集项目VO
 */
@Data
@Schema(description = "作品集项目VO")
public class PortfolioItemVO {
    
    /**
     * 主键ID
     */
    @Schema(description = "作品集项目ID")
    private Integer id;
    
    /**
     * 作品或项目标题
     */
    @Schema(description = "作品或项目标题")
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
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
} 