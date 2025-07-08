package com.csu.unicorp.entity;

import lombok.Data;

/**
 * 项目资料/合同
 */
@Data
public class ProjectDocument {
    private Integer documentId;
    private Integer projectId;
    private String type; // contract/report/other
    private String url;
    private String description;
}
