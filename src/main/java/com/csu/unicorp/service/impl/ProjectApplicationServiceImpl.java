package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.mapper.ProjectApplicationMapper;
import com.csu.unicorp.mapper.ProjectMapper;
import com.csu.unicorp.mapper.ProjectMemberMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.service.ProjectApplicationService;
import com.csu.unicorp.service.ProjectService;
import com.csu.unicorp.vo.MyProjectApplicationDetailVO;
import com.csu.unicorp.vo.ProjectApplicationDetailVO;
import com.csu.unicorp.vo.ProjectVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.csu.unicorp.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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
    private final OrganizationMapper organizationMapper;
    private static final Logger log = LoggerFactory.getLogger(ProjectApplicationServiceImpl.class);

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

        // 1. 查是否有未删除的申请
        LambdaQueryWrapper<ProjectApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectApplication::getProjectId, projectId)
                .eq(ProjectApplication::getUserId, currentUser.getId())
                .eq(ProjectApplication::getIsDeleted, false);

        ProjectApplication existingApplication = projectApplicationMapper.selectOne(queryWrapper);

        // 2. 1. 查询项目最大人数
        Integer maxMemberCount = project.getPlanMemberCount();
        if (maxMemberCount == null) {
            maxMemberCount = Integer.MAX_VALUE; // 没有限制
        }

        // 2. 2. 查询当前已批准成员数
        LambdaQueryWrapper<ProjectApplication> memberCountWrapper = new LambdaQueryWrapper<>();
        memberCountWrapper.eq(ProjectApplication::getProjectId, projectId)
                          .eq(ProjectApplication::getStatus, "approved")
                          .eq(ProjectApplication::getIsDeleted, false);
        long approvedCount = projectApplicationMapper.selectCount(memberCountWrapper);

        // 2. 3. 如果已满，禁止申请
        if (approvedCount >= maxMemberCount) {
            throw new IllegalArgumentException("该项目人数已满，无法申请");
        }

        // 3. 如果有申请，更新status为submitted并返回
        if (existingApplication != null) {
            existingApplication.setStatus("submitted");
            existingApplication.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            projectApplicationMapper.updateById(existingApplication);
            return convertToDetailVO(existingApplication, currentUser);
        }

        // 4. 没有则插入新记录
        ProjectApplication application = new ProjectApplication();
        application.setProjectId(projectId);
        application.setUserId(currentUser.getId());
        application.setStatus("submitted");
        application.setApplicationStatement(dto.getApplicationStatement());
        application.setIsDeleted(false);
        application.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        application.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        projectApplicationMapper.insert(application);
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
        System.out.println("001");
        // 检查申请是否存在
        ProjectApplication application = projectApplicationMapper.selectById(applicationId);
        if (application == null || Boolean.TRUE.equals(application.getIsDeleted())) {
            throw new IllegalArgumentException("申请不存在");
        }
        System.out.println("002");
        // 检查项目是否存在
        Project project = projectMapper.selectById(application.getProjectId());
        if (project == null || Boolean.TRUE.equals(project.getIsDeleted())) {
            throw new IllegalArgumentException("项目不存在");
        }
        System.out.println("003");
        // 检查用户是否有权限更新申请状态
        // if (!projectService.hasProjectPermission(project, userDetails)) {
        //     throw new AccessDeniedException("您没有权限更新该申请");
        // }
        System.out.println("004");
        // 更新申请状态
        application.setStatus(dto.getStatus());
        application.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        projectApplicationMapper.updateById(application);
        System.out.println("005");
        // 如果状态为approved，则自动添加为项目成员
        if ("approved".equals(dto.getStatus())) {
            LambdaQueryWrapper<ProjectMember> memberQuery = new LambdaQueryWrapper<>();
            memberQuery.eq(ProjectMember::getProjectId, project.getId())
                       .eq(ProjectMember::getUserId, currentUser.getId())
                       .eq(ProjectMember::getIsDeleted, false);
            long count = projectMemberMapper.selectCount(memberQuery);
            if (count == 0) {
                ProjectMember member = new ProjectMember();
                member.setProjectId(project.getId());
                member.setUserId(currentUser.getId());
                member.setRoleInProject("成员");
                member.setIsDeleted(0);
                member.setCreatedAt(LocalDateTime.now());
                member.setUpdatedAt(LocalDateTime.now());
                try {
                    projectMemberMapper.insert(member);
                } catch (org.springframework.dao.DuplicateKeyException e) {
                    // 已存在，记录日志或提醒即可，不抛出异常
                    log.warn("项目成员已存在：projectId={}, userId={}", project.getId(), currentUser.getId());
                }
            }
        }
        System.out.println("006");
        // 获取申请人信息
        User applicant = userMapper.selectById(application.getUserId());
        System.out.println("007");
        // 返回更新后的申请详情
        return convertToDetailVO(application, applicant);
    }

    /**
     * 获取当前学生的项目申请列表，返回 MyProjectApplicationDetailVO 分页，字段与项目列表完全一致
     */
    @Override
    public IPage<MyProjectApplicationDetailVO> getMyProjectApplications(
        int page,
        int size,
        String keyword,
        Integer userId,
        List<String> difficulty,
        List<String> supportLanguages,
        List<String> techFields,
        List<String> programmingLanguages
    ) {
        // 1. 查出该学生所有申请记录
        List<ProjectApplication> applications = projectApplicationMapper.selectList(
            new LambdaQueryWrapper<ProjectApplication>()
                .eq(ProjectApplication::getUserId, userId)
                .eq(ProjectApplication::getIsDeleted, false)
        );
        if (applications.isEmpty()) {
            return new Page<>(page, size, 0);
        }
        // 2. 拿到所有申请过的项目ID
        List<Integer> projectIds = applications.stream().map(ProjectApplication::getProjectId).collect(Collectors.toList());

        // 3. 构造分页和动态查询条件
        Page<Project> pageParam = new Page<>(page, size);
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        wrapper.in("id", projectIds);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("title", keyword);
        }
        wrapper.eq("is_deleted", false);

        if (difficulty != null && !difficulty.isEmpty()) {
            wrapper.in("difficulty", difficulty);
        }
        if (supportLanguages != null && !supportLanguages.isEmpty()) {
            wrapper.and(w -> {
                for (String lang : supportLanguages) {
                    w.or().like("support_languages", lang);
                }
            });
        }
        if (techFields != null && !techFields.isEmpty()) {
            wrapper.and(w -> {
                for (String tech : techFields) {
                    w.or().like("tech_fields", tech);
                }
            });
        }
        if (programmingLanguages != null && !programmingLanguages.isEmpty()) {
            wrapper.and(w -> {
                for (String code : programmingLanguages) {
                    w.or().like("programming_languages", code);
                }
            });
        }

        // 4. 分页查项目
        IPage<Project> projectPage = projectMapper.selectPage(pageParam, wrapper);
        List<Project> projectList = projectPage.getRecords();

        // 5. 批量查所有项目的成员数
        List<Integer> pageProjectIds = projectList.stream().map(Project::getId).collect(Collectors.toList());
        Map<Integer, Integer> projectIdToCount = new HashMap<>();
        if (!pageProjectIds.isEmpty()) {
            List<Map<String, Object>> countList = projectMemberMapper.selectMemberCountByProjectIds(pageProjectIds);
            for (Map<String, Object> map : countList) {
                Integer pid = (Integer) map.get("project_id");
                Long cnt = (Long) map.get("cnt");
                projectIdToCount.put(pid, cnt.intValue());
            }
        }

        // 6. 组装VO
        List<MyProjectApplicationDetailVO> voList = new ArrayList<>();
        for (Project project : projectList) {
            MyProjectApplicationDetailVO vo = new MyProjectApplicationDetailVO();
            //vo.setProjectId(project.getId());
            vo.setTitle(project.getTitle());
            vo.setDescription(project.getDescription());
            vo.setMemberCount(projectIdToCount.getOrDefault(project.getId(), 0));
            vo.setPlanMemberCount(project.getPlanMemberCount());
            Organization org = organizationMapper.selectById(project.getOrganizationId());
            if (org != null) {
                vo.setOrganizationName(org.getOrganizationName());
            }
            // 申请相关字段
            ProjectApplication app = applications.stream()
                .filter(a -> a.getProjectId().equals(project.getId()))
                .findFirst().orElse(null);
            if (app != null) {
                vo.setApplicationId(app.getId());
                vo.setApplicationStatus(app.getStatus());
                vo.setAppliedAt(app.getCreatedAt());
            }
            voList.add(vo);
        }

        // 7. 返回分页VO
        Page<MyProjectApplicationDetailVO> voPage = new Page<>(page, size, projectPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
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
        vo.setCreatedAt((Timestamp) map.get("created_at"));
        
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
        vo.setAppliedAt((Timestamp) map.get("applied_at"));
        
        // 直接设置VO的各个字段
        vo.setTitle((String) map.get("project_title"));
        vo.setDescription((String) map.get("project_description"));
        vo.setPlanMemberCount((Integer) map.get("plan_member_count"));
        
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