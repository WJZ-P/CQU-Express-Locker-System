package com.cqu.locker.controller;

import com.cqu.locker.entity.dto.*;
import com.cqu.locker.service.AuthService;
import com.cqu.locker.utils.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error(401, e.getMessage());
        }
    }
    
    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        try {
            authService.sendCode(request);
            return Result.success();
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.register(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error(e.getMessage());
        }
    }
}
