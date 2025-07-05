package com.csu.unicorp.vo;

import lombok.Data;

@Data
public class ProjectDocumentVO {
    private Integer documentId;
    private Integer projectId;
    private String type;
    private String url;
    private String description;
}
