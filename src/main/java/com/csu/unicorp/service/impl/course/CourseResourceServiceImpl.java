package com.csu.unicorp.service.impl.course;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.CourseResourceDTO;
import com.csu.unicorp.entity.course.CourseResource;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.course.CourseResourceMapper;
import com.csu.unicorp.mapper.course.DualTeacherCourseMapper;
import com.csu.unicorp.service.CourseResourceService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.CourseResourceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 课程资源服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseResourceServiceImpl extends ServiceImpl<CourseResourceMapper, CourseResource> implements CourseResourceService {

    private final CourseResourceMapper resourceMapper;
    private final DualTeacherCourseMapper courseMapper;
    private final UserService userService;
    
    // 课程资源存储路径
    private static final String RESOURCE_UPLOAD_PATH = "upload/courses/resources/";
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseResourceVO uploadResource(MultipartFile file, CourseResourceDTO resourceDTO, UserDetails userDetails) throws IOException {
        // 检查课程是否存在
        DualTeacherCourse course = courseMapper.selectById(resourceDTO.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 检查上传权限
        String username = userDetails.getUsername();
        Integer userId = getUserId(username);
        String userType = getUserType(userDetails);
        log.info("课程资源上传权限检查 - 用户ID: {}, 用户类型: {}", userId, userType);
        log.info("课程资源上传权限检查 - 课程教师ID: {}, 课程导师ID: {}", course.getTeacherId(), course.getMentorId());
        
        boolean isTeacher = "TEACHER".equals(userType) && Objects.equals(course.getTeacherId(), userId);
        boolean isMentor = "MENTOR".equals(userType) && Objects.equals(course.getMentorId(), userId);
        
        if (!isTeacher && !isMentor) {
            throw new BusinessException("无权上传课程资源");
        }
        
        // 处理文件上传
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        // 创建目录（如果不存在）
        File uploadDir = new File(RESOURCE_UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // 保存文件
        Path filePath = Paths.get(RESOURCE_UPLOAD_PATH, newFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // 保存资源信息到数据库
        CourseResource resource = new CourseResource();
        resource.setCourseId(resourceDTO.getCourseId());
        resource.setTitle(resourceDTO.getTitle());
        resource.setDescription(resourceDTO.getDescription());
        resource.setResourceType(resourceDTO.getResourceType());
        resource.setFilePath(RESOURCE_UPLOAD_PATH + newFilename);
        resource.setFileSize(file.getSize());
        resource.setFileType(file.getContentType());
        resource.setUploaderId(userId);
        resource.setUploaderType(userType);
        resource.setDownloadCount(0);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setIsDeleted(false);
        
        resourceMapper.insert(resource);
        
        // 转换为VO并返回
        CourseResourceVO vo = convertToVO(resource);
        vo.setUploaderName(getUserName(userId));
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteResource(Integer resourceId, UserDetails userDetails) {
        CourseResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        
        // 检查删除权限
        String username = userDetails.getUsername();
        Integer userId = getUserId(username);
        String userType = getUserType(userDetails);
        
        boolean isUploader = Objects.equals(resource.getUploaderId(), userId) && 
                             Objects.equals(resource.getUploaderType(), userType);
        boolean isTeacher = "TEACHER".equals(userType);
        
        if (!isUploader && !isTeacher) {
            throw new BusinessException("无权删除该资源");
        }
        
        // 逻辑删除资源
        resourceMapper.deleteById(resourceId);
        
        // 尝试删除物理文件
        try {
            Path filePath = Paths.get(resource.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("删除资源文件失败: {}", resource.getFilePath(), e);
            // 文件删除失败不影响业务逻辑
        }
    }

    @Override
    public CourseResourceVO getResourceById(Integer resourceId) {
        CourseResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        
        CourseResourceVO vo = convertToVO(resource);
        vo.setUploaderName(getUserName(resource.getUploaderId()));
        return vo;
    }

    @Override
    public IPage<CourseResourceVO> getResourcesByCourseId(Integer courseId, int page, int size) {
        // 检查课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 分页查询
        Page<CourseResource> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CourseResource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseResource::getCourseId, courseId)
                   .eq(CourseResource::getIsDeleted, false)
                   .orderByDesc(CourseResource::getCreatedAt);
        
        IPage<CourseResource> resourcePage = resourceMapper.selectPage(pageParam, queryWrapper);
        
        // 转换为VO
        return resourcePage.convert(resource -> {
            CourseResourceVO vo = convertToVO(resource);
            vo.setUploaderName(getUserName(resource.getUploaderId()));
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String downloadResource(Integer resourceId) {
        CourseResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        
        // 增加下载次数
        resource.setDownloadCount(resource.getDownloadCount() + 1);
        resourceMapper.updateById(resource);
        
        return resource.getFilePath();
    }
    
    /**
     * 将实体转换为VO
     */
    private CourseResourceVO convertToVO(CourseResource resource) {
        CourseResourceVO vo = new CourseResourceVO();
        BeanUtils.copyProperties(resource, vo);
        
        // 获取课程标题
        DualTeacherCourse course = courseMapper.selectById(resource.getCourseId());
        if (course != null) {
            vo.setCourseTitle(course.getTitle());
        }
        
        return vo;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
    
    /**
     * 从用户名获取用户ID
     */
    private Integer getUserId(String username) {
        User user = userService.getByAccount(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user.getId();
    }
    
    /**
     * 获取用户姓名
     */
    private String getUserName(Integer userId) {
        User user = userService.getById(userId);
        return user != null ? user.getNickname() : "未知用户";
    }
    
    /**
     * 获取用户类型
     */
    private String getUserType(UserDetails userDetails) {
        // 从用户权限中获取角色信息
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        if (roles.contains("ROLE_TEACHER")) {
            return "TEACHER";
        } else if (roles.contains("ROLE_EN_TEACHER")) {
            return "MENTOR";
        } else if (roles.contains("ROLE_STUDENT")) {
            return "STUDENT";
        } else if (roles.contains("ROLE_SCH_ADMIN")) {
            return "ADMIN";
        }
        
        return "UNKNOWN";
    }
} 