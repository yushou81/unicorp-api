package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.CourseResourceDTO;
import com.csu.unicorp.entity.CourseResource;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.mapper.CourseResourceMapper;
import com.csu.unicorp.mapper.DualTeacherCourseMapper;
import com.csu.unicorp.service.CourseResourceService;
import com.csu.unicorp.vo.CourseResourceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 课程资源服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseResourceServiceImpl extends ServiceImpl<CourseResourceMapper, CourseResource> implements CourseResourceService {

    private final CourseResourceMapper resourceMapper;
    private final DualTeacherCourseMapper courseMapper;
    
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
        vo.setUploaderName(username);
        
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
    }

    @Override
    public CourseResourceVO getResourceById(Integer resourceId) {
        CourseResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        
        return convertToVO(resource);
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
        IPage<CourseResource> resourcePage = resourceMapper.selectPageByCourseId(pageParam, courseId);
        
        // 转换为VO
        return resourcePage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String downloadResource(Integer resourceId) {
        CourseResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        
        // 增加下载次数
        resourceMapper.incrementDownloadCount(resourceId);
        
        return resource.getFilePath();
    }
    
    /**
     * 将实体转换为VO
     */
    private CourseResourceVO convertToVO(CourseResource resource) {
        CourseResourceVO vo = new CourseResourceVO();
        BeanUtils.copyProperties(resource, vo);
        
        // 获取上传者姓名（实际项目中应该从用户服务获取）
        vo.setUploaderName("用户" + resource.getUploaderId());
        
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
     * 从用户名获取用户ID（实际项目中应该从用户服务获取）
     */
    private Integer getUserId(String username) {
        // 模拟实现，实际项目中应该从用户服务获取
        return 1;
    }
    
    /**
     * 获取用户类型（实际项目中应该从用户服务获取）
     */
    private String getUserType(UserDetails userDetails) {
        // 模拟实现，实际项目中应该从用户服务获取
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            return "TEACHER";
        } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EN_TEACHER"))) {
            return "MENTOR";
        }
        return "UNKNOWN";
    }
} 