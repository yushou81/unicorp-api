package com.csu.unicorp.controller.course;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseRatingDTO;
import com.csu.unicorp.service.CourseRatingService;
import com.csu.unicorp.vo.CourseRatingVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 课程评价控制器
 */
@RestController
@RequestMapping("/v1/course-ratings")
@RequiredArgsConstructor
@Tag(name = "课程评价", description = "课程评价API，提供评价提交、查询等功能")
public class CourseRatingController {

    private final CourseRatingService ratingService;
    
    /**
     * 提交课程评价
     */
    @Operation(summary = "提交课程评价", description = "学生提交对已完成课程的评价")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "评价提交成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误或权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<CourseRatingVO> submitRating(
            @RequestBody @Validated CourseRatingDTO ratingDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        CourseRatingVO rating = ratingService.submitRating(ratingDTO, userDetails);
        return ResultVO.success("评价提交成功", rating);
    }
    
    /**
     * 更新课程评价
     */
    @Operation(summary = "更新课程评价", description = "学生更新已提交的课程评价")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "评价更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误或权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "评价不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PutMapping("/{ratingId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<CourseRatingVO> updateRating(
            @PathVariable Integer ratingId,
            @RequestBody @Validated CourseRatingDTO ratingDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        CourseRatingVO rating = ratingService.updateRating(ratingId, ratingDTO, userDetails);
        return ResultVO.success("评价更新成功", rating);
    }
    
    /**
     * 删除课程评价
     */
    @Operation(summary = "删除课程评价", description = "学生删除已提交的课程评价")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "评价删除成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "评价不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @DeleteMapping("/{ratingId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Void> deleteRating(
            @PathVariable Integer ratingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        ratingService.deleteRating(ratingId, userDetails);
        return ResultVO.success("评价删除成功");
    }
    
    /**
     * 获取课程评价详情
     */
    @Operation(summary = "获取课程评价详情", description = "根据ID获取课程评价详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取评价详情成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "评价不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/{ratingId}")
    public ResultVO<CourseRatingVO> getRatingById(@PathVariable Integer ratingId) {
        CourseRatingVO rating = ratingService.getRatingById(ratingId);
        return ResultVO.success("获取评价详情成功", rating);
    }
    
    /**
     * 获取课程评价列表
     */
    @Operation(summary = "获取课程评价列表", description = "获取指定课程的所有评价")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取评价列表成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "课程不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/course/{courseId}")
    public ResultVO<IPage<CourseRatingVO>> getRatingsByCourseId(
            @PathVariable Integer courseId,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        IPage<CourseRatingVO> ratings = ratingService.getRatingsByCourseId(courseId, page, size);
        return ResultVO.success("获取评价列表成功", ratings);
    }
    
    /**
     * 获取课程平均评分
     */
    @Operation(summary = "获取课程平均评分", description = "获取指定课程的平均评分")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取平均评分成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "课程不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/average/{courseId}")
    public ResultVO<Double> getAverageRating(@PathVariable Integer courseId) {
        Double avgRating = ratingService.getAverageRating(courseId);
        return ResultVO.success("获取平均评分成功", avgRating);
    }
    
    /**
     * 检查学生是否已评价课程
     */
    @Operation(summary = "检查学生是否已评价课程", description = "检查当前学生是否已对指定课程进行评价")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "检查成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/check/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Boolean> hasRated(
            @PathVariable Integer courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean hasRated = ratingService.hasRated(courseId, userDetails);
        return ResultVO.success("检查成功", hasRated);
    }
} 