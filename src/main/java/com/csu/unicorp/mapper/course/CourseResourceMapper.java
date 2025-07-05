package com.csu.unicorp.mapper.course;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.course.CourseResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 课程资源Mapper接口
 */
@Mapper
public interface CourseResourceMapper extends BaseMapper<CourseResource> {
    
    /**
     * 分页查询课程资源
     * @param page 分页参数
     * @param courseId 课程ID
     * @return 分页结果
     */
    @Select("SELECT * FROM course_resources WHERE course_id = #{courseId} AND is_deleted = 0 ORDER BY created_at DESC")
    IPage<CourseResource> selectPageByCourseId(Page<CourseResource> page, @Param("courseId") Integer courseId);
    
    /**
     * 增加资源下载次数
     * @param resourceId 资源ID
     * @return 影响行数
     */
    @Update("UPDATE course_resources SET download_count = download_count + 1 WHERE id = #{resourceId}")
    int incrementDownloadCount(@Param("resourceId") Integer resourceId);
} 