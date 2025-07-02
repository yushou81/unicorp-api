package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.LearningProgressDTO;
import com.csu.unicorp.vo.LearningProgressVO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

/**
 * 学习进度服务接口
 */
public interface LearningProgressService {
    
    /**
     * 更新学习进度
     * @param progressDTO 进度信息
     * @param userDetails 当前用户
     * @return 更新后的进度
     */
    LearningProgressVO updateProgress(LearningProgressDTO progressDTO, UserDetails userDetails);
    
    /**
     * 获取学生在章节中的学习进度
     * @param chapterId 章节ID
     * @param studentId 学生ID
     * @param userDetails 当前用户
     * @return 学习进度
     */
    LearningProgressVO getStudentProgressInChapter(Integer chapterId, Integer studentId, UserDetails userDetails);
    
    /**
     * 获取学生在课程中的所有章节学习进度
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param userDetails 当前用户
     * @return 学习进度列表
     */
    List<LearningProgressVO> getStudentProgressInCourse(Integer courseId, Integer studentId, UserDetails userDetails);
    
    /**
     * 获取章节的所有学生学习进度
     * @param chapterId 章节ID
     * @param page 页码
     * @param size 每页数量
     * @param userDetails 当前用户
     * @return 分页学习进度列表
     */
    IPage<LearningProgressVO> getChapterStudentProgress(Integer chapterId, Integer page, Integer size, UserDetails userDetails);
    
    /**
     * 获取课程的学习进度概览
     * @param courseId 课程ID
     * @param userDetails 当前用户
     * @return 学习进度统计信息
     */
    Map<String, Object> getCourseProgressOverview(Integer courseId, UserDetails userDetails);
    
    /**
     * 计算学生的课程完成率
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param userDetails 当前用户
     * @return 完成率(0-100)
     */
    Integer calculateCourseCompletionRate(Integer courseId, Integer studentId, UserDetails userDetails);
    
    /**
     * 批量初始化新章节的学习进度记录
     * @param chapterId 章节ID
     * @param userDetails 当前用户
     * @return 是否初始化成功
     */
    boolean initializeChapterProgress(Integer chapterId, UserDetails userDetails);
} 