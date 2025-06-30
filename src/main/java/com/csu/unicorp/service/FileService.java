package com.csu.unicorp.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     * 
     * @param file 上传的文件
     * @param type 文件类型（avatar, resume, resource等）
     * @return 文件的相对路径，例如 "resumes/filename.docx"
     */
    String uploadFile(MultipartFile file, String type);
    
    /**
     * 根据相对路径获取文件的完整URL
     * 
     * @param relativePath 文件的相对路径
     * @return 文件的完整访问URL
     */
    String getFullFileUrl(String relativePath);
    
    /**
     * 获取随机默认头像的相对路径
     * 
     * @return 默认头像的相对路径，例如 "avatar/default/Member007.jpg"
     */
    String getRandomDefaultAvatarPath();
} 