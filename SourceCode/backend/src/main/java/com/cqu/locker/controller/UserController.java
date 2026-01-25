package com.cqu.locker.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqu.locker.entity.SysCourier;
import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.*;
import com.cqu.locker.service.CourierService;
import com.cqu.locker.service.SysCourierService;
import com.cqu.locker.service.SysUserService;
import com.cqu.locker.service.UserService;
import com.cqu.locker.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    
    @Autowired
    private CourierService courierServiceV1;

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
    
    /**
     * 获取快递员信息
     */
    @GetMapping("/api/v1/courier/profile")
    public Result<CourierProfileResponse> getCourierProfile(HttpServletRequest request) {
        try {
            Long courierId = (Long) request.getAttribute("userId"); // 快递员ID也存放在userId属性中
            CourierProfileResponse response = courierServiceV1.getCourierProfile(courierId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取快递员信息失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 投递快递
     */
    @PostMapping("/api/v1/courier/deliver")
    public Result<DeliverResponse> deliver(HttpServletRequest request, 
                                          @Valid @RequestBody DeliverRequest deliverRequest) {
        try {
            Long courierId = (Long) request.getAttribute("userId");
            DeliverResponse response = courierServiceV1.deliver(deliverRequest, courierId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("投递快递失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询收件人信息
     */
    @GetMapping("/api/v1/courier/query-receiver")
    public Result<ReceiverInfoResponse> queryReceiver(@RequestParam String phone) {
        try {
            ReceiverInfoResponse response = courierServiceV1.queryReceiver(phone);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询收件人信息失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取待揽收列表
     */
    @GetMapping("/api/v1/courier/pending-collect")
    public Result<PendingCollectResponse> getPendingCollectList(HttpServletRequest request) {
        try {
            Long courierId = (Long) request.getAttribute("userId");
            PendingCollectResponse response = courierServiceV1.getPendingCollectList(courierId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取待揽收列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取待退回列表
     */
    @GetMapping("/api/v1/courier/pending-return")
    public Result<PendingReturnResponse> getPendingReturnList(HttpServletRequest request) {
        try {
            Long courierId = (Long) request.getAttribute("userId");
            PendingReturnResponse response = courierServiceV1.getPendingReturnList(courierId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取待退回列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 开柜操作
     */
    @PostMapping("/api/v1/courier/open-compartment")
    public Result<Void> openCompartment(HttpServletRequest request, 
                                        @Valid @RequestBody OpenCompartmentRequest openRequest) {
        try {
            Long courierId = (Long) request.getAttribute("userId");
            courierServiceV1.openCompartment(openRequest, courierId);
            return Result.success();
        } catch (Exception e) {
            log.error("开柜操作失败", e);
            return Result.error(e.getMessage());
        }
    }

    // --- 用户管理 ---
    
    /**
     * 用户列表
     */
    @GetMapping("/api/v1/user/list")
    public Result<Page<SysUser>> userList(@RequestParam(defaultValue = "1") Integer current, 
                                          @RequestParam(defaultValue = "10") Integer size) {
        Page<SysUser> page = new Page<>(current, size);
        return Result.success(userService.page(page));
    }
    
    /**
     * 用户详情
     */
    @GetMapping("/api/v1/user/{id}")
    public Result<SysUser> getUserById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    // --- 快递员管理 ---
    
    /**
     * 快递员列表
     */
    @GetMapping("/api/v1/courier/list")
    public Result<Page<SysCourier>> courierList(@RequestParam(defaultValue = "1") Integer current, 
                                                @RequestParam(defaultValue = "10") Integer size) {
        Page<SysCourier> page = new Page<>(current, size);
        return Result.success(courierService.page(page));
    }
    
    /**
     * 快递员详情
     */
    @GetMapping("/api/v1/courier/{id}")
    public Result<SysCourier> getCourierById(@PathVariable Long id) {
        return Result.success(courierService.getById(id));
    }
}
