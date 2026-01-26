package com.cqu.locker.controller.admin;

import com.cqu.locker.entity.SysCourier;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.service.admin.AdminCourierService;
import com.cqu.locker.utils.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-快递员管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/courier")
@CrossOrigin
public class AdminCourierController {
    
    @Autowired
    private AdminCourierService adminCourierService;
    
    /**
     * 分页查询快递员列表
     */
    @GetMapping("/list")
    public Result<PageResponse<SysCourier>> list(CourierQueryRequest request) {
        try {
            PageResponse<SysCourier> response = adminCourierService.queryCouriers(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询快递员列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取快递员详情
     */
    @GetMapping("/{id}")
    public Result<SysCourier> getById(@PathVariable Long id) {
        try {
            SysCourier courier = adminCourierService.getCourierById(id);
            return Result.success(courier);
        } catch (Exception e) {
            log.error("获取快递员详情失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建快递员
     */
    @PostMapping
    public Result<SysCourier> create(@Valid @RequestBody CourierCreateRequest request) {
        try {
            SysCourier courier = adminCourierService.createCourier(request);
            return Result.success(courier);
        } catch (Exception e) {
            log.error("创建快递员失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新快递员
     */
    @PutMapping("/{id}")
    public Result<SysCourier> update(@PathVariable Long id, @RequestBody CourierUpdateRequest request) {
        try {
            SysCourier courier = adminCourierService.updateCourier(id, request);
            return Result.success(courier);
        } catch (Exception e) {
            log.error("更新快递员失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除快递员
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            adminCourierService.deleteCourier(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除快递员失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新快递员状态（在职/离职）
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            adminCourierService.updateCourierStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            log.error("更新快递员状态失败", e);
            return Result.error(e.getMessage());
        }
    }
}
