package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.CourseDiscussion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 课程讨论Mapper接口
 */
@Mapper
public interface CourseDiscussionMapper extends BaseMapper<CourseDiscussion> {
    
    /**
     * 查询课程的所有讨论（顶级讨论，不包含回复）
     * @param courseId 课程ID
     * @param page 分页参数
     * @return 分页讨论列表
     */
    @Select("SELECT * FROM course_discussions WHERE course_id = #{courseId} AND parent_id IS NULL AND is_deleted = 0 ORDER BY created_at DESC")
    IPage<CourseDiscussion> selectTopDiscussionsByCourse(@Param("courseId") Integer courseId, Page<CourseDiscussion> page);
    
    /**
     * 查询课程的所有顶级讨论（不包含回复）
     * @param page 分页参数
     * @param courseId 课程ID
     * @return 分页讨论列表
     */
    @Select("SELECT * FROM course_discussions WHERE course_id = #{courseId} AND parent_id IS NULL AND is_deleted = 0 ORDER BY created_at DESC")
    IPage<CourseDiscussion> selectTopLevelDiscussionsByCourse(Page<CourseDiscussion> page, @Param("courseId") Integer courseId);
    
    /**
     * 查询讨论的所有回复
     * @param parentId 父讨论ID
     * @return 回复列表
     */
    @Select("SELECT * FROM course_discussions WHERE parent_id = #{parentId} AND is_deleted = 0 ORDER BY created_at ASC")
    List<CourseDiscussion> selectRepliesByParentId(@Param("parentId") Integer parentId);
    
    /**
     * 逻辑删除讨论的所有回复
     * @param parentId 父讨论ID
     * @return 影响的行数
     */
    @Update("UPDATE course_discussions SET is_deleted = 1 WHERE parent_id = #{parentId}")
    int deleteRepliesByParentId(@Param("parentId") Integer parentId);
    
    /**
     * 统计讨论的回复数量
     * @param discussionId 讨论ID
     * @return 回复数量
     */
    @Select("SELECT COUNT(*) FROM course_discussions WHERE parent_id = #{discussionId} AND is_deleted = 0")
    Integer countRepliesByDiscussionId(@Param("discussionId") Integer discussionId);
    
    /**
     * 统计课程的讨论数量
     * @param courseId 课程ID
     * @return 讨论数量
     */
    @Select("SELECT COUNT(*) FROM course_discussions WHERE course_id = #{courseId} AND is_deleted = 0")
    Integer countDiscussionsByCourseId(@Param("courseId") Integer courseId);
    
    /**
     * 统计课程的讨论数量
     * @param courseId 课程ID
     * @return 讨论数量
     */
    @Select("SELECT COUNT(*) FROM course_discussions WHERE course_id = #{courseId} AND is_deleted = 0")
    Integer countDiscussionsByCourse(@Param("courseId") Integer courseId);
} 