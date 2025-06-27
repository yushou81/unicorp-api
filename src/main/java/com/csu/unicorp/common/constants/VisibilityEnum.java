package com.csu.unicorp.common.constants;

/**
 * 资源可见性枚举
 */
public enum VisibilityEnum {
    /**
     * 公开可见
     */
    PUBLIC("public"),
    
    /**
     * 仅创建者可见
     */
    PRIVATE("private"),
    
    /**
     * 仅组织内可见
     */
    ORGANIZATION_ONLY("organization_only");
    
    private final String value;
    
    VisibilityEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 根据字符串值获取枚举
     * 
     * @param value 字符串值
     * @return 对应的枚举值，默认返回PUBLIC
     */
    public static VisibilityEnum fromValue(String value) {
        for (VisibilityEnum visibility : VisibilityEnum.values()) {
            if (visibility.value.equals(value)) {
                return visibility;
            }
        }
        return PUBLIC; // 默认为公开
    }
} 