package com.csu.unicorp.service.impl;

import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${app.upload.dir:upload/resources}")
    private String uploadBaseDir;
    
    @Value("${app.upload.max-size:10485760}")
    private long maxFileSize; // 默认10MB
    
    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;

    @Override
    public String uploadFile(MultipartFile file, String type) {
        // 文件为空检查
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传的文件不能为空");
        }
        
        // 文件大小检查
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("文件大小超过限制，最大允许" + (maxFileSize / 1024 / 1024) + "MB");
        }
        
        // 确定文件存储目录
        String subDir;
        if ("avatar".equals(type)) {
            subDir = "avatars";
        } else if ("resume".equals(type)) {
            subDir = "resumes";
        } else {
            // 默认为资源文件
            subDir = "resources";
        }
        
        try {
            // 创建目录
            String uploadDir = uploadBaseDir + "/" + subDir;
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成唯一文件名
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String uniqueFilename = UUID.randomUUID().toString().substring(0, 8) + "_" + timestamp + fileExtension;
            
            // 保存文件
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);
            
            // 返回文件相对路径
            return subDir + "/" + uniqueFilename;
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public String getFullFileUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        return baseUrl + "/api/v1/files/" + relativePath;
    }
} 