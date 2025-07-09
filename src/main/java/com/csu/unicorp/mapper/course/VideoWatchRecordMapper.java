package com.csu.unicorp.mapper.course;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.course.VideoWatchRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 视频观看记录Mapper接口
 */
@Mapper
public interface VideoWatchRecordMapper extends BaseMapper<VideoWatchRecord> {
    
    /**
     * 根据视频ID和用户ID查询观看记录
     * @param videoId 视频ID
     * @param userId 用户ID
     * @return 观看记录
     */
    VideoWatchRecord selectByVideoAndUser(@Param("videoId") Integer videoId, @Param("userId") Integer userId);
} 