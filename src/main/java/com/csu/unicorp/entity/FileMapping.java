// src/main/java/com/csu/unicorp/entity/FileMapping.java
package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
@TableName("file_mapping")
public class FileMapping {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String storedName;
    private String originalName;
    private String type;
    private Timestamp uploadTime;
}