package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.user.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 简历Mapper接口
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {
    
    /**
     * 根据用户ID查询简历
     *
     * @param userId 用户ID
     * @return 简历信息
     */
    @Select("SELECT * FROM resumes WHERE user_id = #{userId} LIMIT 1")
    Resume selectByUserId(@Param("userId") Integer userId);
} 