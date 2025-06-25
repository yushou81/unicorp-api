package com.csu.linkneiapi.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户列表项视图对象
 * 用于商户列表页展示的简要信息
 */
@Data
@Schema(description = "商户列表项视图对象")
public class MerchantSummaryVO {
    
    @Schema(description = "商户ID", example = "1")
    private Long id;
    
    @Schema(description = "商户名称", example = "张三烧烤")
    private String name;
    
    @Schema(description = "商户地址", example = "内江市东兴区XX路XX号")
    private String address;
    
    @Schema(description = "商户Logo图片地址", example = "https://example.com/logo.jpg")
    private String logoUrl;
    
    @Schema(description = "平均评分", example = "4.5")
    private BigDecimal averageRating;
} 