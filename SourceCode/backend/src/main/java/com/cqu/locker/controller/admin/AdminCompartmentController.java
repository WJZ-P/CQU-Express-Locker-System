package com.cqu.locker.controller.admin;

import com.cqu.locker.entity.IotBox;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.service.admin.AdminCompartmentService;
import com.cqu.locker.utils.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-格口管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/compartment")
@CrossOrigin
public class AdminCompartmentController {
    
    @Autowired
    private AdminCompartmentService adminCompartmentService;
    
    /**
     * 分页查询格口列表
     */
    @GetMapping("/list")
    public Result<PageResponse<IotBox>> list(CompartmentQueryRequest request) {
        try {
            PageResponse<IotBox> response = adminCompartmentService.queryCompartments(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询格口列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取格口详情
     */
    @GetMapping("/{id}")
    public Result<IotBox> getById(@PathVariable Long id) {
        try {
            IotBox box = adminCompartmentService.getCompartmentById(id);
            return Result.success(box);
        } catch (Exception e) {
            log.error("获取格口详情失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建格口
     */
    @PostMapping
    public Result<IotBox> create(@Valid @RequestBody CompartmentCreateRequest request) {
        try {
            IotBox box = adminCompartmentService.createCompartment(request);
            return Result.success(box);
        } catch (Exception e) {
            log.error("创建格口失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新格口
     */
    @PutMapping("/{id}")
    public Result<IotBox> update(@PathVariable Long id, @RequestBody CompartmentUpdateRequest request) {
        try {
            IotBox box = adminCompartmentService.updateCompartment(id, request);
            return Result.success(box);
        } catch (Exception e) {
            log.error("更新格口失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除格口
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            adminCompartmentService.deleteCompartment(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除格口失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新格口启用状态
     */
    @PutMapping("/{id}/enabled")
    public Result<Void> updateEnabled(@PathVariable Long id, @RequestParam Integer enabled) {
        try {
            adminCompartmentService.updateCompartmentEnabled(id, enabled);
            return Result.success();
        } catch (Exception e) {
            log.error("更新格口启用状态失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 远程开门
     */
    @PostMapping("/{id}/open")
    public Result<Void> openDoor(@PathVariable Long id) {
        try {
            adminCompartmentService.openDoor(id);
            return Result.success();
        } catch (Exception e) {
            log.error("远程开门失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 标记故障
     */
    @PostMapping("/{id}/fault")
    public Result<Void> markFault(@PathVariable Long id, @RequestParam String reason) {
        try {
            adminCompartmentService.markFault(id, reason);
            return Result.success();
        } catch (Exception e) {
            log.error("标记故障失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 清除故障
     */
    @PostMapping("/{id}/clear-fault")
    public Result<Void> clearFault(@PathVariable Long id) {
        try {
            adminCompartmentService.clearFault(id);
            return Result.success();
        } catch (Exception e) {
            log.error("清除故障失败", e);
            return Result.error(e.getMessage());
        }
    }
}
