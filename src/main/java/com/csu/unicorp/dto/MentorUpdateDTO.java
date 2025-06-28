package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 企业导师更新DTO
 */
@Data
@Schema(description = "企业导师更新DTO")
public class MentorUpdateDTO {
    
    @Schema(description = "导师昵称")
    private String nickname;
    
    @Schema(description = "导师手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
} 