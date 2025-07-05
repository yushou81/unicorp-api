package com.csu.unicorp.vo;

import lombok.Data;
import java.util.List;

@Data
public class ProjectClosureVO {
    private Integer id;
    private Integer projectId;
    private String summary;
    private List<String> attachments;
    private String status;
}
