package com.cqu.locker.controller.admin;

import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.service.admin.AdminUserService;
import com.cqu.locker.utils.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/user")
@CrossOrigin
public class AdminUserController {
    
    @Autowired
    private AdminUserService adminUserService;
    
    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public Result<PageResponse<SysUser>> list(UserQueryRequest request) {
        try {
            PageResponse<SysUser> response = adminUserService.queryUsers(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        try {
            SysUser user = adminUserService.getUserById(id);
            return Result.success(user);
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public Result<SysUser> create(@Valid @RequestBody UserCreateRequest request) {
        try {
            SysUser user = adminUserService.createUser(request);
            return Result.success(user);
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<SysUser> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        try {
            SysUser user = adminUserService.updateUser(id, request);
            return Result.success(user);
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            adminUserService.deleteUser(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            adminUserService.updateUserStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重置用户密码
     */
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam(required = false) String password) {
        try {
            adminUserService.resetPassword(id, password);
            return Result.success();
        } catch (Exception e) {
            log.error("重置密码失败", e);
            return Result.error(e.getMessage());
        }
    }
}
