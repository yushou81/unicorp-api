package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 学生档案Mapper接口
 */
@Mapper
public interface StudentProfileMapper extends BaseMapper<StudentProfile> {
    
    /**
     * 根据用户ID查询学生档案
     * 
     * @param userId 用户ID
     * @return 学生档案
     */
    @Select("SELECT * FROM student_profiles WHERE user_id = #{userId}")
    StudentProfile selectByUserId(@Param("userId") Integer userId);
} 