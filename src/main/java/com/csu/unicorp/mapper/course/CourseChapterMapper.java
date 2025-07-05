package com.csu.unicorp.mapper.course;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.course.CourseChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 课程章节Mapper接口
 */
@Mapper
public interface CourseChapterMapper extends BaseMapper<CourseChapter> {
    
    /**
     * 查询课程的所有章节，按顺序排列
     * @param courseId 课程ID
     * @return 章节列表
     */
    @Select("SELECT * FROM course_chapters WHERE course_id = #{courseId} AND is_deleted = 0 ORDER BY sequence ASC")
    List<CourseChapter> selectChaptersByCourseId(@Param("courseId") Integer courseId);
    
    /**
     * 查询课程的已发布章节，按顺序排列
     * @param courseId 课程ID
     * @return 已发布章节列表
     */
    @Select("SELECT * FROM course_chapters WHERE course_id = #{courseId} AND is_published = 1 AND is_deleted = 0 ORDER BY sequence ASC")
    List<CourseChapter> selectPublishedChaptersByCourseId(@Param("courseId") Integer courseId);
    
    /**
     * 查询课程章节的最大序号
     * @param courseId 课程ID
     * @return 最大序号
     */
    @Select("SELECT MAX(sequence) FROM course_chapters WHERE course_id = #{courseId} AND is_deleted = 0")
    Integer selectMaxSequenceByCourseId(@Param("courseId") Integer courseId);
    
    /**
     * 更新章节序号
     * @param chapterId 章节ID
     * @param sequence 新序号
     * @return 更新条数
     */
    @Update("UPDATE course_chapters SET sequence = #{sequence} WHERE id = #{chapterId}")
    int updateChapterSequence(@Param("chapterId") Integer chapterId, @Param("sequence") Integer sequence);
    
    /**
     * 统计已完成该章节的学生人数
     * @param chapterId 章节ID
     * @return 完成人数
     */
    @Select("SELECT COUNT(*) FROM learning_progress WHERE chapter_id = #{chapterId} AND status = 'completed'")
    Integer countStudentsCompletedChapter(@Param("chapterId") Integer chapterId);
    
    /**
     * 统计课程的总学生人数（已报名人数）
     * @param courseId 课程ID
     * @return 学生总数
     */
    @Select("SELECT COUNT(*) FROM course_enrollments WHERE course_id = #{courseId} AND status = 'enrolled'")
    Integer countStudentsInCourse(@Param("courseId") Integer courseId);
} 