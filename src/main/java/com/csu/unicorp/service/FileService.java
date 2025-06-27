package com.csu.unicorp.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     * 
     * @param file 上传的文件
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file);
} 