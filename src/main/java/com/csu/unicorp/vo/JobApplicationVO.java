package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 投递记录视图对象
 */
@Data
@Schema(description = "岗位投递记录")
public class JobApplicationVO {

    @Schema(description = "投递记录ID")
    private Long id;
    
    @Schema(description = "岗位ID")
    private Long jobPostId;
    
    @Schema(description = "岗位标题")
    private String jobTitle;
    
    @Schema(description = "企业名称")
    private String enterpriseName;
    
    @Schema(description = "企业Logo")
    private String enterpriseLogo;
    
    @Schema(description = "投递状态: SUBMITTED-已投递, VIEWED-已查看, INTERVIEWING-面试中, OFFERED-已录用, REJECTED-不合适")
    private String status;
    
    @Schema(description = "状态中文描述")
    private String statusText;
    
    @Schema(description = "薪资范围")
    private String salaryRange;
    
    @Schema(description = "工作地点")
    private String location;
    
    @Schema(description = "投递时间")
    private LocalDateTime applicationTime;
} 