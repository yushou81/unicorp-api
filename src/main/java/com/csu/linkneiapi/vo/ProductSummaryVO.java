package com.csu.linkneiapi.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品简要信息视图对象
 * 用于商户详情中展示的产品列表项
 */
@Data
@Schema(description = "产品简要信息视图对象")
public class ProductSummaryVO {
    
    @Schema(description = "产品ID", example = "101")
    private Long id;
    
    @Schema(description = "产品名称", example = "招牌烤五花肉")
    private String name;
    
    @Schema(description = "产品价格", example = "2.5")
    private BigDecimal price;
    
    @Schema(description = "产品图片地址", example = "https://example.com/wuhuarou.jpg")
    private String imageUrl;
} 