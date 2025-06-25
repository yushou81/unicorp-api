package com.csu.linkneiapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 商户注册数据传输对象
 */
@Data
@Schema(description = "商户注册数据传输对象")
public class MerchantRegisterDTO {
    @NotBlank(message = "商户名称不能为空")
    @Schema(description = "商户/店铺名称", example = "张三烧烤", required = true)
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

    @Schema(description = "纬度", example = "29.5866")
    private String latitude;

    @Schema(description = "经度", example = "105.0584")
    private String longitude;
} 