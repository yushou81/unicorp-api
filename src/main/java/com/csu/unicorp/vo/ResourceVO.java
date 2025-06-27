package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资源视图对象，用于返回给前端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资源信息")
public class ResourceVO {
    
    /**
     * 资源ID
     */
    @Schema(description = "资源ID")
    private Integer id;
    
    /**
     * 资源标题
     */
    @Schema(description = "资源标题")
    private String title;
    
    /**
     * 资源描述
     */
    @Schema(description = "资源描述")
    private String description;
    
    /**
     * 资源类型
     */
    @Schema(description = "资源类型，如技术文档、教学课件、案例分析等")
    private String resourceType;
    
    /**
     * 文件URL
     */
    @Schema(description = "文件URL")
    private String fileUrl;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    /**
     * 上传者昵称
     */
    @Schema(description = "上传者昵称")
    private String nickname;
    
    /**
     * 上传者所属组织名称
     */
    @Schema(description = "上传者所属组织名称")
    private String organizationName;
} 