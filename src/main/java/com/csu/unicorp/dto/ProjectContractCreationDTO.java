// src/main/java/com/csu/unicorp/dto/ProjectContractCreationDTO.java
package com.csu.unicorp.dto;

import lombok.Data;

@Data
public class ProjectContractCreationDTO {
    private String contractName;
    private String contractUrl;
    private Integer receiverId;
    private String remark;
}