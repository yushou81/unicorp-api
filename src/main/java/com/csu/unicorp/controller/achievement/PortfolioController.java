package com.csu.unicorp.controller.achievement;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.utils.RequestUtils;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.achievement.PortfolioItemCreationDTO;
import com.csu.unicorp.dto.achievement.PortfolioResourceUploadDTO;
import com.csu.unicorp.service.PortfolioService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.achievement.PortfolioItemVO;
import com.csu.unicorp.vo.achievement.PortfolioResourceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 作品集Controller
 */
@RestController
@RequestMapping("/v1/portfolio")
@RequiredArgsConstructor
@Tag(name = "作品集管理", description = "作品集管理相关接口")
public class PortfolioController {
    
    private final PortfolioService portfolioService;
    
    @GetMapping("/items")
    @Operation(summary = "获取作品列表", description = "获取当前用户的作品列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<List<PortfolioItemVO>> getPortfolioItems(@AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        List<PortfolioItemVO> portfolioItems = portfolioService.getPortfolioItems(userId);
        return ResultVO.success(portfolioItems);
    }
    
    @GetMapping("/items/page")
    @Operation(summary = "分页获取作品列表", description = "分页获取当前用户的作品列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Page<PortfolioItemVO>> getPortfolioItemPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        Page<PortfolioItemVO> portfolioItemPage = portfolioService.getPortfolioItemPage(userId, page - 1, size);
        return ResultVO.success(portfolioItemPage);
    }
    
    @GetMapping("/public/items")
    @Operation(summary = "获取公开作品列表", description = "分页获取公开的作品列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<PortfolioItemVO>> getPublicPortfolioItemPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PortfolioItemVO> portfolioItemPage = portfolioService.getPublicPortfolioItemPage(page - 1, size);
        return ResultVO.success(portfolioItemPage);
    }
    
    @GetMapping("/public/items/category/{category}")
    @Operation(summary = "根据分类获取公开作品列表", description = "根据分类分页获取公开的作品列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<PortfolioItemVO>> getPublicPortfolioItemPageByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<PortfolioItemVO> portfolioItemPage = portfolioService.getPublicPortfolioItemPageByCategory(category, page - 1, size);
        return ResultVO.success(portfolioItemPage);
    }
    
    @GetMapping("/items/{id}")
    @Operation(summary = "获取作品详情", description = "根据ID获取作品详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "作品不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<PortfolioItemVO> getPortfolioItemDetail(
            @PathVariable Integer id,
            HttpServletRequest request) {
        String viewerIp = RequestUtils.getClientIp(request);
        PortfolioItemVO portfolioItem = portfolioService.getPortfolioItemDetail(id, viewerIp);
        return ResultVO.success(portfolioItem);
    }
    
    @PostMapping(value = "/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "创建作品", description = "创建新的作品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "创建成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<PortfolioItemVO> createPortfolioItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "projectUrl", required = false) String projectUrl,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "teamMembers", required = false) String teamMembers,
            @RequestParam(value = "isPublic", required = false, defaultValue = "true") Boolean isPublic,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        
        // 创建DTO对象
        PortfolioItemCreationDTO portfolioItemCreationDTO = new PortfolioItemCreationDTO();
        portfolioItemCreationDTO.setTitle(title);
        portfolioItemCreationDTO.setDescription(description);
        portfolioItemCreationDTO.setProjectUrl(projectUrl);
        portfolioItemCreationDTO.setCategory(category);
        portfolioItemCreationDTO.setTags(tags);
        portfolioItemCreationDTO.setTeamMembers(teamMembers);
        portfolioItemCreationDTO.setIsPublic(isPublic);
        
        PortfolioItemVO portfolioItem = portfolioService.createPortfolioItem(userId, portfolioItemCreationDTO, coverImage);
        return ResultVO.success(portfolioItem);
    }
    
    @PutMapping(value = "/items/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "更新作品", description = "更新指定ID的作品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "作品不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<PortfolioItemVO> updatePortfolioItem(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "projectUrl", required = false) String projectUrl,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "teamMembers", required = false) String teamMembers,
            @RequestParam(value = "isPublic", required = false, defaultValue = "true") Boolean isPublic,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        
        // 创建DTO对象
        PortfolioItemCreationDTO portfolioItemCreationDTO = new PortfolioItemCreationDTO();
        portfolioItemCreationDTO.setTitle(title);
        portfolioItemCreationDTO.setDescription(description);
        portfolioItemCreationDTO.setProjectUrl(projectUrl);
        portfolioItemCreationDTO.setCategory(category);
        portfolioItemCreationDTO.setTags(tags);
        portfolioItemCreationDTO.setTeamMembers(teamMembers);
        portfolioItemCreationDTO.setIsPublic(isPublic);
        
        PortfolioItemVO portfolioItem = portfolioService.updatePortfolioItem(id, userId, portfolioItemCreationDTO, coverImage);
        return ResultVO.success(portfolioItem);
    }
    
    @DeleteMapping("/items/{id}")
    @Operation(summary = "删除作品", description = "删除指定ID的作品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "作品不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Boolean> deletePortfolioItem(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        boolean result = portfolioService.deletePortfolioItem(id, userId);
        return ResultVO.success(result);
    }
    
    @PostMapping(value = "/items/{id}/resources", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传作品资源", description = "为指定作品上传资源文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "上传成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "作品不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<PortfolioResourceVO> uploadResource(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("resourceType") String resourceType,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart("file") MultipartFile file) {
        
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        
        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            return ResultVO.error("上传的文件不能为空");
        }
        
        // 处理文件上传
        PortfolioResourceVO resource = portfolioService.uploadResourceFile(id, userId, file, resourceType, description);
        
        return ResultVO.success(resource);
    }
    
    @DeleteMapping("/items/{portfolioItemId}/resources/{resourceId}")
    @Operation(summary = "删除作品资源", description = "删除指定作品的指定资源")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "作品或资源不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('STUDENT')")
    public ResultVO<Boolean> deleteResource(
            @PathVariable Integer portfolioItemId,
            @PathVariable Integer resourceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        boolean result = portfolioService.deleteResource(portfolioItemId, resourceId, userId);
        return ResultVO.success(result);
    }
    
    @PostMapping("/items/{id}/like")
    @Operation(summary = "点赞作品", description = "为指定作品点赞")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "点赞成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "作品不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> likePortfolioItem(@PathVariable Integer id) {
        boolean result = portfolioService.likePortfolioItem(id);
        return ResultVO.success(result);
    }
    
    @GetMapping("/school/items")
    @Operation(summary = "获取学校学生作品列表", description = "获取当前教师或管理员所属学校的学生作品列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<Page<PortfolioItemVO>> getSchoolStudentPortfolioItems(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        Page<PortfolioItemVO> portfolioItems = portfolioService.getSchoolStudentPortfolioItems(userId, page - 1, size);
        return ResultVO.success(portfolioItems);
    }
    
    @GetMapping("/school/items/category/{category}")
    @Operation(summary = "根据分类获取学校学生作品列表", description = "根据分类获取当前教师或管理员所属学校的学生作品列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<Page<PortfolioItemVO>> getSchoolStudentPortfolioItemsByCategory(
            @PathVariable String category,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        Page<PortfolioItemVO> portfolioItems = portfolioService.getSchoolStudentPortfolioItemsByCategory(userId, category, page - 1, size);
        return ResultVO.success(portfolioItems);
    }
    
    @GetMapping("/school/items/by-student")
    @Operation(summary = "获取学校指定学生的作品列表", description = "获取学校指定学生的作品列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<List<PortfolioItemVO>> getSchoolStudentPortfolioItemsByStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer studentId) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        List<PortfolioItemVO> portfolioItems = portfolioService.getSchoolStudentPortfolioItemsByStudent(userId, studentId);
        return ResultVO.success(portfolioItems);
    }
    
    @GetMapping("/school/statistics")
    @Operation(summary = "获取学校作品统计数据", description = "获取当前教师或管理员所属学校的作品统计数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('SCH_ADMIN')")
    public ResultVO<Map<String, Object>> getSchoolPortfolioStatistics(
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer userId = ((CustomUserDetails) userDetails).getUserId();
        Map<String, Object> statistics = portfolioService.getSchoolPortfolioStatistics(userId);
        return ResultVO.success(statistics);
    }
} 