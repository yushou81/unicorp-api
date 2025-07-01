package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 头像上传DTO
 */
@Data
@Schema(description = "头像上传请求")
public class AvatarUploadDTO {
    // 使用MultipartFile在Controller中直接接收文件，不需要在DTO中定义字段
} 