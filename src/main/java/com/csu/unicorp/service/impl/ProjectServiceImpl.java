package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.ProjectCreationDTO;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.Project;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.ProjectMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.ProjectService;
import com.csu.unicorp.vo.ProjectVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    
    /**
     * 分页查询项目列表
     */
    @Override
    public IPage<ProjectVO> getProjectList(int page, int size, String keyword) {
        Page<Project> pageParam = new Page<>(page, size);
        IPage<Project> projectPage = projectMapper.selectProjectsWithOrgName(pageParam, keyword);
        
        // 获取正确的总记录数
        Long total = projectMapper.countProjects(keyword);
        projectPage.setTotal(total);
        
        // 重新计算总页数
        long pages = total % size == 0 ? total / size : total / size + 1;
        ((Page<Project>)projectPage).setPages(pages);
        
        // 转换为VO
        return projectPage.convert(this::convertToVO);
    }
    
    /**
     * 创建新项目
     */
    @Override
    @Transactional
    public ProjectVO createProject(ProjectCreationDTO projectCreationDTO, UserDetails userDetails) {
        // 获取当前登录用户
        User currentUser = getUserByUsername(userDetails.getUsername());
        
        // 检查用户是否有组织关联
        if (currentUser.getOrganizationId() == null) {
            throw new BusinessException("当前用户未关联任何组织，无法发布项目");
        }
        
        // 检查用户是否有权限发布项目（教师、企业管理员或企业导师）
        boolean hasPermission = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER") || 
                        a.getAuthority().equals("ROLE_EN_ADMIN") || 
                        a.getAuthority().equals("ROLE_EN_TEACHER"));
        
        if (!hasPermission) {
            throw new BusinessException("当前用户无权发布项目");
        }
        
        // 创建项目实体
        Project project = new Project();
        BeanUtils.copyProperties(projectCreationDTO, project);
        project.setOrganizationId(currentUser.getOrganizationId());
        
        // 如果没有指定状态，默认为"recruiting"
        if (project.getStatus() == null || project.getStatus().isEmpty()) {
            project.setStatus("recruiting");
        }
        
        project.setCreatedAt(LocalDateTime.now());
        
        // 保存项目
        projectMapper.insert(project);
        
        return convertToVO(project);
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
} 