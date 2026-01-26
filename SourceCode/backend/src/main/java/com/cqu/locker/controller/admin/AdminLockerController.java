package com.cqu.locker.controller.admin;

import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.service.admin.AdminLockerService;
import com.cqu.locker.utils.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-快递柜管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/locker")
@CrossOrigin
public class AdminLockerController {
    
    @Autowired
    private AdminLockerService adminLockerService;
    
    /**
     * 分页查询快递柜列表
     */
    @GetMapping("/list")
    public Result<PageResponse<IotLocker>> list(LockerQueryRequest request) {
        try {
            PageResponse<IotLocker> response = adminLockerService.queryLockers(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询快递柜列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取快递柜详情
     */
    @GetMapping("/{id}")
    public Result<IotLocker> getById(@PathVariable Long id) {
        try {
            IotLocker locker = adminLockerService.getLockerById(id);
            return Result.success(locker);
        } catch (Exception e) {
            log.error("获取快递柜详情失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建快递柜
     */
    @PostMapping
    public Result<IotLocker> create(@Valid @RequestBody LockerCreateRequest request) {
        try {
            IotLocker locker = adminLockerService.createLocker(request);
            return Result.success(locker);
        } catch (Exception e) {
            log.error("创建快递柜失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新快递柜
     */
    @PutMapping("/{id}")
    public Result<IotLocker> update(@PathVariable Long id, @RequestBody LockerUpdateRequest request) {
        try {
            IotLocker locker = adminLockerService.updateLocker(id, request);
            return Result.success(locker);
        } catch (Exception e) {
            log.error("更新快递柜失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除快递柜
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            adminLockerService.deleteLocker(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除快递柜失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新快递柜启用状态
     */
    @PutMapping("/{id}/enabled")
    public Result<Void> updateEnabled(@PathVariable Long id, @RequestParam Integer enabled) {
        try {
            adminLockerService.updateLockerEnabled(id, enabled);
            return Result.success();
        } catch (Exception e) {
            log.error("更新快递柜启用状态失败", e);
            return Result.error(e.getMessage());
        }
    }
}
