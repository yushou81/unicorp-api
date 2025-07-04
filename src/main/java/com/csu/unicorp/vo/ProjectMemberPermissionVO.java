package com.csu.unicorp.vo;

import java.util.Date;
import java.util.List;

public class ProjectMemberPermissionVO {
    private Integer userId;
    private List<String> roles;
    private Date expireAt;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public Date getExpireAt() { return expireAt; }
    public void setExpireAt(Date expireAt) { this.expireAt = expireAt; }
} 