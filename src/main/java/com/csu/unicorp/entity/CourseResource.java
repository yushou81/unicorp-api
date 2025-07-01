package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程资源实体类
 */
@Data
@TableName("course_resources")
public class CourseResource {
    
    /**
     * 资源ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 课程ID
     */
    private Integer courseId;
    
    /**
     * 资源标题
     */
    private String title;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 上传者ID
     */
    private Integer uploaderId;
    
    /**
     * 上传者类型(TEACHER/MENTOR)
     */
    private String uploaderType;
    
    /**
     * 下载次数
     */
    private Integer downloadCount;
    
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