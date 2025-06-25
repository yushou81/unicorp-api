package com.csu.linkneiapi.controller;

import com.csu.linkneiapi.service.MerchantService;
import com.csu.linkneiapi.vo.MerchantDetailVO;
import com.csu.linkneiapi.vo.MerchantSummaryVO;
import com.csu.linkneiapi.vo.PageResultVO;
import com.csu.linkneiapi.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商户Controller
 */
@RestController
@RequestMapping("/api/merchants")
@Tag(name = "Merchant", description = "商户相关操作")
@Validated
public class MerchantController {
    
    private final MerchantService merchantService;
    
    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }
    
    /**
     * 获取商户列表 (分页)
     */
    @GetMapping("/list")
    @Operation(summary = "获取商户列表 (分页)", description = "用于在首页或商户列表页，分页获取平台上的商户简要信息。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取商户列表",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<PageResultVO<MerchantSummaryVO>> getMerchantList(
            @Parameter(description = "请求的页码", schema = @Schema(defaultValue = "1"))
            @RequestParam(required = false, defaultValue = "1") 
            @Min(value = 1, message = "页码必须大于等于1") Integer page,
            
            @Parameter(description = "每页的数据条数", schema = @Schema(defaultValue = "10"))
            @RequestParam(required = false, defaultValue = "10") 
            @Min(value = 1, message = "每页条数必须大于等于1")
            @Max(value = 100, message = "每页条数不能超过100") Integer pageSize) {
        
        PageResultVO<MerchantSummaryVO> pageResult = merchantService.getMerchantList(page, pageSize);
        return ResultVO.success(pageResult);
    }
    
    /**
     * 根据ID获取商户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取商户详情", description = "用于在用户点击某个商户后，获取该商户的完整详细信息，包括其发布的产品列表。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取商户详情",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数验证失败",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "商户未找到",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<MerchantDetailVO> getMerchantDetail(
            @Parameter(description = "商户的唯一ID", required = true)
            @PathVariable 
            @Positive(message = "商户ID必须为正数") Long id) {
        
        MerchantDetailVO merchantDetail = merchantService.getMerchantDetail(id);
        return ResultVO.success(merchantDetail);
    }
} 