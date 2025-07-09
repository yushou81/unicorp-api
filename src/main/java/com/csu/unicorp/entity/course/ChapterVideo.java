package com.csu.unicorp.entity.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 章节视频实体类
 */
@Data
@TableName("course_chapter_videos")
public class ChapterVideo {
    
    /**
     * 视频ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 关联的章节ID
     */
    private Integer chapterId;
    
    /**
     * 视频标题
     */
    private String title;
    
    /**
     * 视频描述
     */
    private String description;
    
    /**
     * 视频文件路径
     */
    private String filePath;
    
    /**
     * 视频文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 视频时长(秒)
     */
    private Integer duration;
    
    /**
     * 视频封面图片路径
     */
    private String coverImage;
    
    /**
     * 上传者ID
     */
    private Integer uploaderId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean isDeleted;
} 