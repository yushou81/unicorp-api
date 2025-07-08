// src/main/java/com/csu/unicorp/entity/ProjectContract.java
package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.sql.Timestamp;

@Data
public class ProjectContract {
    @TableId
    private Integer contractId;
    private Integer projectId;
    private String contractName;
    private String contractUrl;
    private String status; // draft/pending/active/finished/rejected
    private Integer initiatorId;
    private Integer receiverId;
    private Timestamp signTime;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String remark;
}