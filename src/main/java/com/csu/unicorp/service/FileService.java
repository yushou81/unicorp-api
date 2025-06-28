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
     * @return 文件的访问URL
     */
    String uploadFile(MultipartFile file, String type);
} 