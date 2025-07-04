package com.csu.unicorp.vo;

import lombok.Data;
import java.util.List;

@Data
public class ProjectProgressVO {
    private Integer progressId;
    private Integer projectId;
    private String stage;
    private String content;
    private List<String> attachments;
}
