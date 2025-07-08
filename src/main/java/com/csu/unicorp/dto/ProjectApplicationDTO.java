package com.csu.unicorp.dto;

import lombok.Data;

@Data
public class ProjectApplicationDTO {
    private String applicantType;
    private Integer applicantId;
    private String message;
    private String remark;
}
