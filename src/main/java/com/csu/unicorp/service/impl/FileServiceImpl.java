package com.csu.unicorp.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import com.csu.unicorp.entity.FileMapping;
import com.csu.unicorp.mapper.FileMappingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    
    @Value("${server.external-ip:#{null}}")
    private String externalIp;
    
    @Value("${server.port:8081}")
    private int serverPort;
    
    private String getBaseUrl() {
        if (externalIp == null) {
            return null;
        }
        return "http://" + externalIp + ":" + serverPort;
    }

    @Autowired
    private FileMappingMapper fileMappingMapper;
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
        if ("avatars".equals(type)) {
            subDir = "avatars";
        } else if ("resume".equals(type)) {
            subDir = "resumes";
        } else if ("logo".equals(type)) {
            subDir = "logos";
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

             // 保存映射
            FileMapping mapping = new FileMapping();
            mapping.setStoredName(subDir + "/" + uniqueFilename); // 存储名
            mapping.setOriginalName(originalFilename);            // 真实名
            mapping.setType(type);
            fileMappingMapper.insert(mapping);
            
            // 返回文件相对路径
            return subDir + "/" + uniqueFilename;
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            // 如果提供的是完整URL，转换为相对路径
            String relativePath = fileUrl;
            if (fileUrl.startsWith(getBaseUrl())) {
                relativePath = fileUrl.substring(fileUrl.indexOf("/api/v1/files/") + "/api/v1/files/".length());
            }
            
            // 构建文件路径
            Path filePath = Paths.get(uploadBaseDir).resolve(relativePath).normalize();
            
            // 检查文件是否存在
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                log.warn("要删除的文件不存在: {}", filePath);
                return false;
            }
            
            // 删除文件
            Files.delete(filePath);
            log.info("文件已成功删除: {}", filePath);
            return true;
        } catch (IOException e) {
            log.error("删除文件失败: {}", fileUrl, e);
            return false;
        }
    }
    
    @Override
    public String getFullFileUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        return getBaseUrl() + "/api/v1/files/" + relativePath;
    }


    @Override
    public String getRandomDefaultAvatarPath() {
        try {
            // 默认头像目录
            Path defaultAvatarDir = Paths.get(uploadBaseDir, "avatars/default");

            // 检查目录是否存在
            if (!Files.exists(defaultAvatarDir)) {
                log.warn("默认头像目录不存在: {}", defaultAvatarDir);
                return null;
            }

            // 获取所有默认头像文件
            List<Path> avatarFiles = Files.list(defaultAvatarDir)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            if (avatarFiles.isEmpty()) {
                log.warn("默认头像目录为空");
                return null;
            }

            // 随机选择一个头像
            int randomIndex = new Random().nextInt(avatarFiles.size());
            Path selectedAvatar = avatarFiles.get(randomIndex);

            // 返回相对路径
            return "avatars/default/" + selectedAvatar.getFileName().toString();
        } catch (IOException e) {
            log.error("获取随机默认头像失败", e);
            return null;
        }
    }

    
    @Override
    public Resource loadFileAsResource(String fileUrl) {
        try {
            // 如果提供的是完整URL，转换为相对路径
            String relativePath = fileUrl;
            if (fileUrl.startsWith(getBaseUrl())) {
                relativePath = fileUrl.substring(fileUrl.indexOf("/api/v1/files/") + "/api/v1/files/".length());
            }
            
            // 构建文件路径
            Path filePath = Paths.get(uploadBaseDir).resolve(relativePath).normalize();
            
            // 检查路径是否合法（防止目录遍历攻击）
            if (!filePath.toFile().exists()) {
                throw new BusinessException("文件不存在: " + relativePath);
            }
            
            // 创建资源
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new BusinessException("文件不存在: " + relativePath);
            }
        } catch (MalformedURLException e) {
            throw new BusinessException("文件路径有误: " + e.getMessage());
        }
    }
    
    @Override
    public String getFilenameFromUrl(String fileUrl) {
        // 从URL中提取文件名
        String fileName = fileUrl;
        
        // 如果是完整URL，获取最后一个斜杠后的内容
        if (fileUrl.contains("/")) {
            fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        }
        
        // 如果文件名包含时间戳格式 (UUID_timestamp.ext)，尝试提取原始文件名
        if (fileName.contains("_") && fileName.length() > 20) {
            // 提取扩展名
            String extension = "";
            if (fileName.contains(".")) {
                extension = fileName.substring(fileName.lastIndexOf("."));
            }
            
            // 生成更友好的文件名
            return "resource" + extension;
        }
        
        return fileName;

    }
} 