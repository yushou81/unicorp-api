package com.csu.unicorp.entity.achievement;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 成果访问记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("achievement_views")
public class AchievementView {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 成果类型（portfolio、award、research）
     */
    private String achievementType;
    
    /**
     * 成果ID
     */
    private Integer achievementId;
    
    /**
     * 查看者ID，关联users表，可为空（未登录用户）
     */
    private Integer viewerId;
    
    /**
     * 查看者IP
     */
    private String viewerIp;
    
    /**
     * 查看时间
     */
    private LocalDateTime viewTime;
} 