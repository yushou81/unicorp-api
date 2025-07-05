package com.csu.unicorp.entity;

import lombok.Data;

/**
 * 项目进度
 */
@Data
public class ProjectProgress {
    private Integer progressId;
    private Integer projectId;
    private String stage;
    private String content;
    private String attachments; // 逗号分隔的URL
}
