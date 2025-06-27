package com.csu.unicorp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目申请状态更新数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectApplicationStatusUpdateDTO {
    
    /**
     * 申请状态
     */
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "viewed|approved|rejected", message = "状态值必须为viewed、approved或rejected之一")
    private String status;
} 