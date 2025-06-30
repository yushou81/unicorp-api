package com.csu.unicorp.service.impl;

import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.ProfileUpdateDTO;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.StudentInfo;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.StudentInfoMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.PortfolioService;
import com.csu.unicorp.service.ProfileService;
import com.csu.unicorp.vo.PortfolioItemVO;
import com.csu.unicorp.vo.ResumeVO;
import com.csu.unicorp.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 个人主页服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final PortfolioService portfolioService;
    
    @Override
    public UserProfileVO getUserProfile(Integer userId) {
        // 获取用户基本信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("用户不存在");
        }
        
        // 构建用户档案VO
        UserProfileVO profileVO = buildUserProfileVO(user);
        
        return profileVO;
    }
    
    @Override
    public UserProfileVO getCurrentUserProfile(Integer currentUserId) {
        // 获取当前用户基本信息
        User user = userMapper.selectById(currentUserId);
        if (user == null) {
            throw new ResourceNotFoundException("用户不存在");
        }
        
        // 构建用户档案VO
        UserProfileVO profileVO = buildUserProfileVO(user);
        
        return profileVO;
    }
    
    @Override
    @Transactional
    public UserProfileVO updateUserProfile(Integer currentUserId, ProfileUpdateDTO profileUpdateDTO) {
        // 获取当前用户基本信息
        User user = userMapper.selectById(currentUserId);
        if (user == null) {
            throw new ResourceNotFoundException("用户不存在");
        }
        
        // 更新用户信息
        if (profileUpdateDTO.getNickname() != null) {
            user.setNickname(profileUpdateDTO.getNickname());
        }

        if (profileUpdateDTO.getAvatar() != null) {
            user.setAvatar(profileUpdateDTO.getAvatar());
        }
        
        // 保存更新
        userMapper.updateById(user);
        
        // 返回更新后的用户档案
        return buildUserProfileVO(user);
    }
    
    /**
     * 构建用户档案VO
     * 
     * @param user 用户实体
     * @return 用户档案VO
     */
    private UserProfileVO buildUserProfileVO(User user) {
        UserProfileVO profileVO = new UserProfileVO();
        
        // 复制基本信息
        profileVO.setId(user.getId());
        profileVO.setAccount(user.getAccount());
        profileVO.setNickname(user.getNickname());
        profileVO.setAvatarUrl(user.getAvatar());
        
        // 获取组织信息
        if (user.getOrganizationId() != null) {
            Organization organization = organizationMapper.selectById(user.getOrganizationId());
            if (organization != null) {
                profileVO.setOrganizationName(organization.getOrganizationName());
            }
        }
        
        // 获取用户角色
        String role = userMapper.selectRoleByUserId(user.getId());
        profileVO.setRole(role);
        
        // 如果是学生角色，添加学生特有信息
        if (RoleConstants.DB_ROLE_STUDENT.equals(role)) {
            // 获取学生档案信息
            StudentInfo studentInfo = studentInfoMapper.selectByUserId(user.getId());
            if (studentInfo != null) {
                ResumeVO resumeVO = new ResumeVO();
                resumeVO.setMajor(studentInfo.getMajor());
                resumeVO.setEducationLevel(studentInfo.getEducationLevel());
                profileVO.setResume(resumeVO);
            }
            
            // 获取作品集列表
            List<PortfolioItemVO> portfolioItems = portfolioService.getPortfolioItems(user.getId());
            profileVO.setPortfolio(portfolioItems);
        }
        
        return profileVO;
    }
} 