package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.CourseChapterDTO;
import com.csu.unicorp.entity.CourseChapter;
import com.csu.unicorp.vo.CourseChapterVO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * 课程章节服务接口
 */
public interface CourseChapterService {
    
    /**
     * 创建课程章节
     * @param chapterDTO 章节信息
     * @param userDetails 当前用户
     * @return 创建的章节
     */
    CourseChapterVO createChapter(CourseChapterDTO chapterDTO, UserDetails userDetails);
    
    /**
     * 更新课程章节
     * @param chapterId 章节ID
     * @param chapterDTO 章节信息
     * @param userDetails 当前用户
     * @return 更新后的章节
     */
    CourseChapterVO updateChapter(Integer chapterId, CourseChapterDTO chapterDTO, UserDetails userDetails);
    
    /**
     * 删除课程章节
     * @param chapterId 章节ID
     * @param userDetails 当前用户
     * @return 是否删除成功
     */
    boolean deleteChapter(Integer chapterId, UserDetails userDetails);
    
    /**
     * 获取课程章节详情
     * @param chapterId 章节ID
     * @param userDetails 当前用户
     * @return 章节详情
     */
    CourseChapterVO getChapterDetail(Integer chapterId, UserDetails userDetails);
    
    /**
     * 获取课程所有章节列表
     * @param courseId 课程ID
     * @param userDetails 当前用户
     * @return 章节列表
     */
    List<CourseChapterVO> getChaptersByCourse(Integer courseId, UserDetails userDetails);
    
    /**
     * 更新章节发布状态
     * @param chapterId 章节ID
     * @param isPublished 是否发布
     * @param userDetails 当前用户
     * @return 更新后的章节
     */
    CourseChapterVO updateChapterPublishStatus(Integer chapterId, Boolean isPublished, UserDetails userDetails);
    
    /**
     * 更新章节顺序
     * @param chapterId 章节ID
     * @param sequence 新顺序
     * @param userDetails 当前用户
     * @return 是否更新成功
     */
    boolean updateChapterSequence(Integer chapterId, Integer sequence, UserDetails userDetails);
    
    /**
     * 关联资源到章节
     * @param chapterId 章节ID
     * @param resourceId 资源ID
     * @param userDetails 当前用户
     * @return 是否关联成功
     */
    boolean associateResourceToChapter(Integer chapterId, Integer resourceId, UserDetails userDetails);
    
    /**
     * 从章节中移除资源
     * @param chapterId 章节ID
     * @param resourceId 资源ID
     * @param userDetails 当前用户
     * @return 是否移除成功
     */
    boolean removeResourceFromChapter(Integer chapterId, Integer resourceId, UserDetails userDetails);
    
    /**
     * 获取章节关联的资源列表
     * @param chapterId 章节ID
     * @param userDetails 当前用户
     * @return 资源列表
     */
    List<Integer> getChapterResources(Integer chapterId, UserDetails userDetails);
} 