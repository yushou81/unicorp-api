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
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    
    // 文件上传目录
    @Value("${file.upload-dir:upload}")
    private String uploadDir;
    
    // 文件访问基础URL
    @Value("${file.base-url:http://localhost:8081/api/files}")
    private String baseUrl;
    
    @Override
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传的文件不能为空");
        }
        
        try {
            // 创建上传目录（如果不存在）
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成唯一文件名
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = generateUniqueFilename(fileExtension);
            
            // 按日期组织目录结构
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path datePath = uploadPath.resolve(dateDir);
            if (!Files.exists(datePath)) {
                Files.createDirectories(datePath);
            }
            
            // 保存文件
            Path targetLocation = datePath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // 返回文件访问URL
            String relativePath = dateDir + "/" + uniqueFilename;
            String fileUrl = baseUrl + "/" + relativePath;
            
            log.info("文件上传成功: {}", fileUrl);
            return fileUrl;
        } catch (IOException ex) {
            log.error("文件上传失败", ex);
            throw new BusinessException("文件上传失败: " + ex.getMessage());
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename.lastIndexOf(".") != -1) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
    
    /**
     * 生成唯一文件名
     */
    private String generateUniqueFilename(String fileExtension) {
        return UUID.randomUUID().toString() + fileExtension;
    }
} 