package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseResourceDTO;
import com.csu.unicorp.vo.CourseResourceVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 课程资源服务接口
 */
public interface CourseResourceService {
    
    /**
     * 上传课程资源
     * @param file 资源文件
     * @param resourceDTO 资源信息
     * @param userDetails 当前用户
     * @return 资源视图对象
     * @throws IOException 文件处理异常
     */
    CourseResourceVO uploadResource(MultipartFile file, CourseResourceDTO resourceDTO, UserDetails userDetails) throws IOException;
    
    /**
     * 删除课程资源
     * @param resourceId 资源ID
     * @param userDetails 当前用户
     */
    void deleteResource(Integer resourceId, UserDetails userDetails);
    
    /**
     * 获取课程资源详情
     * @param resourceId 资源ID
     * @return 资源视图对象
     */
    CourseResourceVO getResourceById(Integer resourceId);
    
    /**
     * 分页获取课程资源列表
     * @param courseId 课程ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<CourseResourceVO> getResourcesByCourseId(Integer courseId, int page, int size);
    
    /**
     * 下载课程资源
     * @param resourceId 资源ID
     * @return 资源文件路径
     */
    String downloadResource(Integer resourceId);
} 