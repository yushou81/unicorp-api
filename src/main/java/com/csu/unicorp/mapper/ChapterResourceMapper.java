package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.ChapterResource;
import com.csu.unicorp.entity.CourseResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 章节资源关联Mapper接口
 */
@Mapper
public interface ChapterResourceMapper extends BaseMapper<ChapterResource> {
    
    /**
     * 查询章节关联的所有资源
     * @param chapterId 章节ID
     * @return 资源列表
     */
    @Select("SELECT r.* FROM course_resources r " +
            "INNER JOIN chapter_resources cr ON r.id = cr.resource_id " +
            "WHERE cr.chapter_id = #{chapterId} AND cr.is_deleted = 0 AND r.is_deleted = 0 " +
            "ORDER BY cr.sequence ASC")
    List<CourseResource> selectResourcesByChapterId(@Param("chapterId") Integer chapterId);
    
    /**
     * 查询章节关联的资源ID列表
     * @param chapterId 章节ID
     * @return 资源ID列表
     */
    @Select("SELECT resource_id FROM chapter_resources WHERE chapter_id = #{chapterId} AND is_deleted = 0 ORDER BY sequence ASC")
    List<Integer> selectResourceIdsByChapterId(@Param("chapterId") Integer chapterId);
    
    /**
     * 查询章节关联的资源数量
     * @param chapterId 章节ID
     * @return 资源数量
     */
    @Select("SELECT COUNT(*) FROM chapter_resources WHERE chapter_id = #{chapterId} AND is_deleted = 0")
    Integer countResourcesByChapterId(@Param("chapterId") Integer chapterId);
    
    /**
     * 查询资源关联的章节ID
     * @param resourceId 资源ID
     * @return 章节ID列表
     */
    @Select("SELECT chapter_id FROM chapter_resources WHERE resource_id = #{resourceId} AND is_deleted = 0")
    List<Integer> selectChapterIdsByResourceId(@Param("resourceId") Integer resourceId);
    
    /**
     * 更新资源在章节中的顺序
     * @param id 关联ID
     * @param sequence 新序号
     * @return 更新条数
     */
    @Update("UPDATE chapter_resources SET sequence = #{sequence} WHERE id = #{id}")
    int updateResourceSequence(@Param("id") Integer id, @Param("sequence") Integer sequence);
    
    /**
     * 查询章节资源关联的最大序号
     * @param chapterId 章节ID
     * @return 最大序号
     */
    @Select("SELECT MAX(sequence) FROM chapter_resources WHERE chapter_id = #{chapterId} AND is_deleted = 0")
    Integer selectMaxSequenceByChapterId(@Param("chapterId") Integer chapterId);
} 