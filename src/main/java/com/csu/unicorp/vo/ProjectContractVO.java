// src/main/java/com/csu/unicorp/vo/ProjectContractVO.java
package com.csu.unicorp.vo;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ProjectContractVO {
    private Integer contractId;
    private Integer projectId;
    private String contractName;
    private String contractUrl;
    private String status;
    private Integer initiatorId;
    private Integer receiverId;
    private Timestamp signTime;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String remark;
}