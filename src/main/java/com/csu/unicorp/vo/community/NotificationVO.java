package com.csu.unicorp.vo.community;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 社区通知VO
 */
@Data
@Schema(description = "社区通知VO")
public class NotificationVO {
    
    @Schema(description = "通知ID")
    private Long id;
    
    @Schema(description = "接收者用户ID")
    private Long userId;
    
    @Schema(description = "发送者用户ID")
    private Long fromUserId;
    
    @Schema(description = "发送者用户名")
    private String fromUsername;
    
    @Schema(description = "发送者用户头像")
    private String fromUserAvatar;
    
    @Schema(description = "通知类型")
    private String type;
    
    @Schema(description = "通知标题")
    private String title;
    
    @Schema(description = "通知内容")
    private String content;
    
    @Schema(description = "目标ID")
    private Long targetId;
    
    @Schema(description = "目标类型")
    private String targetType;
    
    @Schema(description = "是否已读")
    private Boolean isRead;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
} 