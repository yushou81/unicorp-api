package com.csu.unicorp.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectProgressDTO {
    private String stage;
    private String content;
    private List<String> attachments;
}
