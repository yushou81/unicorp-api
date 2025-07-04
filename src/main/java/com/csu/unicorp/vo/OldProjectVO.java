// package com.csu.unicorp.vo;

// import com.fasterxml.jackson.annotation.JsonFormat;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import java.time.LocalDateTime;
// import java.util.List;



// /**
//  * 项目视图对象
//  */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class OldProjectVO {
    
//     private Integer id; // 项目ID
//     private String title; // 项目名称
//     private Integer planMemberCount; // 计划人数
//     private String description; // 项目描述
//     private String difficulty; // 项目难度
//     private List<String> supportLanguages; // 支持语言
//     private List<String> techFields; // 技术领域
//     private List<String> programmingLanguages; // 编程语言
//     private String projectProposalUrl; // 项目计划书文件URL
//     private String status; // 项目状态
//     private LocalDateTime createdAt; // 创建时间
//     private String organizationName; // 组织名称（新增）
//     private Integer memberCount; // 当前已加入人数
//     private Boolean applied; // 是否已申请
//     private String applicationStatus; // 新增字段
//     private Integer applicationId;//申请id
// } 