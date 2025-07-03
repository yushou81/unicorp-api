package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.course.CourseRating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 课程评价Mapper接口
 */
@Mapper
public interface CourseRatingMapper extends BaseMapper<CourseRating> {
    
    /**
     * 分页查询课程评价
     * @param page 分页参数
     * @param courseId 课程ID
     * @return 分页结果
     */
    @Select("SELECT * FROM course_ratings WHERE course_id = #{courseId} AND is_deleted = 0 ORDER BY created_at DESC")
    IPage<CourseRating> selectPageByCourseId(Page<CourseRating> page, @Param("courseId") Integer courseId);
    
    /**
     * 查询课程评分平均值
     * @param courseId 课程ID
     * @return 平均评分
     */
    @Select("SELECT AVG(rating) FROM course_ratings WHERE course_id = #{courseId} AND is_deleted = 0")
    Double selectAvgRatingByCourseId(@Param("courseId") Integer courseId);
    
    /**
     * 检查学生是否已评价课程
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 评价数量
     */
    @Select("SELECT COUNT(*) FROM course_ratings WHERE course_id = #{courseId} AND student_id = #{studentId} AND is_deleted = 0")
    Integer countByCourseIdAndStudentId(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);
} 