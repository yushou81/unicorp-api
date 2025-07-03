package com.csu.unicorp.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String resourceId;
    
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
    @Schema(description = "资源类型，如技术文档、教学课件、案例分析、专利、著作权等")
    private String resourceType;
    
    /**
     * 文件URL
     */
    @Schema(description = "文件URL")
    private String fileUrl;
    
    /**
     * 图片URL（用于专利、著作权等类型资源的图片展示）
     */
    @Schema(description = "图片URL，用于专利、著作权等类型资源的图片展示")
    private String imageUrl;
    
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