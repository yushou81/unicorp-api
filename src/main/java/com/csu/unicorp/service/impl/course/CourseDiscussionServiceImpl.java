package com.csu.unicorp.service.impl.course;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.CourseDiscussionDTO;
import com.csu.unicorp.entity.course.CourseDiscussion;
import com.csu.unicorp.entity.course.DualTeacherCourse;
import com.csu.unicorp.entity.user.User;
import com.csu.unicorp.mapper.CourseDiscussionMapper;
import com.csu.unicorp.mapper.DualTeacherCourseMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.CourseDiscussionService;
import com.csu.unicorp.vo.CourseDiscussionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程讨论服务实现类
 */
@Service
@RequiredArgsConstructor
public class CourseDiscussionServiceImpl implements CourseDiscussionService {

    private final CourseDiscussionMapper discussionMapper;
    private final DualTeacherCourseMapper courseMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public CourseDiscussionVO createDiscussion(CourseDiscussionDTO discussionDTO, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(discussionDTO.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 获取当前用户ID
        Integer userId = Integer.parseInt(userDetails.getUsername());

        // 创建讨论
        CourseDiscussion discussion = new CourseDiscussion();
        discussion.setCourseId(discussionDTO.getCourseId());
        discussion.setUserId(userId);
        discussion.setContent(discussionDTO.getContent());
        discussion.setParentId(null); // 新讨论没有父讨论
        discussion.setCreatedAt(LocalDateTime.now());
        discussion.setUpdatedAt(LocalDateTime.now());
        discussion.setIsDeleted(false);

        discussionMapper.insert(discussion);

        return convertToVO(discussion, userMapper.selectById(userId));
    }

    @Override
    @Transactional
    public CourseDiscussionVO replyToDiscussion(CourseDiscussionDTO replyDTO, UserDetails userDetails) {
        // 验证父讨论是否存在
        CourseDiscussion parentDiscussion = discussionMapper.selectById(replyDTO.getParentId());
        if (parentDiscussion == null || Boolean.TRUE.equals(parentDiscussion.getIsDeleted())) {
            throw new RuntimeException("讨论不存在");
        }

        // 获取当前用户ID
        Integer userId = Integer.parseInt(userDetails.getUsername());

        // 创建回复
        CourseDiscussion reply = new CourseDiscussion();
        reply.setCourseId(parentDiscussion.getCourseId());
        reply.setUserId(userId);
        reply.setContent(replyDTO.getContent());
        reply.setParentId(replyDTO.getParentId());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());
        reply.setIsDeleted(false);

        discussionMapper.insert(reply);

        return convertToVO(reply, userMapper.selectById(userId));
    }

    @Override
    public CourseDiscussionVO getDiscussionDetail(Integer discussionId, UserDetails userDetails) {
        // 验证讨论是否存在
        CourseDiscussion discussion = discussionMapper.selectById(discussionId);
        if (discussion == null || Boolean.TRUE.equals(discussion.getIsDeleted())) {
            throw new RuntimeException("讨论不存在");
        }

        // 获取发布者信息
        User user = userMapper.selectById(discussion.getUserId());

        return convertToVO(discussion, user);
    }

    @Override
    public IPage<CourseDiscussionVO> getCourseDiscussions(Integer courseId, Integer page, Integer size, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 分页查询课程讨论（只查询顶级讨论，不包含回复）
        Page<CourseDiscussion> pageParam = new Page<>(page, size);
        IPage<CourseDiscussion> discussionPage = discussionMapper.selectTopLevelDiscussionsByCourse(pageParam, courseId);
        
        // 转换为VO
        return discussionPage.convert(discussion -> {
            User user = userMapper.selectById(discussion.getUserId());
            return convertToVO(discussion, user);
        });
    }

    @Override
    @Transactional
    public CourseDiscussionVO updateDiscussion(Integer discussionId, String content, UserDetails userDetails) {
        // 验证讨论是否存在
        CourseDiscussion discussion = discussionMapper.selectById(discussionId);
        if (discussion == null || Boolean.TRUE.equals(discussion.getIsDeleted())) {
            throw new RuntimeException("讨论不存在");
        }

        // 获取当前用户ID
        Integer userId = Integer.parseInt(userDetails.getUsername());

        // 验证是否是讨论发布者
        if (!discussion.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改他人的讨论");
        }

        // 更新讨论内容
        discussion.setContent(content);
        discussion.setUpdatedAt(LocalDateTime.now());
        discussionMapper.updateById(discussion);

        // 获取发布者信息
        User user = userMapper.selectById(discussion.getUserId());

        return convertToVO(discussion, user);
    }

    @Override
    @Transactional
    public boolean deleteDiscussion(Integer discussionId, UserDetails userDetails) {
        // 验证讨论是否存在
        CourseDiscussion discussion = discussionMapper.selectById(discussionId);
        if (discussion == null || Boolean.TRUE.equals(discussion.getIsDeleted())) {
            throw new RuntimeException("讨论不存在");
        }

        // 获取当前用户ID和角色
        Integer userId = Integer.parseInt(userDetails.getUsername());
        boolean isAdmin = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isTeacher = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"));

        // 验证是否有权限删除
        if (!discussion.getUserId().equals(userId) && !isAdmin && !isTeacher) {
            throw new RuntimeException("无权删除他人的讨论");
        }

        // 逻辑删除讨论
        discussion.setIsDeleted(true);
        discussionMapper.updateById(discussion);

        // 如果是顶级讨论，同时删除所有回复
        if (discussion.getParentId() == null) {
            discussionMapper.deleteRepliesByParentId(discussionId);
        }

        return true;
    }

    @Override
    public List<CourseDiscussionVO> getDiscussionReplies(Integer discussionId, UserDetails userDetails) {
        // 验证讨论是否存在
        CourseDiscussion discussion = discussionMapper.selectById(discussionId);
        if (discussion == null || Boolean.TRUE.equals(discussion.getIsDeleted())) {
            throw new RuntimeException("讨论不存在");
        }

        // 查询回复列表
        List<CourseDiscussion> replies = discussionMapper.selectRepliesByParentId(discussionId);
        
        // 转换为VO
        return replies.stream().map(reply -> {
            User user = userMapper.selectById(reply.getUserId());
            return convertToVO(reply, user);
        }).collect(Collectors.toList());
    }

    @Override
    public Integer countCourseDiscussions(Integer courseId) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 统计讨论数量（包括回复）
        return discussionMapper.countDiscussionsByCourse(courseId);
    }
    
    /**
     * 将实体转换为VO
     * @param discussion 讨论实体
     * @param user 用户实体
     * @return 讨论VO
     */
    private CourseDiscussionVO convertToVO(CourseDiscussion discussion, User user) {
        CourseDiscussionVO vo = new CourseDiscussionVO();
        BeanUtils.copyProperties(discussion, vo);
        
        // 设置用户信息
        if (user != null) {
            vo.setUserName(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }
        
        return vo;
    }
} 