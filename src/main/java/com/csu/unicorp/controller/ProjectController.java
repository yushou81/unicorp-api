package com.csu.unicorp.controller;

import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.*;
import com.csu.unicorp.vo.*;
import com.csu.unicorp.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.csu.unicorp.common.exception.BusinessException;

/**
 * 项目合作管理模块 Controller
 */
@RestController
@RequestMapping("/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectApplicationService applicationService;
    private final ProjectProgressService progressService;
    private final ProjectClosureService closureService;
    private final ProjectDocumentService documentService;
    private final ProjectFundService fundService;
    private final ProjectLogService logService;

    public ProjectController(ProjectService projectService,
                            ProjectApplicationService applicationService,
                            ProjectProgressService progressService,
                            ProjectClosureService closureService,
                            ProjectDocumentService documentService,
                            ProjectFundService fundService,
                            ProjectLogService logService) {
        this.projectService = projectService;
        this.applicationService = applicationService;
        this.progressService = progressService;
        this.closureService = closureService;
        this.documentService = documentService;
        this.fundService = fundService;
        this.logService = logService;
    }

    // 1.1 发布项目
    @PostMapping
    public ResultVO<ProjectVO> createProject(@RequestBody ProjectCreationDTO dto) {
    ProjectVO projectVO = projectService.createProject(dto);
    return ResultVO.success("操作成功", projectVO);
}

@PutMapping
public ResultVO<ProjectVO> updateProject(@RequestBody ProjectCreationDTO dto) {
    if (dto.getProjectId() == null) {
        throw new BusinessException("projectId不能为空");
    }
    ProjectVO projectVO = projectService.updateProject(dto);
    return ResultVO.success("操作成功", projectVO);
}



    // 1.2 获取项目列表
    @GetMapping
public ResultVO<PageResultVO<ProjectVO>> getProjectList(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String initiatorType,
        @RequestParam(required = false) String field,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Integer initiatorId, // 新增
        @RequestParam(required = false) Integer  organizationId, // 新增
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @AuthenticationPrincipal CustomUserDetails userDetails
        ) {
            Integer userId = userDetails.getUserId();
    PageResultVO<ProjectVO> list = projectService.getProjectList(status, initiatorType, field, keyword, initiatorId, organizationId, page, pageSize,userId);
    return ResultVO.success("操作成功", list);
}


@PutMapping("/{projectId}/status")
public ResultVO<ProjectVO> updateProjectStatus(
        @PathVariable Integer projectId,
        @RequestParam String status,
        @RequestParam String reason) {
    projectService.updateProjectStatus(projectId, status, reason);
    return ResultVO.success("操作成功", null);
}



    // 1.3 获取项目详情
    @GetMapping("/{projectId}")
    public ResultVO<ProjectVO> getProject(@PathVariable Integer projectId) {
        ProjectVO vo = projectService.getProjectById(projectId);
        return ResultVO.success("操作成功", vo);
    }

    // 1.4 申请对接/合作
    @PostMapping("/{projectId}/apply")
    public ResultVO<ProjectApplicationVO> applyForProject(@PathVariable Integer projectId, @RequestBody ProjectApplicationDTO dto) {
        ProjectApplicationVO vo = applicationService.applyForProject(projectId, dto);
        return ResultVO.success("操作成功", vo);
    }


    // 1.5 审核对接申请
    @PostMapping("/{projectId}/applications/{applicationId}/review")
    public ResultVO<ProjectApplicationVO> reviewApplication(@PathVariable Integer projectId, @PathVariable Integer applicationId, @RequestBody ProjectApplicationReviewDTO dto) {
        ProjectApplicationVO vo = applicationService.reviewApplication(projectId, applicationId, dto);
        return ResultVO.success("操作成功", vo);
    }


    // 1.5.1 获取项目所有对接申请
    @GetMapping("/{projectId}/applications")
    public ResultVO<List<ProjectApplicationVO>> getProjectApplications(@PathVariable Integer projectId) {
        List<ProjectApplicationVO> list = applicationService.getProjectApplications(projectId);
        return ResultVO.success("操作成功", list);
    }



    @GetMapping("/application/my-applications")
    public ResultVO<List<ProjectApplicationVO>> getMyApplications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer userId = userDetails.getUserId();
        List<ProjectApplicationVO> list = applicationService.getMyProjectApplications(userId);
        return ResultVO.success("操作成功",list);
}






    // 2.1 项目进度更新
    @PostMapping("/{projectId}/progress")
    public ResultVO<ProjectProgressVO> addProgress(@PathVariable Integer projectId, @RequestBody ProjectProgressDTO dto) {
        ProjectProgressVO vo = progressService.addProgress(projectId, dto);
        return ResultVO.success("操作成功", vo);
    }

    // 2.1.1 获取项目进度列表
    @GetMapping("/{projectId}/progress")
    public ResultVO<List<ProjectProgressVO>> getProgressList(@PathVariable Integer projectId) {
        List<ProjectProgressVO> list = progressService.getProgressList(projectId);
        return ResultVO.success("操作成功", list);
    }

    // 2.2 项目结项
    @PostMapping("/{projectId}/close")
    public ResultVO<ProjectClosureVO> closeProject(@PathVariable Integer projectId, @RequestBody ProjectClosureDTO dto) {
        ProjectClosureVO vo = closureService.closeProject(projectId, dto);
        return ResultVO.success("操作成功", vo);
    }

    // 2.2.1 获取项目结项信息
    @GetMapping("/{projectId}/closure")
    public ResultVO<ProjectClosureVO> getClosure(@PathVariable Integer projectId) {
        ProjectClosureVO vo = closureService.getClosure(projectId);
        return ResultVO.success("操作成功", vo);
    }

  



    // 3.1 上传合同/资料：没用
    @PostMapping("/{projectId}/documents")
    public ResultVO<ProjectDocumentVO> addDocument(@PathVariable Integer projectId, @RequestBody ProjectDocumentDTO dto) {
        ProjectDocumentVO vo = documentService.addDocument(projectId, dto);
        return ResultVO.success("操作成功", vo);
    }

    // 3.2 获取项目资料列表：没用
    @GetMapping("/{projectId}/documents")
    public ResultVO<List<ProjectDocumentVO>> getDocuments(@PathVariable Integer projectId) {
        List<ProjectDocumentVO> list = documentService.getDocuments(projectId);
        return ResultVO.success("操作成功", list);
    }






    // 4.1 经费申请
    @PostMapping("/{projectId}/funds/apply")
    public ResultVO<ProjectFundVO> applyFund(@PathVariable Integer projectId, @RequestBody ProjectFundApplyDTO dto) {
        ProjectFundVO vo = fundService.applyFund(projectId, dto);
        return ResultVO.success("操作成功", vo);
    }

    // 4.2 经费审批
    @PostMapping("/{projectId}/funds/{fundId}/review")
    public ResultVO<ProjectFundVO> reviewFund(@PathVariable Integer projectId, @PathVariable Integer fundId, @RequestBody ProjectFundReviewDTO dto) {
        ProjectFundVO vo = fundService.reviewFund(projectId, fundId, dto);
        return ResultVO.success("操作成功", vo);
    }

    // 4.3 经费使用记录
    @GetMapping("/{projectId}/funds/records")
    public ResultVO<List<ProjectFundRecordVO>> getFundRecords(@PathVariable Integer projectId) {
        List<ProjectFundRecordVO> list = fundService.getFundRecords(projectId);
        return ResultVO.success("操作成功", list);
    }

    @GetMapping("/{projectId}/funds")
    public ResultVO<List<ProjectFundVO>> getFundList(@PathVariable Integer projectId) {
        List<ProjectFundVO> list = fundService.getFundList(projectId);
        return ResultVO.success("操作成功", list);
    }








    // 5.1 获取项目操作日志
    @GetMapping("/{projectId}/logs")
    public ResultVO<List<ProjectLogVO>> getProjectLogs(@PathVariable Integer projectId) {
        List<ProjectLogVO> list = logService.getProjectLogs(projectId);
        return ResultVO.success("操作成功", list);
    }
}
