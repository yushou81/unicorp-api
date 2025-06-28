package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.dto.ProjectApplicationCreationDTO;
import com.csu.unicorp.dto.ProjectApplicationStatusUpdateDTO;
import com.csu.unicorp.entity.Project;
import com.csu.unicorp.entity.ProjectApplication;
import com.csu.unicorp.entity.ProjectMember;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.ProjectApplicationMapper;
import com.csu.unicorp.mapper.ProjectMapper;
import com.csu.unicorp.mapper.ProjectMemberMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.ProjectApplicationService;
import com.csu.unicorp.service.ProjectService;
import com.csu.unicorp.vo.MyProjectApplicationDetailVO;
import com.csu.unicorp.vo.ProjectApplicationDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目申请服务实现类
 */
@Service
@RequiredArgsConstructor
public class ProjectApplicationServiceImpl extends ServiceImpl<ProjectApplicationMapper, ProjectApplication> implements ProjectApplicationService {

    private final ProjectMapper projectMapper;
    private final ProjectApplicationMapper projectApplicationMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserMapper userMapper;
    private final ProjectService projectService;

    /**
     * 学生申请加入项目
     */
    @Override
    @Transactional
    public ProjectApplicationDetailVO applyForProject(Integer projectId, ProjectApplicationCreationDTO dto, UserDetails userDetails) {
        // 获取当前用户
        User currentUser = userMapper.findByUsername(userDetails.getUsername());
        if (currentUser == null) {
            throw new AccessDeniedException("用户不存在");
        }
        
        // 检查用户是否有学生角色
        String role = userMapper.selectRoleByUserId(currentUser.getId());
        if (!RoleConstants.DB_ROLE_STUDENT.equals(role)) {
            throw new AccessDeniedException("只有学生用户可以申请项目");
        }
        
        // 检查项目是否存在
        Project project = projectMapper.selectById(projectId);
        if (project == null || Boolean.TRUE.equals(project.getIsDeleted())) {
            throw new IllegalArgumentException("项目不存在");
        }
        
        // 检查项目是否处于招募状态
        if (!"recruiting".equals(project.getStatus())) {
            throw new IllegalArgumentException("该项目当前不接受申请");
        }
        
        // 检查用户是否已经申请过该项目
        LambdaQueryWrapper<ProjectApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectApplication::getProjectId, projectId)
                .eq(ProjectApplication::getUserId, currentUser.getId())
                .eq(ProjectApplication::getIsDeleted, false);
        
        long count = projectApplicationMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new IllegalArgumentException("您已经申请过该项目");
        }
        
        // 创建申请记录
        ProjectApplication application = new ProjectApplication();
        application.setProjectId(projectId);
        application.setUserId(currentUser.getId());
        application.setStatus("submitted");
        application.setApplicationStatement(dto.getApplicationStatement());
        application.setIsDeleted(false);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        
        projectApplicationMapper.insert(application);
        
        // 返回申请详情
        return convertToDetailVO(application, currentUser);
    }

    /**
     * 获取项目的申请列表
     */
    @Override
    public List<ProjectApplicationDetailVO> getProjectApplications(Integer projectId, UserDetails userDetails) {
        // 获取当前用户
        User currentUser = userMapper.findByUsername(userDetails.getUsername());
        if (currentUser == null) {
            throw new AccessDeniedException("用户不存在");
        }
        
        // 检查项目是否存在
        Project project = projectMapper.selectById(projectId);
        if (project == null || Boolean.TRUE.equals(project.getIsDeleted())) {
            throw new IllegalArgumentException("项目不存在");
        }
        
        // 检查用户是否有权限查看项目申请
        if (!projectService.hasProjectPermission(project, userDetails)) {
            throw new AccessDeniedException("您没有权限查看该项目的申请");
        }
        
        // 获取项目申请列表
        List<Map<String, Object>> applications = projectApplicationMapper.selectApplicationsWithUserInfo(projectId);
        
        // 转换为VO
        return applications.stream()
                .map(this::convertMapToDetailVO)
                .collect(Collectors.toList());
    }

    /**
     * 更新项目申请状态
     */
    @Override
    @Transactional
    public ProjectApplicationDetailVO updateApplicationStatus(Integer applicationId, ProjectApplicationStatusUpdateDTO dto, UserDetails userDetails) {
        // 获取当前用户
        User currentUser = userMapper.findByUsername(userDetails.getUsername());
        if (currentUser == null) {
            throw new AccessDeniedException("用户不存在");
        }
        
        // 检查申请是否存在
        ProjectApplication application = projectApplicationMapper.selectById(applicationId);
        if (application == null || Boolean.TRUE.equals(application.getIsDeleted())) {
            throw new IllegalArgumentException("申请不存在");
        }
        
        // 检查项目是否存在
        Project project = projectMapper.selectById(application.getProjectId());
        if (project == null || Boolean.TRUE.equals(project.getIsDeleted())) {
            throw new IllegalArgumentException("项目不存在");
        }
        
        // 检查用户是否有权限更新申请状态
        if (!projectService.hasProjectPermission(project, userDetails)) {
            throw new AccessDeniedException("您没有权限更新该申请");
        }
        
        // 更新申请状态
        application.setStatus(dto.getStatus());
        application.setUpdatedAt(LocalDateTime.now());
        projectApplicationMapper.updateById(application);
        
        // 如果状态为approved，则自动添加为项目成员
        if ("approved".equals(dto.getStatus())) {
            // 检查是否已经是项目成员
            LambdaQueryWrapper<ProjectMember> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProjectMember::getProjectId, application.getProjectId())
                    .eq(ProjectMember::getUserId, application.getUserId())
                    .eq(ProjectMember::getIsDeleted, false);
            
            long count = projectMemberMapper.selectCount(queryWrapper);
            if (count == 0) {
                // 添加为项目成员
                ProjectMember member = new ProjectMember();
                member.setProjectId(application.getProjectId());
                member.setUserId(application.getUserId());
                member.setRoleInProject("成员");
                member.setIsDeleted(false);
                member.setCreatedAt(LocalDateTime.now());
                member.setUpdatedAt(LocalDateTime.now());
                
                projectMemberMapper.insert(member);
            }
        }
        
        // 获取申请人信息
        User applicant = userMapper.selectById(application.getUserId());
        
        // 返回更新后的申请详情
        return convertToDetailVO(application, applicant);
    }

    /**
     * 获取当前学生的项目申请列表
     */
    @Override
    public IPage<MyProjectApplicationDetailVO> getMyApplications(int page, int size, UserDetails userDetails) {
        // 获取当前用户
        User currentUser = userMapper.findByUsername(userDetails.getUsername());
        if (currentUser == null) {
            throw new AccessDeniedException("用户不存在");
        }
        
        // 检查用户是否有学生角色
        String role = userMapper.selectRoleByUserId(currentUser.getId());
        if (!RoleConstants.DB_ROLE_STUDENT.equals(role)) {
            throw new AccessDeniedException("只有学生用户可以查看自己的申请");
        }
        
        // 分页查询学生的申请
        Page<Map<String, Object>> pageParam = new Page<>(page, size);
        IPage<Map<String, Object>> applications = projectApplicationMapper.selectStudentApplications(pageParam, currentUser.getId());
        
        // 转换为VO
        List<MyProjectApplicationDetailVO> voList = applications.getRecords().stream()
                .map(this::convertMapToMyDetailVO)
                .collect(Collectors.toList());
        
        // 创建新的分页结果
        IPage<MyProjectApplicationDetailVO> result = new Page<>(applications.getCurrent(), applications.getSize(), applications.getTotal());
        result.setRecords(voList);
        
        return result;
    }
    
    /**
     * 将Map转换为ProjectApplicationDetailVO
     */
    private ProjectApplicationDetailVO convertMapToDetailVO(Map<String, Object> map) {
        ProjectApplicationDetailVO vo = new ProjectApplicationDetailVO();
        vo.setId((Integer) map.get("id"));
        vo.setProjectId((Integer) map.get("project_id"));
        vo.setUserId((Integer) map.get("user_id"));
        vo.setStatus((String) map.get("status"));
        vo.setApplicationStatement((String) map.get("application_statement"));
        vo.setCreatedAt((LocalDateTime) map.get("created_at"));
        
        ProjectApplicationDetailVO.ApplicantProfileVO profileVO = new ProjectApplicationDetailVO.ApplicantProfileVO();
        profileVO.setNickname((String) map.get("nickname"));
        profileVO.setRealName((String) map.get("real_name"));
        profileVO.setMajor((String) map.get("major"));
        vo.setApplicantProfile(profileVO);
        
        return vo;
    }
    
    /**
     * 将Map转换为MyProjectApplicationDetailVO
     */
    private MyProjectApplicationDetailVO convertMapToMyDetailVO(Map<String, Object> map) {
        MyProjectApplicationDetailVO vo = new MyProjectApplicationDetailVO();
        vo.setApplicationId((Integer) map.get("application_id"));
        vo.setStatus((String) map.get("status"));
        vo.setAppliedAt((LocalDateTime) map.get("applied_at"));
        
        MyProjectApplicationDetailVO.ProjectInfoVO projectInfo = new MyProjectApplicationDetailVO.ProjectInfoVO();
        projectInfo.setProjectId((Integer) map.get("project_id"));
        projectInfo.setProjectTitle((String) map.get("project_title"));
        projectInfo.setOrganizationName((String) map.get("organization_name"));
        vo.setProjectInfo(projectInfo);
        
        return vo;
    }
    
    /**
     * 将实体转换为VO
     */
    private ProjectApplicationDetailVO convertToDetailVO(ProjectApplication application, User applicant) {
        ProjectApplicationDetailVO vo = new ProjectApplicationDetailVO();
        vo.setId(application.getId());
        vo.setProjectId(application.getProjectId());
        vo.setUserId(application.getUserId());
        vo.setStatus(application.getStatus());
        vo.setApplicationStatement(application.getApplicationStatement());
        vo.setCreatedAt(application.getCreatedAt());
        
        ProjectApplicationDetailVO.ApplicantProfileVO profileVO = new ProjectApplicationDetailVO.ApplicantProfileVO();
        profileVO.setNickname(applicant.getNickname());
        
        // 获取用户实名信息和专业信息
        Map<String, Object> userInfo = userMapper.getUserVerificationAndProfile(applicant.getId());
        if (userInfo != null) {
            profileVO.setRealName((String) userInfo.get("real_name"));
            profileVO.setMajor((String) userInfo.get("major"));
        }
        
        vo.setApplicantProfile(profileVO);
        
        return vo;
    }
} 