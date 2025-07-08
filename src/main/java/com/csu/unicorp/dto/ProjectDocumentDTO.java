package com.csu.unicorp.dto;

import lombok.Data;

@Data
public class ProjectDocumentDTO {
    private String type; // contract/report/other
    private String url;
    private String description;
}
