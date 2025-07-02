package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.CourseQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程问答Mapper接口
 */
@Mapper
public interface CourseQuestionMapper extends BaseMapper<CourseQuestion> {
    
    /**
     * 查询课程的所有问题
     * @param courseId 课程ID
     * @param page 分页参数
     * @return 分页问题列表
     */
    @Select("SELECT * FROM course_questions WHERE course_id = #{courseId} AND is_deleted = 0 ORDER BY created_at DESC")
    IPage<CourseQuestion> selectQuestionsByCourse(@Param("courseId") Integer courseId, Page<CourseQuestion> page);
    
    /**
     * 查询章节的所有问题
     * @param chapterId 章节ID
     * @param page 分页参数
     * @return 分页问题列表
     */
    @Select("SELECT * FROM course_questions WHERE chapter_id = #{chapterId} AND is_deleted = 0 ORDER BY created_at DESC")
    IPage<CourseQuestion> selectQuestionsByChapter(@Param("chapterId") Integer chapterId, Page<CourseQuestion> page);
    
    /**
     * 查询学生在课程中提出的所有问题
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param page 分页参数
     * @return 分页问题列表
     */
    @Select("SELECT * FROM course_questions WHERE course_id = #{courseId} AND student_id = #{studentId} AND is_deleted = 0 ORDER BY created_at DESC")
    IPage<CourseQuestion> selectQuestionsByStudent(@Param("courseId") Integer courseId, 
                                                 @Param("studentId") Integer studentId,
                                                 Page<CourseQuestion> page);
    
    /**
     * 查询学生在课程中提出的所有问题（重载方法，参数顺序不同）
     * @param page 分页参数
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 分页问题列表
     */
    @Select("SELECT * FROM course_questions WHERE course_id = #{courseId} AND student_id = #{studentId} AND is_deleted = 0 ORDER BY created_at DESC")
    IPage<CourseQuestion> selectQuestionsByStudentAndCourse(Page<CourseQuestion> page,
                                                @Param("studentId") Integer studentId,
                                                @Param("courseId") Integer courseId);
    
    /**
     * 统计课程的未回答问题数量
     * @param courseId 课程ID
     * @return 未回答问题数量
     */
    @Select("SELECT COUNT(*) FROM course_questions WHERE course_id = #{courseId} AND status = 'pending' AND is_deleted = 0")
    Integer countPendingQuestionsByCourse(@Param("courseId") Integer courseId);
    
    /**
     * 统计课程的所有问题数量
     * @param courseId 课程ID
     * @return 问题总数
     */
    @Select("SELECT COUNT(*) FROM course_questions WHERE course_id = #{courseId} AND is_deleted = 0")
    Integer countQuestionsByCourse(@Param("courseId") Integer courseId);
    
    /**
     * 统计课程的已回答问题数量
     * @param courseId 课程ID
     * @return 已回答问题数量
     */
    @Select("SELECT COUNT(*) FROM course_questions WHERE course_id = #{courseId} AND status = 'answered' AND is_deleted = 0")
    Integer countAnsweredQuestionsByCourse(@Param("courseId") Integer courseId);
    
    /**
     * 统计教师或导师需要回答的问题数量
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @return 未回答问题数量
     */
    @Select("SELECT COUNT(*) FROM course_questions cq " +
            "INNER JOIN dual_teacher_courses dtc ON cq.course_id = dtc.id " +
            "WHERE dtc.id = #{courseId} AND (dtc.teacher_id = #{teacherId} OR dtc.mentor_id = #{teacherId}) " +
            "AND cq.status = 'pending' AND cq.is_deleted = 0")
    Integer countPendingQuestionsForTeacher(@Param("courseId") Integer courseId, 
                                          @Param("teacherId") Integer teacherId);
} 