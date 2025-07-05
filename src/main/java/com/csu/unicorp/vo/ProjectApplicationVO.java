package com.csu.unicorp.vo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ProjectApplicationVO {
    private Integer applicationId;
    private Integer projectId;
    private String applicantType;
    private Integer applicantId;
    private String message;
    private String status;
    private String organizationName; // 申请人所属组织名
private Timestamp createTime; // 申请时间
private String projectName; // 项目名称
private String projectDescription; // 项目简介
private Timestamp approveTime; // 同意时间
private String reason;
}
