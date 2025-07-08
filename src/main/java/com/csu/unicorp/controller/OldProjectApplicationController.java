// package com.csu.unicorp.controller;

// import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.csu.unicorp.dto.OldProjectApplicationCreationDTO;
// import com.csu.unicorp.dto.OldProjectApplicationStatusUpdateDTO;
// import com.csu.unicorp.service.OldProjectApplicationService;
// import com.csu.unicorp.vo.MyProjectApplicationDetailVO;
// import com.csu.unicorp.vo.OldProjectApplicationDetailVO;
// import com.csu.unicorp.vo.OldProjectVO;
// import com.csu.unicorp.vo.ResultVO;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.security.SecurityRequirement;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.web.bind.annotation.*;


// import java.util.List;
// import com.csu.unicorp.mapper.UserMapper;
// import com.csu.unicorp.entity.User;

// /**
//  * 项目申请控制器
//  */
// @Tag(name = "Project Applications", description = "项目申请与成员管理")
// @RestController
// @RequiredArgsConstructor
// public class OldProjectApplicationController {
    
//     private final OldProjectApplicationService projectApplicationService;
//     private final UserMapper userMapper;
    
//     /**
//      * 学生申请加入项目
//      */
//     @Operation(summary = "[学生] 申请加入项目", description = "学生用户提交加入某个项目的申请")
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "201", description = "申请成功", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class))),
//         @ApiResponse(responseCode = "400", description = "已申请过该项目", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class))),
//         @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class)))
//     })
//     @PostMapping("/v1/projects/{id}/apply")
//     @SecurityRequirement(name = "bearerAuth")
//     @PreAuthorize("hasRole('STUDENT')")
//     public ResultVO<OldProjectApplicationDetailVO> applyForProject(
//             @PathVariable("id") Integer projectId,
//             @Valid @RequestBody(required = false) OldProjectApplicationCreationDTO dto,
//             @AuthenticationPrincipal UserDetails userDetails) {
        
//         if (dto == null) {
//             dto = new OldProjectApplicationCreationDTO();
//         }
        
//         OldProjectApplicationDetailVO result = projectApplicationService.applyForProject(projectId, dto, userDetails);
//         return ResultVO.success("申请提交成功", result);
//     }
    
//     /**
//      * 项目所有者查看项目申请列表
//      */
//     @Operation(summary = "[所有者] 查看项目申请列表", description = "项目所有者查看指定项目的所有申请人列表")
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "200", description = "成功获取申请列表", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class))),
//         @ApiResponse(responseCode = "403", description = "权限不足", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class)))
//     })
//     @GetMapping("/v1/projects/{id}/applications")
//     @SecurityRequirement(name = "bearerAuth")
//     @PreAuthorize("hasAnyRole('TEACHER', 'EN_ADMIN', 'EN_TEACHER')")
//     public ResultVO<List<OldProjectApplicationDetailVO>> getProjectApplications(
//             @PathVariable("id") Integer projectId,
//             @AuthenticationPrincipal UserDetails userDetails) {
        
//         List<OldProjectApplicationDetailVO> applications = projectApplicationService.getProjectApplications(projectId, userDetails);
//         return ResultVO.success("获取申请列表成功", applications);
//     }
    
//     /**
//      * 项目所有者更新项目申请状态
//      */
//     @Operation(summary = "[所有者] 更新项目申请状态", description = "由项目所有者调用，用于更新某个项目申请的状态（如：批准、拒绝）")
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "200", description = "状态更新成功", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class))),
//         @ApiResponse(responseCode = "400", description = "无效的状态值", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class))),
//         @ApiResponse(responseCode = "403", description = "权限不足", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class))),
//         @ApiResponse(responseCode = "404", description = "申请未找到", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class)))
//     })
//     @PatchMapping("/v1/project-applications/{id}")
//     @SecurityRequirement(name = "bearerAuth")
//     @PreAuthorize("hasAnyRole('TEACHER', 'EN_ADMIN', 'EN_TEACHER','STUDENT')")
//     public ResultVO<OldProjectApplicationDetailVO> updateApplicationStatus(
//             @PathVariable("id") Integer applicationId,
//             @Valid @RequestBody OldProjectApplicationStatusUpdateDTO dto,
//             @AuthenticationPrincipal UserDetails userDetails) {
        
//         OldProjectApplicationDetailVO result = projectApplicationService.updateApplicationStatus(applicationId, dto, userDetails);
//         return ResultVO.success("申请状态更新成功", result);
//     }
    
//     /**
//      * 学生查看自己的项目申请
//      */
//     @Operation(summary = "[学生] 查看我的项目申请", description = "获取当前登录学生的所有项目申请记录及其最新状态")
//     @ApiResponses(value = {
//         @ApiResponse(responseCode = "200", description = "成功获取申请列表", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class))),
//         @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)", 
//                 content = @Content(mediaType = "application/json", 
//                 schema = @Schema(implementation = ResultVO.class)))
//     })
//     @GetMapping("/v1/me/project-applications")
//         @PreAuthorize("hasRole('STUDENT')")
//         public ResultVO<IPage<MyProjectApplicationDetailVO>> getMyApplications(
//         @RequestParam(defaultValue = "1") int page,
//         @RequestParam(defaultValue = "10") int size,
//         @RequestParam(required = false) String keyword,
//         @RequestParam(required = false) List<String> difficulty,
//         @RequestParam(required = false) List<String> supportLanguages,
//         @RequestParam(required = false) List<String> techFields,
//         @RequestParam(required = false) List<String> programmingLanguages,
//         @AuthenticationPrincipal UserDetails userDetails
//         ) {
//         // 通过 userDetails.getUsername() 查 userId
//         User user = userMapper.findByUsername(userDetails.getUsername());
//         Integer userId = user != null ? user.getId() : null;
//         IPage<MyProjectApplicationDetailVO> result = projectApplicationService.getMyProjectApplications(
//                 page, size, keyword, userId, difficulty, supportLanguages, techFields, programmingLanguages);
//         return ResultVO.success("获取申请列表成功", result);
//         }
// } 