package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "企业信息的标准视图对象")
public class EnterpriseVO {

    @Schema(description = "企业ID", example = "101")
    private Long id;
    
    @Schema(description = "企业全称", example = "华迪信息技术有限公司")
    private String name;
    
    @Schema(description = "完整的企业Logo URL", example = "http://localhost:8081/static/logos/enterprise/huadi.png")
    private String logoUrl;
    
    @Schema(description = "所属行业", example = "信息技术与服务")
    private String industry;
    
    @Schema(description = "企业状态，新创建的默认为待审核", example = "PENDING_REVIEW")
    private String status;
} 