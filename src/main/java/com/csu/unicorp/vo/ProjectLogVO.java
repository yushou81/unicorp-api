package com.csu.unicorp.vo;

import lombok.Data;
import java.util.Date;

@Data
public class ProjectLogVO {
    private Integer logId;
    private Integer projectId;
    private String action;
    private String operator;
    private Date time;
}
