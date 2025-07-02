package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseQuestionDTO;
import com.csu.unicorp.vo.CourseQuestionVO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * 课程问答服务接口
 */
public interface CourseQuestionService {
    
    /**
     * 学生提问
     * @param questionDTO 问题信息
     * @param userDetails 当前用户
     * @return 创建的问题
     */
    CourseQuestionVO askQuestion(CourseQuestionDTO questionDTO, UserDetails userDetails);
    
    /**
     * 教师回答问题
     * @param questionId 问题ID
     * @param answer 回答内容
     * @param userDetails 当前用户
     * @return 更新后的问题
     */
    CourseQuestionVO answerQuestion(Integer questionId, String answer, UserDetails userDetails);
    
    /**
     * 获取问题详情
     * @param questionId 问题ID
     * @param userDetails 当前用户
     * @return 问题详情
     */
    CourseQuestionVO getQuestionDetail(Integer questionId, UserDetails userDetails);
    
    /**
     * 分页获取课程问题列表
     * @param courseId 课程ID
     * @param page 页码
     * @param size 每页数量
     * @param userDetails 当前用户
     * @return 分页问题列表
     */
    IPage<CourseQuestionVO> getCourseQuestions(Integer courseId, Integer page, Integer size, UserDetails userDetails);
    
    /**
     * 分页获取章节问题列表
     * @param chapterId 章节ID
     * @param page 页码
     * @param size 每页数量
     * @param userDetails 当前用户
     * @return 分页问题列表
     */
    IPage<CourseQuestionVO> getChapterQuestions(Integer chapterId, Integer page, Integer size, UserDetails userDetails);
    
    /**
     * 分页获取学生在课程中提出的问题列表
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param page 页码
     * @param size 每页数量
     * @param userDetails 当前用户
     * @return 分页问题列表
     */
    IPage<CourseQuestionVO> getStudentQuestions(Integer courseId, Integer studentId, Integer page, Integer size, UserDetails userDetails);
    
    /**
     * 更新问题
     * @param questionId 问题ID
     * @param title 更新的标题
     * @param content 更新的内容
     * @param userDetails 当前用户
     * @return 更新后的问题
     */
    CourseQuestionVO updateQuestion(Integer questionId, String title, String content, UserDetails userDetails);
    
    /**
     * 删除问题
     * @param questionId 问题ID
     * @param userDetails 当前用户
     * @return 是否删除成功
     */
    boolean deleteQuestion(Integer questionId, UserDetails userDetails);
    
    /**
     * 统计课程的问答数据
     * @param courseId 课程ID
     * @return 问答统计数据
     */
    Map<String, Object> getQuestionStatistics(Integer courseId);
    
    /**
     * 统计教师需要回答的问题数量
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @return 未回答问题数量
     */
    Integer countPendingQuestionsForTeacher(Integer courseId, Integer teacherId);
} 