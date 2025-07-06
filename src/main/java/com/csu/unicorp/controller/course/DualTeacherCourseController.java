package com.csu.unicorp.controller.course;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseEnrollmentDTO;
import com.csu.unicorp.dto.DualTeacherCourseDTO;
import com.csu.unicorp.service.DualTeacherCourseService;
import com.csu.unicorp.vo.DualTeacherCourseVO;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.UserVO;
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

import java.util.List;

/**
 * 双师课堂控制器
 */
@RestController
@RequestMapping("/v1/dual-courses")
@RequiredArgsConstructor
@Tag(name = "双师课堂", description = "双师课堂API，提供课程创建、查询、报名等功能")
public class DualTeacherCourseController {

    private final DualTeacherCourseService courseService;

    /**
     * 创建双师课堂课程
     */
    @Operation(summary = "创建双师课堂课程", description = "创建新的双师课堂课程，需要教师或学校管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "课程创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误或权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN')")
    public ResultVO<DualTeacherCourseVO> createCourse(@RequestBody @Validated DualTeacherCourseDTO courseDTO, 
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        DualTeacherCourseVO course = courseService.createCourse(courseDTO, userDetails);
        return ResultVO.success("课程创建成功", course);
    }
    
    /**
     * 更新课程信息
     */
    @Operation(summary = "更新双师课堂课程信息", description = "更新现有双师课堂课程的信息，需要该课程的教师、企业导师或学校管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "课程更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误或权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "课程不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'EN_TEACHER', 'SCH_ADMIN')")
    public ResultVO<DualTeacherCourseVO> updateCourse(@PathVariable Integer id, 
                                                     @RequestBody @Validated DualTeacherCourseDTO courseDTO, 
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        DualTeacherCourseVO updated = courseService.updateCourse(id, courseDTO, userDetails);
        return ResultVO.success("课程更新成功", updated);
    }
    
    /**
     * 获取课程详情
     */
    @Operation(summary = "获取双师课堂课程详情", description = "根据ID获取课程详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取课程详情成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "课程不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/{id}")
    public ResultVO<DualTeacherCourseVO> getCourseById(@PathVariable Integer id) {
        DualTeacherCourseVO course = courseService.getCourseById(id);
        return ResultVO.success("获取课程详情成功", course);
    }
    
    /**
     * 删除课程
     */
    @Operation(summary = "删除双师课堂课程", description = "删除指定的双师课堂课程，需要该课程的教师或学校管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "课程删除成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "课程不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN')")
    public ResultVO<Void> deleteCourse(@PathVariable Integer id, 
                                      @AuthenticationPrincipal UserDetails userDetails) {
        courseService.deleteCourse(id, userDetails);
        return ResultVO.success("课程删除成功");
    }
    
    /**
     * 获取教师创建的课程列表
     */
    @Operation(summary = "获取教师创建的课程列表", description = "获取当前教师用户创建的所有课程")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取课程列表成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "用户不是教师", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER', 'SCH_ADMIN')")
    public ResultVO<IPage<DualTeacherCourseVO>> getTeacherCourses(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        IPage<DualTeacherCourseVO> courses = courseService.getTeacherCourses(page, size, userDetails);
        return ResultVO.success("获取教师课程列表成功", courses);
    }
    
    /**
     * 获取企业导师参与的课程列表
     */
    @Operation(summary = "获取企业导师参与的课程列表", description = "获取当前企业导师用户参与的所有课程")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取课程列表成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "用户不是企业导师", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/mentor")
    @PreAuthorize("hasRole('EN_TEACHER')")
    public ResultVO<IPage<DualTeacherCourseVO>> getMentorCourses(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        IPage<DualTeacherCourseVO> courses = courseService.getMentorCourses(page, size, userDetails);
        return ResultVO.success("获取企业导师课程列表成功", courses);
    }
    
    /**
     * 获取可报名的课程列表
     */
    @Operation(summary = "获取可报名的课程列表", description = "获取当前可供学生报名的所有课程")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取课程列表成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/enrollable")
    public ResultVO<IPage<DualTeacherCourseVO>> getEnrollableCourses(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        IPage<DualTeacherCourseVO> courses = courseService.getEnrollableCourses(page, size);
        return ResultVO.success("获取可报名课程列表成功", courses);
    }
    
    /**
     * 学生报名课程
     */
    @Operation(summary = "学生报名课程", description = "学生用户报名双师课堂课程")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "报名成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "报名失败，可能的原因：用户不是学生、课程不存在、课程未开放报名、已报名该课程、课程人数已满", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Void> enrollCourse(@RequestBody @Validated CourseEnrollmentDTO enrollmentDTO, 
                                       @AuthenticationPrincipal UserDetails userDetails) {
        courseService.enrollCourse(enrollmentDTO, userDetails);
        return ResultVO.success("课程报名成功");
    }
    
    /**
     * 学生取消报名
     */
    @Operation(summary = "学生取消报名", description = "学生用户取消已报名的课程")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "取消报名成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "取消报名失败，可能的原因：用户未报名该课程、课程已开始或已结束", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @DeleteMapping("/enroll/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Void> cancelEnrollment(@PathVariable Integer courseId, 
                                          @AuthenticationPrincipal UserDetails userDetails) {
        courseService.cancelEnrollment(courseId, userDetails);
        return ResultVO.success("取消报名成功");
    }
    
    /**
     * 获取学生已报名的课程列表
     */
    @Operation(summary = "获取学生已报名的课程列表", description = "获取当前学生用户已报名的所有课程")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取课程列表成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "用户不是学生", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/enrolled")
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<IPage<DualTeacherCourseVO>> getStudentEnrolledCourses(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        IPage<DualTeacherCourseVO> courses = courseService.getStudentEnrolledCourses(page, size, userDetails);
        return ResultVO.success("获取已报名课程列表成功", courses);
    }
    
    /**
     * 更新课程状态
     */
    @Operation(summary = "更新课程状态", description = "更新双师课堂课程的状态，需要该课程的教师或学校管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "状态更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误或权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "课程不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN')")
    public ResultVO<DualTeacherCourseVO> updateCourseStatus(
            @PathVariable Integer id,
            @Parameter(description = "新状态值，可选：planning, open, in_progress, completed, cancelled") 
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        DualTeacherCourseVO updated = courseService.updateCourseStatus(id, status, userDetails);
        return ResultVO.success("课程状态更新成功", updated);
    }
    
    /**
     * 更新学生选课状态
     */
    @Operation(summary = "更新学生选课状态", description = "更新学生选课状态，需要课程教师或学校管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "选课状态更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误或权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "选课记录不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PatchMapping("/enrollment/{enrollmentId}/status")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCH_ADMIN')")
    public ResultVO<Void> updateEnrollmentStatus(
            @PathVariable Integer enrollmentId,
            @Parameter(description = "新状态值，可选：enrolled, cancelled, completed") 
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        courseService.updateEnrollmentStatus(enrollmentId, status, userDetails);
        return ResultVO.success("选课状态更新成功");
    }

    /**
     * 获取课程学生列表
     */
    @Operation(summary = "获取课程学生列表", description = "获取指定课程ID的已报名学生列表，需要教师或管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取学生列表成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "课程不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('TEACHER', 'EN_TEACHER', 'SCH_ADMIN')")
    public ResultVO<List<UserVO>> getCourseStudents(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<UserVO> students = courseService.getCourseStudents(id, userDetails);
        return ResultVO.success("获取课程学生列表成功", students);
    }
} 