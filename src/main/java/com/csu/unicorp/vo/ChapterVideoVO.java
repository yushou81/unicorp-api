package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 章节视频视图对象
 */
@Data
@Schema(description = "章节视频视图对象")
public class ChapterVideoVO {
    
    /**
     * 视频ID
     */
    @Schema(description = "视频ID", example = "1")
    private Integer id;
    
    /**
     * 章节ID
     */
    @Schema(description = "章节ID", example = "1")
    private Integer chapterId;
    
    /**
     * 章节标题
     */
    @Schema(description = "章节标题", example = "第一章：Java基础")
    private String chapterTitle;
    
    /**
     * 视频标题
     */
    @Schema(description = "视频标题", example = "第一章：Java基础入门视频")
    private String title;
    
    /**
     * 视频描述
     */
    @Schema(description = "视频描述", example = "本视频介绍Java语言的基础知识，包括变量、数据类型、运算符等")
    private String description;
    
    /**
     * 视频文件路径
     */
    @Schema(description = "视频文件路径", example = "upload/courses/videos/chapter1_intro.mp4")
    private String filePath;
    
    /**
     * 视频文件大小(字节)
     */
    @Schema(description = "视频文件大小(字节)", example = "15728640")
    private Long fileSize;
    
    /**
     * 视频时长(秒)
     */
    @Schema(description = "视频时长(秒)", example = "600")
    private Integer duration;
    
    /**
     * 视频封面图片路径
     */
    @Schema(description = "视频封面图片路径", example = "upload/courses/videos/covers/chapter1_cover.jpg")
    private String coverImage;
    
    /**
     * 上传者ID
     */
    @Schema(description = "上传者ID", example = "5")
    private Integer uploaderId;
    
    /**
     * 上传者姓名
     */
    @Schema(description = "上传者姓名", example = "张三")
    private String uploaderName;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-06-01T10:15:30")
    private LocalDateTime createdAt;
    
    /**
     * 学生观看相关信息
     */
    @Schema(description = "观看进度(秒)", example = "120")
    private Integer watchProgress;
    
    /**
     * 是否看完
     */
    @Schema(description = "是否看完", example = "false")
    private Boolean isCompleted;
    
    /**
     * 上次观看位置(秒)
     */
    @Schema(description = "上次观看位置(秒)", example = "120")
    private Integer lastPosition;
} 