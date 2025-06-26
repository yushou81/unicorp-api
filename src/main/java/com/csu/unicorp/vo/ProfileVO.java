package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "组合了用户核心信息和求职档案的视图对象")
public class ProfileVO {

    // 来自用户表的字段
    @Schema(description = "用户名 (不可修改)")
    private String username;
    
    @Schema(description = "用户昵称")
    private String nickname;
    
    @Schema(description = "用户头像的完整URL")
    private String avatarUrl;
    
    @Schema(description = "手机号码 (后端已脱敏)")
    private String phone;
    
    // 来自用户个人档案表的字段
    @Schema(description = "真实姓名")
    private String fullName;
    
    @Schema(description = "最高学历")
    private String educationLevel;
    
    @Schema(description = "毕业院校")
    private String university;
    
    @Schema(description = "所学专业")
    private String major;
    
    @Schema(description = "毕业年份")
    private Integer graduationYear;
    
    @Schema(description = "期望职位")
    private String targetJobTitle;
    
    @Schema(description = "自我介绍")
    private String selfIntroduction;
} 