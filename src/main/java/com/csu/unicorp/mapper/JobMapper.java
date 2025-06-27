package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 岗位Mapper接口
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {
    
    /**
     * 分页查询岗位列表，支持关键词搜索
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT j.*, o.organization_name FROM jobs j " +
            "LEFT JOIN organizations o ON j.organization_id = o.id " +
            "WHERE j.is_deleted = 0 AND j.status = 'open' " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (j.title LIKE CONCAT('%', #{keyword}, '%') OR j.description LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "ORDER BY j.created_at DESC" +
            "</script>")
    IPage<Job> selectJobsWithOrgName(Page<Job> page, @Param("keyword") String keyword);
    
    /**
     * 统计符合条件的岗位总数
     * 
     * @param keyword 搜索关键词
     * @return 岗位总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM jobs j " +
            "WHERE j.is_deleted = 0 AND j.status = 'open' " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (j.title LIKE CONCAT('%', #{keyword}, '%') OR j.description LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "</script>")
    Long countJobs(@Param("keyword") String keyword);
} 