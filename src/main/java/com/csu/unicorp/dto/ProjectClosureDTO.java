package com.csu.unicorp.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectClosureDTO {
    private String summary;
    private List<String> attachments;
}
