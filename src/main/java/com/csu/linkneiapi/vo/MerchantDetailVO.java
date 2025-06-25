package com.csu.linkneiapi.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商户详情视图对象
 * 用于商户详情页展示的完整信息，包括产品列表
 */
@Data
@Schema(description = "商户详情视图对象")
public class MerchantDetailVO {
    
    @Schema(description = "商户ID", example = "1")
    private Long id;
    
    @Schema(description = "商户名称", example = "张三烧烤")
    private String name;
    
    @Schema(description = "商户地址", example = "内江市东兴区XX路XX号")
    private String address;
    
    @Schema(description = "联系电话", example = "13888888888")
    private String phone;
    
    @Schema(description = "商户简介/描述", example = "本店炭火烧烤，秘制酱料，欢迎品尝！")
    private String description;
    
    @Schema(description = "商户Logo图片地址", example = "https://example.com/logo.jpg")
    private String logoUrl;
    
    @Schema(description = "营业时间", example = "10:00-22:00")
    private String businessHours;
    
    @Schema(description = "该商户的产品列表")
    private List<ProductSummaryVO> products;
} 