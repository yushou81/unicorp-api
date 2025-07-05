package com.csu.unicorp.entity;

import lombok.Data;
import java.util.Date;

/**
 * 项目操作日志
 */
@Data
public class ProjectLog {
    private Integer logId;
    private Integer projectId;
    private String action;
    private String operator;
    private Date time;
}
