package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseDiscussionDTO;
import com.csu.unicorp.vo.CourseDiscussionVO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * 课程讨论服务接口
 */
public interface CourseDiscussionService {
    
    /**
     * 创建讨论
     * @param discussionDTO 讨论信息
     * @param userDetails 当前用户
     * @return 创建的讨论
     */
    CourseDiscussionVO createDiscussion(CourseDiscussionDTO discussionDTO, UserDetails userDetails);
    
    /**
     * 回复讨论
     * @param discussionDTO 回复信息
     * @param userDetails 当前用户
     * @return 创建的回复
     */
    CourseDiscussionVO replyToDiscussion(CourseDiscussionDTO discussionDTO, UserDetails userDetails);
    
    /**
     * 获取讨论详情
     * @param discussionId 讨论ID
     * @param userDetails 当前用户
     * @return 讨论详情（包含回复）
     */
    CourseDiscussionVO getDiscussionDetail(Integer discussionId, UserDetails userDetails);
    
    /**
     * 分页获取课程讨论列表
     * @param courseId 课程ID
     * @param page 页码
     * @param size 每页数量
     * @param userDetails 当前用户
     * @return 分页讨论列表
     */
    IPage<CourseDiscussionVO> getCourseDiscussions(Integer courseId, Integer page, Integer size, UserDetails userDetails);
    
    /**
     * 更新讨论内容
     * @param discussionId 讨论ID
     * @param content 更新的内容
     * @param userDetails 当前用户
     * @return 更新后的讨论
     */
    CourseDiscussionVO updateDiscussion(Integer discussionId, String content, UserDetails userDetails);
    
    /**
     * 删除讨论
     * @param discussionId 讨论ID
     * @param userDetails 当前用户
     * @return 是否删除成功
     */
    boolean deleteDiscussion(Integer discussionId, UserDetails userDetails);
    
    /**
     * 获取讨论的所有回复
     * @param discussionId 讨论ID
     * @param userDetails 当前用户
     * @return 回复列表
     */
    List<CourseDiscussionVO> getDiscussionReplies(Integer discussionId, UserDetails userDetails);
    
    /**
     * 统计课程的讨论数量
     * @param courseId 课程ID
     * @return 讨论数量
     */
    Integer countCourseDiscussions(Integer courseId);
} 