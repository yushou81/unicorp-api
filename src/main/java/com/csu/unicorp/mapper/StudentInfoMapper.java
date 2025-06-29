package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 学生信息Mapper接口
 */
@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {
    
    /**
     * 根据用户ID查询学生信息
     * 
     * @param userId 用户ID
     * @return 学生信息
     */
    @Select("SELECT * FROM student_info WHERE user_id = #{userId}")
    StudentInfo selectByUserId(Integer userId);
} 