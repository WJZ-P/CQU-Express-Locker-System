package com.cqu.locker.controller;

import com.cqu.locker.entity.SysUser;
import com.cqu.locker.service.SysUserService;
import com.cqu.locker.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private SysUserService userService;

    // 简单模拟 Token 存储 (实际生产环境应使用 Redis + JWT)
    private static final Map<String, SysUser> tokenMap = new HashMap<>();

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        SysUser user = userService.login(username, password);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, user);
        
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/userinfo")
    public Result<SysUser> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        SysUser user = tokenMap.get(token);
        if (user == null) {
            return Result.error(401, "未登录或Token过期");
        }
        return Result.success(user);
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
         if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        tokenMap.remove(token);
        return Result.success();
    }
}
