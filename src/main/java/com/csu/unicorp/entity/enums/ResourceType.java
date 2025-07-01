package com.csu.unicorp.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 课程资源类型枚举
 */
@Getter
public enum ResourceType {
    
    /**
     * 文档类型
     */
    DOCUMENT("document", "文档"),
    
    /**
     * 视频类型
     */
    VIDEO("video", "视频"),
    
    /**
     * 代码类型
     */
    CODE("code", "代码"),
    
    /**
     * 其他类型
     */
    OTHER("other", "其他");
    
    /**
     * 数据库存储的值
     */
    @EnumValue
    @JsonValue
    private final String value;
    
    /**
     * 显示名称
     */
    private final String displayName;
    
    ResourceType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }
    
    /**
     * 根据数据库值获取枚举
     */
    public static ResourceType fromValue(String value) {
        for (ResourceType type : ResourceType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return OTHER;
    }
} 