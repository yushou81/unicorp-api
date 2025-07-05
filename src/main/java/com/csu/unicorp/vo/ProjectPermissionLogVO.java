package com.csu.unicorp.vo;

import java.util.Date;
import java.util.List;

public class ProjectPermissionLogVO {
    private Integer logId;
    private Integer userId;
    private String action;
    private List<String> roles;
    private String operator;
    private Date time;
    private Date expireAt;

    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
    public Date getExpireAt() { return expireAt; }
    public void setExpireAt(Date expireAt) { this.expireAt = expireAt; }
} 