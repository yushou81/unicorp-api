package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.Job;
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
            "     j.job_category LIKE CONCAT('%', #{keyword}, '%') OR",
            "     j.skill_tags LIKE CONCAT('%', #{keyword}, '%') OR",
            "     o.organization_name LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "ORDER BY j.created_at DESC",
            "</script>"
    })
    IPage<JobVO> pageJobs(Page<JobVO> page, @Param("keyword") String keyword);
    
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
} 