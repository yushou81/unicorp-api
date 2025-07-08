// package com.csu.unicorp.entity;

// import com.baomidou.mybatisplus.annotation.IdType;
// import com.baomidou.mybatisplus.annotation.TableId;
// import com.baomidou.mybatisplus.annotation.TableLogic;
// import com.baomidou.mybatisplus.annotation.TableName;
// import lombok.Data;
// import lombok.Builder;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

// import java.sql.Timestamp;
// import java.time.LocalDateTime;

// /**
//  * 项目申请实体类，对应project_applications表
//  */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @TableName("project_applications")
// public class OldProjectApplication {
//     /**
//      * 申请ID，自增主键
//      */
//     @TableId(type = IdType.AUTO)
//     private Integer id;
    
//     /**
//      * 关联的项目ID
//      */
//     private Integer projectId;
    
//     /**
//      * 申请人的用户ID
//      */
//     private Integer userId;
    
//     /**
//      * 申请状态：submitted-已提交，viewed-已查看，approved-已批准，rejected-已拒绝
//      */
//     private String status;
    
//     /**
//      * 申请陈述或备注
//      */
//     private String applicationStatement;
    
//     /**
//      * 逻辑删除标志
//      */
//     @TableLogic
//     private Boolean isDeleted;
    
//     /**
//      * 创建时间
//      */
//     private Timestamp createdAt;
    
//     /**
//      * 更新时间
//      */
//     private Timestamp updatedAt;
// } 