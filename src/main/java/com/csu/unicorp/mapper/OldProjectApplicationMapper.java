// package com.csu.unicorp.mapper;

// import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
// import com.csu.unicorp.entity.OldProjectApplication;
// import org.apache.ibatis.annotations.Mapper;
// import org.apache.ibatis.annotations.Param;
// import org.apache.ibatis.annotations.Select;

// import java.util.List;
// import java.util.Map;

// /**
//  * 项目申请Mapper接口
//  */
// @Mapper
// public interface OldProjectApplicationMapper extends BaseMapper<OldProjectApplication> {
    
//     /**
//      * 查询指定项目的所有申请，包含申请人基本信息
//      * 
//      * @param projectId 项目ID
//      * @return 申请列表及申请人信息
//      */
//     @Select("SELECT pa.*, u.nickname, uv.real_name, sp.major " +
//             "FROM project_applications pa " +
//             "JOIN users u ON pa.user_id = u.id " +
//             "LEFT JOIN user_verifications uv ON pa.user_id = uv.user_id " +
//             "LEFT JOIN resumes sp ON pa.user_id = sp.user_id " +
//             "WHERE pa.project_id = #{projectId} AND pa.is_deleted = 0 " +
//             "ORDER BY pa.created_at DESC")
//     List<Map<String, Object>> selectApplicationsWithUserInfo(@Param("projectId") Integer projectId);
    
//     /**
//      * 分页查询指定学生的所有项目申请
//      * 
//      * @param page 分页参数
//      * @param userId 学生用户ID
//      * @return 分页结果
//      */
//     @Select("SELECT pa.id as application_id, pa.status, pa.created_at as applied_at, " +
//             "p.id as project_id, p.title as project_title, o.organization_name " +
//             "FROM project_applications pa " +
//             "JOIN projects p ON pa.project_id = p.id " +
//             "JOIN organizations o ON p.organization_id = o.id " +
//             "WHERE pa.user_id = #{userId} AND pa.is_deleted = 0 " +
//             "ORDER BY pa.created_at DESC")
//     IPage<Map<String, Object>> selectStudentApplications(Page<Map<String, Object>> page, @Param("userId") Integer userId);
// } 