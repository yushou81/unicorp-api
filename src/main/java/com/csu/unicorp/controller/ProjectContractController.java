// src/main/java/com/csu/unicorp/controller/ProjectContractController.java
package com.csu.unicorp.controller;

import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.ProjectContractCreationDTO;
import com.csu.unicorp.dto.ProjectContractStatusUpdateDTO;
import com.csu.unicorp.vo.ProjectContractVO;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.service.ProjectContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/v1/projects/{projectId}/contracts")
public class ProjectContractController {

    @Autowired
    private ProjectContractService contractService;

    @Operation(summary = "发起合同", description = "创建并发起一个新合同")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功")
    })
    @PostMapping
    public ResultVO<ProjectContractVO> createContract(
            @PathVariable Integer projectId,
            @RequestBody ProjectContractCreationDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProjectContractVO vo = contractService.createContract(projectId, dto, userDetails.getUserId());
        return ResultVO.success(vo);
    }

    @Operation(summary = "获取合同详情", description = "根据合同ID获取合同详情")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/{contractId}")
    public ResultVO<ProjectContractVO> getContract(
            @PathVariable Integer projectId,
            @PathVariable Integer contractId) {
        ProjectContractVO vo = contractService.getContractById(projectId, contractId);
        return ResultVO.success(vo);
    }

    @Operation(summary = "更新合同状态", description = "如签署、归档、作废等")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @PutMapping("/{contractId}/status")
    public ResultVO<ProjectContractVO> updateContractStatus(
            @PathVariable Integer projectId,
            @PathVariable Integer contractId,
            @RequestBody ProjectContractStatusUpdateDTO dto) {
        ProjectContractVO vo = contractService.updateContractStatus(projectId, contractId, dto);
        return ResultVO.success(vo);
    }

    @Operation(summary = "获取项目下所有合同", description = "合同列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping
    public ResultVO<List<ProjectContractVO>> getContracts(
            @PathVariable Integer projectId) {
        List<ProjectContractVO> list = contractService.getContractsByProjectId(projectId);
        return ResultVO.success("成功",list);
    }
}