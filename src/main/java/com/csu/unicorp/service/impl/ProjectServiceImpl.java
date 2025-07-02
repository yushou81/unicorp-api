package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.ProjectCreationDTO;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.Project;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.ProjectMember;
import com.csu.unicorp.entity.ProjectApplication;
import com.csu.unicorp.entity.UserVerification;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.ProjectMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.ProjectMemberMapper;
import com.csu.unicorp.mapper.ProjectApplicationMapper;
import com.csu.unicorp.mapper.UserVerificationMapper;
import com.csu.unicorp.service.ProjectService;
import com.csu.unicorp.vo.ProjectMemberVO;
import com.csu.unicorp.vo.ProjectVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final ProjectApplicationMapper projectApplicationMapper;
    private final UserVerificationMapper userVerificationMapper;
    
    /**
     * 分页查询项目列表
     */
    @Override
    public IPage<ProjectVO> getProjectList(
        int page,
        int size,
        String keyword,
        Integer organizationId,
        List<String> difficulty,
        List<String> supportLanguages,
        List<String> techFields,
        List<String> programmingLanguages,
        Integer userId,
        String needstatus
    ) {
        Page<Project> pageParam = new Page<>(page, size);
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        if (organizationId != null) {
            wrapper.eq("organization_id", organizationId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("title", keyword);
        }
        wrapper.eq("is_deleted", false);

        // 新增：难度筛选
        if (difficulty != null && !difficulty.isEmpty()) {
            wrapper.in("difficulty", difficulty);
        }
        // 新增：支持语言筛选
        if (supportLanguages != null && !supportLanguages.isEmpty()) {
            wrapper.and(w -> {
                for (String lang : supportLanguages) {
                    w.or().like("support_languages", lang);
                }
            });
        }
        // 新增：技术领域筛选
        if (techFields != null && !techFields.isEmpty()) {
            wrapper.and(w -> {
                for (String tech : techFields) {
                    w.or().like("tech_fields", tech);
                }
            });
        }
        // 新增：编程语言筛选
        if (programmingLanguages != null && !programmingLanguages.isEmpty()) {
            wrapper.and(w -> {
                for (String code : programmingLanguages) {
                    w.or().like("programming_languages", code);
                }
            });
        }

        // 先查出所有项目
        List<Project> allProjects = projectMapper.selectList(wrapper);
        List<Integer> allProjectIds = allProjects.stream().map(Project::getId).collect(Collectors.toList());

        // needstatus筛选
        Map<Integer, String> projectIdToStatus = new HashMap<>();
        if (userId != null && !allProjectIds.isEmpty()) {
            List<ProjectApplication> applications = projectApplicationMapper.selectList(
                new LambdaQueryWrapper<ProjectApplication>()
                    .eq(ProjectApplication::getUserId, userId)
                    .in(ProjectApplication::getProjectId, allProjectIds)
                    .eq(ProjectApplication::getIsDeleted, false)
            );
            for (ProjectApplication app : applications) {
                projectIdToStatus.put(app.getProjectId(), app.getStatus());
            }
        }

        List<Integer> filteredProjectIds;
        if (userId != null && !"all".equals(needstatus) && !allProjectIds.isEmpty()) {
            if ("no".equals(needstatus)) {
                filteredProjectIds = allProjectIds.stream()
                    .filter(pid -> !projectIdToStatus.containsKey(pid))
                    .collect(Collectors.toList());
            } else {
                filteredProjectIds = allProjectIds.stream()
                    .filter(pid -> needstatus.equals(projectIdToStatus.get(pid)))
                    .collect(Collectors.toList());
            }
        } else {
            filteredProjectIds = allProjectIds;
        }

        // 3. 批量查所有项目的成员数
        Map<Integer, Integer> projectIdToCount = new HashMap<>();
        if (!filteredProjectIds.isEmpty()) {
            List<Map<String, Object>> countList = projectMemberMapper.selectMemberCountByProjectIds(filteredProjectIds);
            for (Map<String, Object> map : countList) {
                Integer pid = (Integer) map.get("project_id");
                Long cnt = (Long) map.get("cnt"); // MySQL返回Long
                projectIdToCount.put(pid, cnt.intValue());
            }
        }

        // 组装VO（和原来一样）
        List<ProjectVO> voList = new ArrayList<>();
        for (Integer pid : filteredProjectIds) {
            ProjectVO vo = new ProjectVO();
            Project project = projectMapper.selectById(pid);
            BeanUtils.copyProperties(project, vo);
            vo.setMemberCount(projectIdToCount.getOrDefault(pid, 0));
            vo.setApplicationStatus(projectIdToStatus.get(pid));
            Organization org = organizationMapper.selectById(project.getOrganizationId());
            if (org != null) {
                vo.setOrganizationName(org.getOrganizationName());
            }
            // 新增：查找该用户在该项目下的申请id
            if (userId != null) {
                LambdaQueryWrapper<ProjectApplication> appQuery = new LambdaQueryWrapper<>();
                appQuery.eq(ProjectApplication::getProjectId, pid)
                        .eq(ProjectApplication::getUserId, userId)
                        .eq(ProjectApplication::getIsDeleted, false);
                ProjectApplication app = projectApplicationMapper.selectOne(appQuery);
                vo.setApplicationId(app != null ? app.getId() : null);
            }
            voList.add(vo);
        }

        // 返回分页VO
        int total = filteredProjectIds.size();
        int fromIndex = Math.min((page - 1) * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<Integer> pageProjectIds = filteredProjectIds.subList(fromIndex, toIndex);
        List<Project> pageProjects = pageProjectIds.isEmpty() ? new ArrayList<>() : projectMapper.selectBatchIds(pageProjectIds);
        List<ProjectVO> voListPage = new ArrayList<>();
        for (Project project : pageProjects) {
            ProjectVO vo = new ProjectVO();
            BeanUtils.copyProperties(project, vo);
            vo.setMemberCount(projectIdToCount.getOrDefault(project.getId(), 0));
            vo.setApplicationStatus(projectIdToStatus.get(project.getId()));
            vo.setApplied(projectIdToStatus.containsKey(project.getId()));
            Organization org = organizationMapper.selectById(project.getOrganizationId());
            if (org != null) {
                vo.setOrganizationName(org.getOrganizationName());
            }
            // 新增：查找该用户在该项目下的申请id
            if (userId != null) {
                LambdaQueryWrapper<ProjectApplication> appQuery = new LambdaQueryWrapper<>();
                appQuery.eq(ProjectApplication::getProjectId, project.getId())
                        .eq(ProjectApplication::getUserId, userId)
                        .eq(ProjectApplication::getIsDeleted, false);
                ProjectApplication app = projectApplicationMapper.selectOne(appQuery);
                vo.setApplicationId(app != null ? app.getId() : null);
            }
            voListPage.add(vo);
        }
        Page<ProjectVO> voPage = new Page<>(page, size, total);
        voPage.setRecords(voListPage);
        return voPage;
    }
    
    /**
     * 创建新项目
     * @param projectCreationDTO 前端传来的项目创建DTO
     * @param userDetails 当前登录用户信息
     * @return 创建后的项目VO
     */
    @Override
    @Transactional
    public ProjectVO createProject(ProjectCreationDTO projectCreationDTO, UserDetails userDetails) {
        // 1. 获取当前登录用户
        User currentUser = getUserByUsername(userDetails.getUsername());

        // 2. 检查用户是否有组织关联
        if (currentUser.getOrganizationId() == null) {
            throw new BusinessException("当前用户未关联任何组织，无法发布项目");
        }

        // 3. 检查用户是否有权限发布项目
        boolean hasPermission = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER") ||
                        a.getAuthority().equals("ROLE_EN_ADMIN") ||
                        a.getAuthority().equals("ROLE_EN_TEACHER"));
        if (!hasPermission) {
            throw new BusinessException("当前用户无权发布项目");
        }

        // 4. 创建项目实体，并拷贝同名字段
        Project project = new Project();
        BeanUtils.copyProperties(projectCreationDTO, project);

        // 5. 处理多选字段（List<String> -> 逗号分隔字符串）
        if (projectCreationDTO.getSupportLanguages() != null) {
            project.setSupportLanguages(String.join(",", projectCreationDTO.getSupportLanguages()));
        }
        if (projectCreationDTO.getTechFields() != null) {
            project.setTechFields(String.join(",", projectCreationDTO.getTechFields()));
        }
        if (projectCreationDTO.getProgrammingLanguages() != null) {
            project.setProgrammingLanguages(String.join(",", projectCreationDTO.getProgrammingLanguages()));
        }

        // 6. 设置组织ID
        project.setOrganizationId(currentUser.getOrganizationId());

        // 7. 设置默认状态
        if (project.getStatus() == null || project.getStatus().isEmpty()) {
            project.setStatus("recruiting");
        }

        // 8. 设置创建时间
        project.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // 9. 保存项目到数据库
        projectMapper.insert(project);

        // 10. 构建VO返回，逗号分隔字符串转List
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        if (project.getSupportLanguages() != null) {
            vo.setSupportLanguages(Arrays.asList(project.getSupportLanguages().split(",")));
        }
        if (project.getTechFields() != null) {
            vo.setTechFields(Arrays.asList(project.getTechFields().split(",")));
        }
        if (project.getProgrammingLanguages() != null) {
            vo.setProgrammingLanguages(Arrays.asList(project.getProgrammingLanguages().split(",")));
        }

        return vo;
    }
    
    /**
     * 根据ID获取项目详情
     */
    @Override
    public ProjectVO getProjectById(Integer id) {
        Project project = projectMapper.selectById(id);
        if (project == null || project.getIsDeleted()) {
            throw new BusinessException("项目不存在");
        }
        
        return convertToVO(project);
    }
    
    /**
     * 更新项目信息
     */
    @Override
    @Transactional
    public ProjectVO updateProject(Integer id, ProjectCreationDTO projectCreationDTO, UserDetails userDetails) {
        // 获取项目
        Project project = projectMapper.selectById(id);
        if (project == null || project.getIsDeleted()) {
            throw new BusinessException("项目不存在");
        }
        
        // 检查权限
        if (!hasProjectPermission(project, userDetails)) {
            throw new BusinessException("无权操作此项目");
        }
        
        // 更新项目信息
        BeanUtils.copyProperties(projectCreationDTO, project);

        // 处理多选字段（List<String> -> 逗号分隔字符串）
        if (projectCreationDTO.getSupportLanguages() != null) {
            project.setSupportLanguages(String.join(",", projectCreationDTO.getSupportLanguages()));
        }
        if (projectCreationDTO.getTechFields() != null) {
            project.setTechFields(String.join(",", projectCreationDTO.getTechFields()));
        }
        if (projectCreationDTO.getProgrammingLanguages() != null) {
            project.setProgrammingLanguages(String.join(",", projectCreationDTO.getProgrammingLanguages()));
        }

        projectMapper.updateById(project);
        
        return convertToVO(project);
    }
    
    /**
     * 删除项目
     */
    @Override
    @Transactional
    public void deleteProject(Integer id, UserDetails userDetails) {
        // 获取项目
        Project project = projectMapper.selectById(id);
        if (project == null || project.getIsDeleted()) {
            throw new BusinessException("项目不存在");
        }
        
        // 检查权限
        if (!hasProjectPermission(project, userDetails)) {
            throw new BusinessException("无权操作此项目");
        }
        
        // 逻辑删除项目
        projectMapper.deleteById(id);
    }
    
    /**
     * 检查用户是否有权限操作项目
     */
    @Override
    public boolean hasProjectPermission(Project project, UserDetails userDetails) {
        // 系统管理员有所有权限
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + RoleConstants.ROLE_SYSTEM_ADMIN))) {
            return true;
        }
        
        // 获取当前登录用户
        User currentUser = getUserByUsername(userDetails.getUsername());
        
        // 同一组织的教师、企业管理员或企业导师可以操作
        if (currentUser.getOrganizationId() != null 
                && currentUser.getOrganizationId().equals(project.getOrganizationId())
                && (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + RoleConstants.ROLE_TEACHER)) ||
                    userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + RoleConstants.ROLE_ENTERPRISE_ADMIN)) ||
                    userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + RoleConstants.ROLE_ENTERPRISE_MENTOR)))) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 将项目实体转换为VO
     */
    @Override
    public ProjectVO convertToVO(Project project) {
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        
        // 获取组织名称
        if (project.getOrganizationId() != null) {
            Organization organization = organizationMapper.selectById(project.getOrganizationId());
            if (organization != null) {
                vo.setOrganizationName(organization.getOrganizationName());
            }
        }
        
        return vo;
    }
    
    /**
     * 根据用户名获取用户
     */
    private User getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccount, username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void removeProjectMember(Integer projectId,Integer memberId) {
        projectMemberMapper.logicDeleteByUserId(projectId,memberId);
    }

    @Override
    public List<ProjectMemberVO> getProjectMembers(Integer projectId) {
        // 1. 查出所有未删除的成员
        List<ProjectMember> members = projectMemberMapper.selectList(
            new QueryWrapper<ProjectMember>()
                .eq("project_id", projectId)
                .eq("is_deleted", 0)
        );
        List<ProjectMemberVO> result = new ArrayList<>();
        for (ProjectMember member : members) {
            ProjectMemberVO vo = new ProjectMemberVO();
            vo.setUserId(member.getUserId());
            vo.setJoinedAt(member.getCreatedAt() != null ? member.getCreatedAt().toString() : null);

            // 2. 查 real_name
            UserVerification verification = userVerificationMapper.selectOne(
                new QueryWrapper<UserVerification>().eq("user_id", member.getUserId())
            );
            if (verification != null) {
                vo.setRealName(verification.getRealName());
            }

            // 3. 查 application_statement
            ProjectApplication application = projectApplicationMapper.selectOne(
                new QueryWrapper<ProjectApplication>()
                    .eq("user_id", member.getUserId())
                    .eq("project_id", projectId)
                    .eq("is_deleted", 0)
            );
            if (application != null) {
                vo.setApplicationStatement(application.getApplicationStatement());
            }

            result.add(vo);
        }
        return result;
    }
} 