package com.cqu.locker.controller.admin;

import com.cqu.locker.entity.BusOrder;
import com.cqu.locker.entity.dto.admin.*;
import com.cqu.locker.service.admin.AdminExpressService;
import com.cqu.locker.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-快递订单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/express")
@CrossOrigin
public class AdminExpressController {
    
    @Autowired
    private AdminExpressService adminExpressService;
    
    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    public Result<PageResponse<BusOrder>> list(ExpressQueryRequest request) {
        try {
            PageResponse<BusOrder> response = adminExpressService.queryOrders(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询订单列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<BusOrder> getById(@PathVariable Long id) {
        try {
            BusOrder order = adminExpressService.getOrderById(id);
            return Result.success(order);
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            adminExpressService.deleteOrder(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除订单失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 强制取件
     */
    @PostMapping("/{id}/force-pickup")
    public Result<Void> forcePickup(@PathVariable Long id) {
        try {
            adminExpressService.forcePickup(id);
            return Result.success();
        } catch (Exception e) {
            log.error("强制取件失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 标记超时
     */
    @PostMapping("/{id}/mark-overtime")
    public Result<Void> markOvertime(@PathVariable Long id) {
        try {
            adminExpressService.markOvertime(id);
            return Result.success();
        } catch (Exception e) {
            log.error("标记超时失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 退回快递
     */
    @PostMapping("/{id}/return")
    public Result<Void> returnExpress(@PathVariable Long id) {
        try {
            adminExpressService.returnExpress(id);
            return Result.success();
        } catch (Exception e) {
            log.error("退回快递失败", e);
            return Result.error(e.getMessage());
        }
    }
}
