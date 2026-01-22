package com.cqu.locker.service;

import com.cqu.locker.entity.dto.*;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 发送验证码
     * @param request 发送验证码请求
     */
    void sendCode(SendCodeRequest request);
    
    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册响应
     */
    RegisterResponse register(RegisterRequest request);
}
