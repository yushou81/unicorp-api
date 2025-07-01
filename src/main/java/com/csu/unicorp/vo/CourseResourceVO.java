package com.csu.unicorp.vo;

import com.csu.unicorp.entity.enums.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程资源视图对象
 */
@Data
@Schema(description = "课程资源视图对象")
public class CourseResourceVO {
    
    /**
     * 资源ID
     */
    @Schema(description = "资源ID", example = "1")
    private Integer id;
    
    /**
     * 课程ID
     */
    @Schema(description = "课程ID", example = "1")
    private Integer courseId;
    
    /**
     * 课程标题
     */
    @Schema(description = "课程标题", example = "Java企业级开发实战")
    private String courseTitle;
    
    /**
     * 资源标题
     */
    @Schema(description = "资源标题", example = "Java企业级开发实战课件")
    private String title;
    
    /**
     * 资源描述
     */
    @Schema(description = "资源描述", example = "本课件包含Java企业级开发实战的核心内容和示例代码")
    private String description;
    
    /**
     * 资源类型分类
     */
    @Schema(description = "资源类型分类", example = "DOCUMENT")
    private ResourceType resourceType;
    
    /**
     * 文件路径
     */
    @Schema(description = "文件路径", example = "courses/resources/java_enterprise.pdf")
    private String filePath;
    
    /**
     * 文件大小(字节)
     */
    @Schema(description = "文件大小(字节)", example = "2048000")
    private Long fileSize;
    
    /**
     * 文件类型
     */
    @Schema(description = "文件类型", example = "application/pdf")
    private String fileType;
    
    /**
     * 上传者ID
     */
    @Schema(description = "上传者ID", example = "5")
    private Integer uploaderId;
    
    /**
     * 上传者类型(TEACHER/MENTOR)
     */
    @Schema(description = "上传者类型", example = "TEACHER")
    private String uploaderType;
    
    /**
     * 上传者姓名
     */
    @Schema(description = "上传者姓名", example = "张三")
    private String uploaderName;
    
    /**
     * 下载次数
     */
    @Schema(description = "下载次数", example = "15")
    private Integer downloadCount;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-06-01T10:15:30")
    private LocalDateTime createdAt;
} 