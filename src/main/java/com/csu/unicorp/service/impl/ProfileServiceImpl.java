package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.entity.StudentProfile;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.StudentProfileMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户个人资料服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;

    @Override
    @Transactional
    public void updateAvatar(Integer userId, String avatarUrl) {
        // 验证URL
        if (!StringUtils.hasText(avatarUrl)) {
            throw new BusinessException("头像URL不能为空");
        }
        
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新用户头像URL
        //user.setAvatarUrl(avatarUrl);
        //userMapper.updateById(user);
        
        //log.info("用户 {} 更新了头像: {}", userId, avatarUrl);
    }

    @Override
    @Transactional
    public void updateResume(Integer userId, String resumeUrl) {
        // 验证URL
        if (!StringUtils.hasText(resumeUrl)) {
            throw new BusinessException("简历URL不能为空");
        }
        
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证用户是否为学生
        List<String> roles = userMapper.selectRolesByUserId(userId);
        if (roles == null || !roles.contains("STUDENT")) {
            throw new BusinessException("只有学生用户可以上传简历");
        }
        
        // 查找或创建学生档案
        LambdaQueryWrapper<StudentProfile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentProfile::getUserId, userId);
        StudentProfile profile = studentProfileMapper.selectOne(queryWrapper);
        
        if (profile == null) {
            // 创建新的学生档案
            profile = new StudentProfile();
            profile.setUserId(userId);
            profile.setResumeUrl(resumeUrl);
            studentProfileMapper.insert(profile);
        } else {
            // 更新现有档案
            profile.setResumeUrl(resumeUrl);
            studentProfileMapper.updateById(profile);
        }
        
        log.info("学生 {} 更新了简历: {}", userId, resumeUrl);
    }
} 