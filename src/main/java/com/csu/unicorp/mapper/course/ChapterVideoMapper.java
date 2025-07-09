package com.csu.unicorp.mapper.course;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.course.ChapterVideo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 章节视频Mapper接口
 */
@Mapper
public interface ChapterVideoMapper extends BaseMapper<ChapterVideo> {
    
    /**
     * 根据章节ID查询视频
     * @param chapterId 章节ID
     * @return 章节视频
     */
    ChapterVideo selectByChapterId(@Param("chapterId") Integer chapterId);
} 