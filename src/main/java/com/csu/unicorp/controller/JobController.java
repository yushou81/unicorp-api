package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.JobCreationDTO;
import com.csu.unicorp.service.JobService;
import com.csu.unicorp.vo.JobVO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 岗位控制器
 */
@Tag(name = "Jobs", description = "招聘岗位管理")
@RestController
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    
    private final JobService jobService;
    
    /**
     * 获取岗位列表
     */
    @Operation(summary = "获取岗位列表(公开)", description = "获取所有状态为'open'的岗位列表，支持分页和搜索")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取岗位列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JobVO.class)))
    })
    @GetMapping
    public ResponseEntity<ResultVO<IPage<JobVO>>> getJobs(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<JobVO> jobs = jobService.getJobList(page, size, keyword);
        return ResponseEntity.ok(ResultVO.success("获取岗位列表成功", jobs));
    }
    
    /**
     * 创建新岗位
     */
    @Operation(summary = "[企业] 创建新岗位", description = "由企业管理员或企业导师调用，用于发布一个新的招聘岗位")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "岗位创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JobVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    public ResponseEntity<ResultVO<JobVO>> createJob(
            @Valid @RequestBody JobCreationDTO jobCreationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        JobVO job = jobService.createJob(jobCreationDTO, userDetails);
        return new ResponseEntity<>(ResultVO.success("岗位创建成功", job), HttpStatus.CREATED);
    }
    
    /**
     * 获取特定岗位详情
     */
    @Operation(summary = "获取特定岗位详情", description = "根据ID获取岗位的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取岗位详情", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JobVO.class))),
        @ApiResponse(responseCode = "404", description = "岗位未找到")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResultVO<JobVO>> getJob(@PathVariable Integer id) {
        JobVO job = jobService.getJobById(id);
        return ResponseEntity.ok(ResultVO.success("获取岗位详情成功", job));
    }
    
    /**
     * 更新岗位信息
     */
    @Operation(summary = "[企业] 更新岗位信息", description = "更新已发布岗位的信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "岗位更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JobVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "岗位未找到")
    })
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    public ResponseEntity<ResultVO<JobVO>> updateJob(
            @PathVariable Integer id,
            @Valid @RequestBody JobCreationDTO jobCreationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        JobVO job = jobService.updateJob(id, jobCreationDTO, userDetails);
        return ResponseEntity.ok(ResultVO.success("岗位更新成功", job));
    }
    
    /**
     * 删除岗位
     */
    @Operation(summary = "[企业] 删除岗位", description = "逻辑删除一个岗位")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "岗位未找到")
    })
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        jobService.deleteJob(id, userDetails);
        return ResponseEntity.noContent().build();
    }
} 