package com.csu.unicorp.controller;

import com.csu.unicorp.service.VerificationCodeService;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮箱验证码控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "邮箱验证码相关接口")
public class EmailVerificationController {
    
    private final VerificationCodeService verificationCodeService;
    
    /**
     * 发送邮箱验证码
     */
    @Operation(summary = "发送邮箱验证码", description = "向指定邮箱发送验证码，用于邮箱验证")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "验证码发送成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "发送失败，如邮箱格式错误或发送过于频繁", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/verification-code")
    public ResultVO<Void> sendVerificationCode(
            @Parameter(description = "邮箱地址", required = true) 
            @RequestParam String email) {
        
        log.info("收到发送验证码请求，邮箱: {}", email);
        
        // 检查是否可以发送验证码（是否在限制期内）
        if (!verificationCodeService.canSendCode(email)) {
            return ResultVO.error("发送过于频繁，请稍后再试");
        }
        
        // 生成并发送验证码
        String code = verificationCodeService.generateEmailVerificationCode(email);
        
        if (code == null) {
            return ResultVO.error("验证码发送失败，请稍后再试");
        }
        
        return ResultVO.success("验证码已发送，请查收邮件");
    }
} 