package com.csu.unicorp.entity;

import java.util.Date;

public class ProjectPermissionLog {
    private Integer logId;
    private Integer projectId;
    private Integer userId;
    private String action;
    private String role;
    private String operator;
    private Date time;
    private Date expireAt;

    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }
    public Integer getProjectId() { return projectId; }
    public void setProjectId(Integer projectId) { this.projectId = projectId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
    public Date getExpireAt() { return expireAt; }
    public void setExpireAt(Date expireAt) { this.expireAt = expireAt; }
} 