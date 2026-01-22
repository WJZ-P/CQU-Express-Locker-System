package com.cqu.locker.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqu.locker.entity.SysCourier;
import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.UpdateProfileRequest;
import com.cqu.locker.entity.dto.UserProfileResponse;
import com.cqu.locker.service.SysCourierService;
import com.cqu.locker.service.SysUserService;
import com.cqu.locker.service.UserService;
import com.cqu.locker.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户与快递员管理控制器
 */
@Slf4j
@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private SysUserService userService;
    
    @Autowired
    private SysCourierService courierService;
    
    @Autowired
    private UserService userServiceV1;

    // --- V1 API (新版接口) ---
    
    /**
     * 获取用户信息
     */
    @GetMapping("/api/v1/user/profile")
    public Result<UserProfileResponse> getUserProfile(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            UserProfileResponse response = userServiceV1.getUserProfile(userId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/api/v1/user/profile")
    public Result<Void> updateUserProfile(HttpServletRequest request, 
                                          @RequestBody UpdateProfileRequest updateRequest) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            userServiceV1.updateUserProfile(userId, updateRequest);
            return Result.success();
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    // --- 用户管理 ---
    
    /**
     * 用户列表
     */
    @GetMapping("/api/user/list")
    public Result<Page<SysUser>> userList(@RequestParam(defaultValue = "1") Integer current, 
                                          @RequestParam(defaultValue = "10") Integer size) {
        Page<SysUser> page = new Page<>(current, size);
        return Result.success(userService.page(page));
    }
    
    /**
     * 用户详情
     */
    @GetMapping("/api/user/{id}")
    public Result<SysUser> getUserById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    // --- 快递员管理 ---
    
    /**
     * 快递员列表
     */
    @GetMapping("/api/courier/list")
    public Result<Page<SysCourier>> courierList(@RequestParam(defaultValue = "1") Integer current, 
                                                @RequestParam(defaultValue = "10") Integer size) {
        Page<SysCourier> page = new Page<>(current, size);
        return Result.success(courierService.page(page));
    }
    
    /**
     * 快递员详情
     */
    @GetMapping("/api/courier/{id}")
    public Result<SysCourier> getCourierById(@PathVariable Long id) {
        return Result.success(courierService.getById(id));
    }
}
