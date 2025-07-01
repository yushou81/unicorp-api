package com.csu.unicorp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.JobFavorite;
import com.csu.unicorp.vo.JobVO;

/**
 * 岗位收藏Mapper接口
 */
@Mapper
public interface JobFavoriteMapper extends BaseMapper<JobFavorite> {
    
    /**
     * 根据用户ID分页查询收藏的岗位信息
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @return 岗位信息分页结果
     */
    @Select("SELECT j.*, o.organization_name " +
            "FROM jobs j " +
            "JOIN job_favorites f ON j.id = f.job_id " +
            "JOIN organizations o ON j.organization_id = o.id " +
            "WHERE f.user_id = #{userId} AND j.is_deleted = 0 " +
            "ORDER BY f.created_at DESC")
    IPage<JobVO> selectFavoriteJobsByUserId(Page<JobVO> page, @Param("userId") Integer userId);
    
    /**
     * 检查用户是否已收藏某个岗位
     * 
     * @param userId 用户ID
     * @param jobId 岗位ID
     * @return 收藏记录数量
     */
    @Select("SELECT COUNT(*) FROM job_favorites WHERE user_id = #{userId} AND job_id = #{jobId}")
    Integer checkFavorite(@Param("userId") Integer userId, @Param("jobId") Integer jobId);
} 