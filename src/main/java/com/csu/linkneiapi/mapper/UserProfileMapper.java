package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.linkneiapi.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户简历/档案数据访问接口
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
    
    /**
     * 根据用户ID查询简历信息
     * @param userId 用户ID
     * @return 简历信息
     */
    @Select("SELECT * FROM user_profile WHERE user_id = #{userId}")
    UserProfile findByUserId(@Param("userId") Long userId);
} 