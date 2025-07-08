// package com.csu.unicorp.mapper;

// import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
// import com.csu.unicorp.entity.Project;
// import org.apache.ibatis.annotations.Mapper;
// import org.apache.ibatis.annotations.Param;
// import org.apache.ibatis.annotations.Select;

// /**
//  * 项目Mapper接口
//  */
// @Mapper
// public interface OldProjectMapper extends BaseMapper<Project> {
    
//     /**
//      * 分页查询项目列表，支持关键词搜索
//      * 
//      * @param page 分页参数
//      * @param keyword 搜索关键词
//      * @return 分页结果
//      */
//     @Select("<script>" +
//             "SELECT p.*, o.organization_name FROM projects p " +
//             "LEFT JOIN organizations o ON p.organization_id = o.id " +
//             "WHERE p.is_deleted = 0 AND p.status = 'recruiting' " +
//             "<if test='keyword != null and keyword != \"\"'>" +
//             "AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.description LIKE CONCAT('%', #{keyword}, '%')) " +
//             "</if>" +
//             "ORDER BY p.created_at DESC" +
//             "</script>")
//     IPage<Project> selectProjectsWithOrgName(Page<Project> page, @Param("keyword") String keyword);
    
//     /**
//      * 统计符合条件的项目总数
//      * 
//      * @param keyword 搜索关键词
//      * @return 项目总数
//      */
//     @Select("<script>" +
//             "SELECT COUNT(*) FROM projects p " +
//             "WHERE p.is_deleted = 0 AND p.status = 'recruiting' " +
//             "<if test='keyword != null and keyword != \"\"'>" +
//             "AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.description LIKE CONCAT('%', #{keyword}, '%')) " +
//             "</if>" +
//             "</script>")
//     Long countProjects(@Param("keyword") String keyword);
// } 