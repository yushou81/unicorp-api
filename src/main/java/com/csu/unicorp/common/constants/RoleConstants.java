package com.csu.unicorp.common.constants;

/**
 * 角色常量类
 * 定义系统中所有角色的常量，避免硬编码
 */
public class RoleConstants {

    /**
     * 数据库中的角色名称
     */
    public static final String DB_ROLE_STUDENT = "STUDENT";
    public static final String DB_ROLE_TEACHER = "TEACHER";
    public static final String DB_ROLE_SCHOOL_ADMIN = "SCHOOL_ADMIN";
    public static final String DB_ROLE_ENTERPRISE_ADMIN = "ENTERPRISE_ADMIN";
    public static final String DB_ROLE_ENTERPRISE_MENTOR = "MENTOR";
    public static final String DB_ROLE_SYSTEM_ADMIN = "SYSADMIN";

    /**
     * Spring Security中使用的角色名称（会自动添加ROLE_前缀）
     */
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_SCHOOL_ADMIN = "SCHOOL_ADMIN";
    public static final String ROLE_ENTERPRISE_ADMIN = "ENTERPRISE_ADMIN";
    public static final String ROLE_ENTERPRISE_MENTOR = "MENTOR";
    public static final String ROLE_SYSTEM_ADMIN = "SYSADMIN";

    /**
     * 中文角色名称（用于显示）
     */
    public static final String DISPLAY_STUDENT = "学生";
    public static final String DISPLAY_TEACHER = "教师";
    public static final String DISPLAY_SCHOOL_ADMIN = "学校管理员";
    public static final String DISPLAY_ENTERPRISE_ADMIN = "企业管理员";
    public static final String DISPLAY_ENTERPRISE_MENTOR = "企业导师";
    public static final String DISPLAY_SYSTEM_ADMIN = "系统管理员";
} 