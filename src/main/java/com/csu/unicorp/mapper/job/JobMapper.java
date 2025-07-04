package com.csu.unicorp.mapper.job;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.job.Job;
import com.csu.unicorp.vo.JobVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 岗位Mapper接口
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {
    
    /**
     * 分页查询岗位列表，包含组织名称
     *
     * @param page    分页参数
     * @param keyword 搜索关键词
     * @return 岗位列表
     */
    @Select({
            "<script>",
            "SELECT j.*, o.organization_name",
            "FROM jobs j",
            "LEFT JOIN organizations o ON j.organization_id = o.id",
            "WHERE j.is_deleted = 0 AND j.status = 'open'",
            "<if test='keyword != null and keyword != \"\"'>",
            "AND (j.title LIKE CONCAT('%', #{keyword}, '%') OR",
            "     j.description LIKE CONCAT('%', #{keyword}, '%') OR",
            "     j.location LIKE CONCAT('%', #{keyword}, '%') OR",
            "     j.tags LIKE CONCAT('%', #{keyword}, '%') OR",
            "     o.organization_name LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "ORDER BY j.created_at DESC",
            "</script>"
    })
    IPage<JobVO> pageJobs(Page<JobVO> page, @Param("keyword") String keyword);
    
    /**
     * 分页查询岗位列表，支持多条件筛选
     *
     * @param page                 分页参数
     * @param keyword              搜索关键词
     * @param location             工作地点
     * @param jobType              工作类型
     * @param educationRequirement 学历要求
     * @param salaryMin            最低薪资
     * @param salaryMax            最高薪资
     * @param sortBy               排序方式
     * @return 岗位列表
     */
    @Select({
            "<script>",
            "SELECT j.*, o.organization_name",
            "FROM jobs j",
            "LEFT JOIN organizations o ON j.organization_id = o.id",
            "WHERE j.is_deleted = 0 AND j.status = 'open'",
            "<if test='keyword != null and keyword != \"\"'>",
            "AND (j.title LIKE CONCAT('%', #{keyword}, '%') OR",
            "     j.description LIKE CONCAT('%', #{keyword}, '%') OR",
            "     j.tags LIKE CONCAT('%', #{keyword}, '%') OR",
            "     o.organization_name LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "<if test='location != null and location != \"\"'>",
            "AND j.location = #{location}",
            "</if>",
            "<if test='jobType != null and jobType != \"\"'>",
            "AND j.job_type = #{jobType}",
            "</if>",
            "<if test='educationRequirement != null and educationRequirement != \"\"'>",
            "AND j.education_requirement = #{educationRequirement}",
            "</if>",
            "<if test='salaryMin != null'>",
            "AND j.salary_min >= #{salaryMin}",
            "</if>",
            "<if test='salaryMax != null'>",
            "AND j.salary_max &lt;= #{salaryMax}",
            "</if>",
            "<choose>",
            "  <when test=\"sortBy == 'salary_asc'\">ORDER BY j.salary_min ASC</when>",
            "  <when test=\"sortBy == 'salary_desc'\">ORDER BY j.salary_max DESC</when>",
            "  <otherwise>ORDER BY j.created_at DESC</otherwise>",
            "</choose>",
            "</script>"
    })
    IPage<JobVO> pageJobsWithFilters(
            Page<JobVO> page, 
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobType") String jobType,
            @Param("educationRequirement") String educationRequirement,
            @Param("salaryMin") Integer salaryMin,
            @Param("salaryMax") Integer salaryMax,
            @Param("sortBy") String sortBy
    );
    
    /**
     * 分页查询岗位列表，支持多条件筛选，包含组织ID和发布者ID筛选
     *
     * @param page                 分页参数
     * @param keyword              搜索关键词
     * @param location             工作地点
     * @param jobType              工作类型
     * @param educationRequirement 学历要求
     * @param salaryMin            最低薪资
     * @param salaryMax            最高薪资
     * @param sortBy               排序方式
     * @param organizeId           组织ID筛选
     * @param posterId             发布者ID筛选
     * @return 岗位列表
     */
    @Select({
            "<script>",
            "SELECT j.*, o.organization_name",
            "FROM jobs j",
            "LEFT JOIN organizations o ON j.organization_id = o.id",
            "WHERE j.is_deleted = 0 AND j.status = 'open'",
            "<if test='keyword != null and keyword != \"\"'>",
            "AND (j.title LIKE CONCAT('%', #{keyword}, '%') OR",
            "     j.description LIKE CONCAT('%', #{keyword}, '%') OR",
            "     j.tags LIKE CONCAT('%', #{keyword}, '%') OR",
            "     o.organization_name LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "<if test='location != null and location != \"\"'>",
            "AND j.location = #{location}",
            "</if>",
            "<if test='jobType != null and jobType != \"\"'>",
            "AND j.job_type = #{jobType}",
            "</if>",
            "<if test='educationRequirement != null and educationRequirement != \"\"'>",
            "AND j.education_requirement = #{educationRequirement}",
            "</if>",
            "<if test='salaryMin != null'>",
            "AND j.salary_min >= #{salaryMin}",
            "</if>",
            "<if test='salaryMax != null'>",
            "AND j.salary_max &lt;= #{salaryMax}",
            "</if>",
            "<if test='organizeId != null'>",
            "AND j.organization_id = #{organizeId}",
            "</if>",
            "<if test='posterId != null'>",
            "AND j.posted_by_user_id = #{posterId}",
            "</if>",
            "<choose>",
            "  <when test=\"sortBy == 'salary_asc'\">ORDER BY j.salary_min ASC</when>",
            "  <when test=\"sortBy == 'salary_desc'\">ORDER BY j.salary_max DESC</when>",
            "  <otherwise>ORDER BY j.created_at DESC</otherwise>",
            "</choose>",
            "</script>"
    })
    IPage<JobVO> pageJobsWithAdvancedFilters(
            Page<JobVO> page, 
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobType") String jobType,
            @Param("educationRequirement") String educationRequirement,
            @Param("salaryMin") Integer salaryMin,
            @Param("salaryMax") Integer salaryMax,
            @Param("sortBy") String sortBy,
            @Param("organizeId") Integer organizeId,
            @Param("posterId") Integer posterId
    );
    
    /**
     * 根据ID查询岗位详情，包含组织名称
     *
     * @param id 岗位ID
     * @return 岗位详情
     */
    @Select("SELECT j.*, o.organization_name " +
            "FROM jobs j " +
            "LEFT JOIN organizations o ON j.organization_id = o.id " +
            "WHERE j.id = #{id} AND j.is_deleted = 0")
    JobVO getJobDetailById(@Param("id") Integer id);
    
    /**
     * 增加岗位浏览量
     *
     * @param id 岗位ID
     */
    @Update("UPDATE jobs SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Integer id);
    
    /**
     * 根据分类ID查询岗位列表
     *
     * @param page       分页参数
     * @param categoryId 分类ID
     * @return 岗位列表
     */
    @Select({
            "SELECT j.*, o.organization_name",
            "FROM jobs j",
            "LEFT JOIN organizations o ON j.organization_id = o.id",
            "JOIN job_category_relations r ON j.id = r.job_id",
            "WHERE j.is_deleted = 0 AND j.status = 'open'",
            "AND r.category_id = #{categoryId}",
            "ORDER BY j.created_at DESC"
    })
    IPage<JobVO> pageJobsByCategory(Page<JobVO> page, @Param("categoryId") Integer categoryId);
} 