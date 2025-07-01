package com.csu.unicorp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 实验设备创建DTO
 */
@Data
public class EquipmentCreationDTO {
    
    @NotBlank(message = "设备名称不能为空")
    @Size(max = 100, message = "设备名称不能超过100个字符")
    private String name;
    
    @NotBlank(message = "设备描述不能为空")
    @Size(max = 500, message = "设备描述不能超过500个字符")
    private String description;
    
    private String imageUrl;
    
    @NotBlank(message = "设备位置不能为空")
    @Size(max = 200, message = "设备位置不能超过200个字符")
    private String location;
    
    @NotNull(message = "所属组织ID不能为空")
    private Integer organizationId;
} 