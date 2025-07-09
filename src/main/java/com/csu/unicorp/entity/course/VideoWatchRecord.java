package com.csu.unicorp.entity.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频观看记录实体类
 */
@Data
@TableName("video_watch_records")
public class VideoWatchRecord {
    
    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 视频ID
     */
    private Integer videoId;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 观看进度(秒)
     */
    private Integer watchProgress;
    
    /**
     * 是否看完
     */
    private Boolean isCompleted;
    
    /**
     * 上次观看位置(秒)
     */
    private Integer lastPosition;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 