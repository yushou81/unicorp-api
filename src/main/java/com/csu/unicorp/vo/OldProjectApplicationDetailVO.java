//package com.csu.unicorp.vo;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//
///**
// * 项目申请详情视图对象
// */
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ProjectApplicationDetailVO {
//
//    /**
//     * 申请ID
//     */
//    private Integer id;
//
//    /**
//     * 项目ID
//     */
//    private Integer projectId;
//
//    /**
//     * 用户ID
//     */
//    private Integer userId;
//
//    /**
//     * 申请状态
//     */
//    private String status;
//
//    /**
//     * 申请陈述或备注
//     */
//    private String applicationStatement;
//
//    /**
//     * 申请时间
//     */
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    private Timestamp createdAt;
//
//    /**
//     * 申请人资料
//     */
//    private ApplicantProfileVO applicantProfile;
//
//    /**
//     * 申请人资料内部类
//     */
//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class ApplicantProfileVO {
//        /**
//         * 昵称
//         */
//        private String nickname;
//
//        /**
//         * 真实姓名
//         */
//        private String realName;
//
//        /**
//         * 专业
//         */
//        private String major;
//    }
//}