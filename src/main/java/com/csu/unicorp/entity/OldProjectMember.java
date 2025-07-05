// package com.csu.unicorp.entity;

// import com.baomidou.mybatisplus.annotation.IdType;
// import com.baomidou.mybatisplus.annotation.TableId;
// import com.baomidou.mybatisplus.annotation.TableLogic;
// import com.baomidou.mybatisplus.annotation.TableName;
// import lombok.Data;
// import lombok.Builder;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

// import java.time.LocalDateTime;

// /**
//  * 项目成员实体类，对应project_members表
//  */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @TableName("project_members")
// public class OldProjectMember {
//     /**
//      * 主键ID
//      */
//     @TableId(type = IdType.AUTO)
//     private Integer id;
    
//     /**
//      * 关联的项目ID
//      */
//     private Integer projectId;
    
//     /**
//      * 项目成员的用户ID
//      */
//     private Integer userId;
    
//     /**
//      * 成员在项目中的角色
//      */
//     private String roleInProject;
    
//     /**
//      * 逻辑删除标志
//      */
//     @TableLogic
//     private int isDeleted;
    
//     /**
//      * 创建时间
//      */
//     private LocalDateTime createdAt;
    
//     /**
//      * 更新时间
//      */
//     private LocalDateTime updatedAt;
// } 