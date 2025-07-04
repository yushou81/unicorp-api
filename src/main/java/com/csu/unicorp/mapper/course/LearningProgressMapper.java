package com.csu.unicorp.mapper.course;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.course.LearningProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学习进度Mapper接口
 */
@Mapper
public interface LearningProgressMapper extends BaseMapper<LearningProgress> {
    
    /**
     * 查询学生的章节学习进度
     * @param studentId 学生ID
     * @param chapterId 章节ID
     * @return 学习进度
     */
    @Select("SELECT * FROM learning_progress WHERE student_id = #{studentId} AND chapter_id = #{chapterId} LIMIT 1")
    LearningProgress selectProgressByStudentAndChapter(@Param("studentId") Integer studentId, 
                                                     @Param("chapterId") Integer chapterId);
    
    /**
     * 查询学生在课程中的所有章节学习进度
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 学习进度列表
     */
    @Select("SELECT lp.* FROM learning_progress lp " +
            "INNER JOIN course_chapters cc ON lp.chapter_id = cc.id " +
            "WHERE lp.student_id = #{studentId} AND cc.course_id = #{courseId}")
    List<LearningProgress> selectProgressByStudentAndCourse(@Param("studentId") Integer studentId, 
                                                          @Param("courseId") Integer courseId);
    
    /**
     * 查询学生在多个章节中的学习进度
     * @param studentId 学生ID
     * @param chapterIds 章节ID列表
     * @return 学习进度列表
     */
    @Select("<script>" +
            "SELECT * FROM learning_progress WHERE student_id = #{studentId} " +
            "AND chapter_id IN " +
            "<foreach collection='chapterIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<LearningProgress> selectProgressByStudentAndChapters(@Param("studentId") Integer studentId, 
                                                            @Param("chapterIds") List<Integer> chapterIds);
    
    /**
     * 查询章节的所有学生学习进度
     * @param chapterId 章节ID
     * @param page 分页参数
     * @return 分页学习进度列表
     */
    @Select("SELECT * FROM learning_progress WHERE chapter_id = #{chapterId}")
    IPage<LearningProgress> selectProgressByChapter(@Param("chapterId") Integer chapterId, 
                                                  Page<LearningProgress> page);
    
    /**
     * 查询章节的所有学生学习进度（参数顺序不同的重载方法）
     * @param page 分页参数
     * @param chapterId 章节ID
     * @return 分页学习进度列表
     */
    @Select("SELECT * FROM learning_progress WHERE chapter_id = #{chapterId}")
    IPage<LearningProgress> selectStudentProgressByChapter(Page<LearningProgress> page,
                                                        @Param("chapterId") Integer chapterId);
    
    /**
     * 统计章节完成学生数量
     * @param chapterId 章节ID
     * @return 完成学生数量
     */
    @Select("SELECT COUNT(*) FROM learning_progress WHERE chapter_id = #{chapterId} AND status = 'completed'")
    Integer countCompletedStudentsByChapter(@Param("chapterId") Integer chapterId);
    
    /**
     * 统计课程已完成章节数量
     * @param courseId 课程ID
     * @return 已完成章节数量
     */
    @Select("SELECT COUNT(DISTINCT lp.chapter_id) FROM learning_progress lp " +
            "INNER JOIN course_chapters cc ON lp.chapter_id = cc.id " +
            "WHERE cc.course_id = #{courseId} AND lp.status = 'completed'")
    Integer countCompletedChaptersByCourse(@Param("courseId") Integer courseId);
    
    /**
     * 统计课程进行中章节数量
     * @param courseId 课程ID
     * @return 进行中章节数量
     */
    @Select("SELECT COUNT(DISTINCT lp.chapter_id) FROM learning_progress lp " +
            "INNER JOIN course_chapters cc ON lp.chapter_id = cc.id " +
            "WHERE cc.course_id = #{courseId} AND lp.status = 'in_progress'")
    Integer countInProgressChaptersByCourse(@Param("courseId") Integer courseId);
    
    /**
     * 统计课程未开始章节数量
     * @param courseId 课程ID
     * @return 未开始章节数量
     */
    @Select("SELECT COUNT(*) FROM course_chapters cc " +
            "LEFT JOIN learning_progress lp ON cc.id = lp.chapter_id " +
            "WHERE cc.course_id = #{courseId} AND cc.is_deleted = 0 AND lp.id IS NULL")
    Integer countNotStartedChaptersByCourse(@Param("courseId") Integer courseId);
    
    /**
     * 获取课程的平均完成率
     * @param courseId 课程ID
     * @return 平均完成率(0-100)
     */
    @Select("SELECT ROUND(AVG(progress_percent)) FROM learning_progress lp " +
            "INNER JOIN course_chapters cc ON lp.chapter_id = cc.id " +
            "WHERE cc.course_id = #{courseId}")
    Integer getAverageCompletionRateByCourse(@Param("courseId") Integer courseId);
    
    /**
     * 统计学生在课程中完成的章节数量
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 完成章节数量
     */
    @Select("SELECT COUNT(*) FROM learning_progress lp " +
            "INNER JOIN course_chapters cc ON lp.chapter_id = cc.id " +
            "WHERE lp.student_id = #{studentId} AND cc.course_id = #{courseId} AND lp.status = 'completed'")
    Integer countCompletedChaptersByStudentAndCourse(@Param("studentId") Integer studentId, 
                                                   @Param("courseId") Integer courseId);
    
    /**
     * 统计课程章节完成率
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 完成率(0-100)
     */
    @Select("SELECT IFNULL(ROUND(AVG(" +
            "CASE WHEN lp.status = 'completed' THEN 100 " +
            "WHEN lp.status = 'in_progress' THEN lp.progress_percent " +
            "ELSE 0 END)), 0) AS completion_rate " +
            "FROM course_chapters cc " +
            "LEFT JOIN learning_progress lp ON cc.id = lp.chapter_id AND lp.student_id = #{studentId} " +
            "WHERE cc.course_id = #{courseId} AND cc.is_deleted = 0")
    Integer calculateCourseCompletionRate(@Param("courseId") Integer courseId, 
                                        @Param("studentId") Integer studentId);
} 