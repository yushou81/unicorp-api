package com.csu.unicorp.controller;

import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.PortfolioItemCreationDTO;
import com.csu.unicorp.service.PortfolioService;
import com.csu.unicorp.vo.PortfolioItemVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作品集控制器
 */
@Tag(name = "Portfolio", description = "学生作品集管理")
@RestController
@RequestMapping("/v1/me/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    
    private final PortfolioService portfolioService;
    
    /**
     * 获取我的作品集列表
     */
    @Operation(summary = "[学生] 获取我的作品集列表", description = "获取当前登录学生的所有作品集项目。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取作品集列表",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PortfolioItemVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<ResultVO<List<PortfolioItemVO>>> getMyPortfolioItems(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PortfolioItemVO> portfolioItems = portfolioService.getPortfolioItems(userDetails.getUser().getId());
        return ResponseEntity.ok(ResultVO.success("获取作品集列表成功", portfolioItems));
    }
    
    /**
     * 添加新的作品集项目
     */
    @Operation(summary = "[学生] 添加新的作品集项目", description = "添加一个新的作品集项目到当前登录学生的作品集中。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "作品集项目创建成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PortfolioItemVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ResultVO<PortfolioItemVO>> addPortfolioItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PortfolioItemCreationDTO portfolioItemCreationDTO) {
        PortfolioItemVO portfolioItem = portfolioService.addPortfolioItem(
                userDetails.getUser().getId(), portfolioItemCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResultVO.success("作品集项目创建成功", portfolioItem));
    }
    
    /**
     * 更新作品集项目
     */
    @Operation(summary = "[学生] 更新一个作品集项目", description = "更新当前登录学生的一个作品集项目。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PortfolioItemVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "项目未找到",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{itemId}")
    public ResponseEntity<ResultVO<PortfolioItemVO>> updatePortfolioItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer itemId,
            @Valid @RequestBody PortfolioItemCreationDTO portfolioItemCreationDTO) {
        PortfolioItemVO portfolioItem = portfolioService.updatePortfolioItem(
                userDetails.getUser().getId(), itemId, portfolioItemCreationDTO);
        return ResponseEntity.ok(ResultVO.success("作品集项目更新成功", portfolioItem));
    }
    
    /**
     * 删除作品集项目
     */
    @Operation(summary = "[学生] 删除一个作品集项目", description = "删除当前登录学生的一个作品集项目。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deletePortfolioItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer itemId) {
        portfolioService.deletePortfolioItem(userDetails.getUser().getId(), itemId);
        return ResponseEntity.noContent().build();
    }
} 