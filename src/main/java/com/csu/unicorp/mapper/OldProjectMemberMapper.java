// package com.csu.unicorp.mapper;

// import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// import com.csu.unicorp.entity.OldProjectMember;
// import org.apache.ibatis.annotations.Mapper;
// import org.apache.ibatis.annotations.Select;
// import org.apache.ibatis.annotations.Param;
// import org.apache.ibatis.annotations.Update;

// import java.util.List;
// import java.util.Map;

// /**
//  * 项目成员Mapper接口
//  */
// @Mapper
// public interface OldProjectMemberMapper extends BaseMapper<OldProjectMember> {
//         // 批量统计每个项目的成员数
//         @Select("SELECT project_id, COUNT(*) AS cnt FROM project_members WHERE is_deleted = 0 GROUP BY project_id")
//         List<Map<String, Object>> countMembersForAllProjects();

//         @Select({
//             "<script>",
//             "SELECT project_id, COUNT(*) AS cnt FROM project_members",
//             "WHERE is_deleted = 0",
//             "AND project_id IN",
//             "<foreach collection='projectIds' item='id' open='(' separator=',' close=')'>",
//             "#{id}",
//             "</foreach>",
//             "GROUP BY project_id",
//             "</script>"
//         })
//         List<Map<String, Object>> selectMemberCountByProjectIds(@Param("projectIds") List<Integer> projectIds);


//         @Update("UPDATE project_members SET is_deleted = 1 WHERE project_id = #{projectId} AND user_id = #{userId}")
//         void logicDeleteByUserId(@Param("projectId") Integer projectId,@Param("userId") Integer userId);
    
// } 