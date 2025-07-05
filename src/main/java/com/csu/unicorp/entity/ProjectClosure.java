package com.csu.unicorp.entity;

import lombok.Data;

/**
 * 项目结项
 */
@Data
public class ProjectClosure {
    private Integer id;
    private Integer projectId;
    private String summary;
    private String attachments; // 逗号分隔的URL
    private String status; // closed
}
