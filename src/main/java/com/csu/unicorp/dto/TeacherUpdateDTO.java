package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 教师更新DTO
 */
@Data
@Schema(description = "教师更新DTO")
public class TeacherUpdateDTO {
    
    @Schema(description = "教师昵称")
    private String nickname;
    
    @Schema(description = "教师手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
} 