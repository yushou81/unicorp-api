package com.csu.unicorp.service;

import com.csu.unicorp.dto.ChapterVideoDTO;
import com.csu.unicorp.vo.ChapterVideoVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 章节视频服务接口
 */
public interface ChapterVideoService {
    
    /**
     * 上传视频
     * @param file 视频文件
     * @param videoDTO 视频信息
     * @param userDetails 当前用户
     * @return 视频视图对象
     * @throws IOException 文件处理异常
     */
    ChapterVideoVO uploadVideo(MultipartFile file, ChapterVideoDTO videoDTO, UserDetails userDetails) throws IOException;
    
    /**
     * 获取视频详情
     * @param videoId 视频ID
     * @param userDetails 当前用户
     * @return 视频视图对象
     */
    ChapterVideoVO getVideoById(Integer videoId, UserDetails userDetails);
    
    /**
     * 获取章节关联的视频
     * @param chapterId 章节ID
     * @param userDetails 当前用户
     * @return 视频视图对象
     */
    ChapterVideoVO getVideoByChapterId(Integer chapterId, UserDetails userDetails);
    
    /**
     * 删除视频
     * @param videoId 视频ID
     * @param userDetails 当前用户
     * @return 是否删除成功
     */
    boolean deleteVideo(Integer videoId, UserDetails userDetails);
    
    /**
     * 更新视频信息
     * @param videoId 视频ID
     * @param videoDTO 视频信息
     * @param userDetails 当前用户
     * @return 更新后的视频视图对象
     */
    ChapterVideoVO updateVideo(Integer videoId, ChapterVideoDTO videoDTO, UserDetails userDetails);
    
    /**
     * 更新观看进度
     * @param videoId 视频ID
     * @param position 当前位置(秒)
     * @param userDetails 当前用户
     * @return 是否更新成功
     */
    boolean updateWatchProgress(Integer videoId, Integer position, UserDetails userDetails);
    
    /**
     * 标记视频为已完成
     * @param videoId 视频ID
     * @param userDetails 当前用户
     * @return 是否标记成功
     */
    boolean markVideoCompleted(Integer videoId, UserDetails userDetails);
} 