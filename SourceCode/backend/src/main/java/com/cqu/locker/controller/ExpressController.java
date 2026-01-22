package com.cqu.locker.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqu.locker.entity.BusOrder;
import com.cqu.locker.entity.dto.*;
import com.cqu.locker.service.BusOrderService;
import com.cqu.locker.service.ExpressService;
import com.cqu.locker.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 快递业务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ExpressController {

    @Autowired
    private BusOrderService orderService;
    
    @Autowired
    private ExpressService expressService;

    // --- V1 API (新版接口) ---
    
    /**
     * 获取待取快递列表
     */
    @GetMapping("/v1/express/pending")
    public Result<ExpressPendingResponse> getPendingList(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            ExpressPendingResponse response = expressService.getPendingList(userId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取待取快递列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取快递详情
     */
    @GetMapping("/v1/express/{expressId}")
    public Result<ExpressDetailResponse> getExpressDetail(
            @PathVariable String expressId,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            ExpressDetailResponse response = expressService.getExpressDetail(expressId, userId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取快递详情失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 取件（验证取件码）
     */
    @PostMapping("/v1/express/pickup")
    public Result<PickupResponse> pickup(
            @Valid @RequestBody PickupRequest pickupRequest,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            PickupResponse response = expressService.pickup(pickupRequest, userId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("取件失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 开柜（已验证后再次开柜）
     */
    @PostMapping("/v1/express/open")
    public Result<Void> openCompartment(
            @Valid @RequestBody OpenCompartmentRequest openRequest,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            expressService.openCompartment(openRequest, userId);
            return Result.success();
        } catch (Exception e) {
            log.error("开柜失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 寄件
     */
    @PostMapping("/v1/express/send")
    public Result<SendExpressResponse> sendExpress(
            @Valid @RequestBody SendExpressRequest sendRequest,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            SendExpressResponse response = expressService.sendExpress(sendRequest, userId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("寄件失败", e);
            return Result.error(e.getMessage());
        }
    }

    // --- 旧版接口 ---

    /**
     * 快递列表
     */
    @GetMapping("/express/list")
    public Result<Page<BusOrder>> list(@RequestParam(defaultValue = "1") Integer current, 
                                       @RequestParam(defaultValue = "10") Integer size,
                                       @RequestParam(required = false) String trackingNo) {
        Page<BusOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusOrder::getType, 1); // 1-投递
        if (trackingNo != null) {
            wrapper.like(BusOrder::getTrackingNo, trackingNo);
        }
        return Result.success(orderService.page(page, wrapper));
    }
    
    /**
     * 快递详情
     */
    @GetMapping("/express/{id}")
    public Result<BusOrder> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    /**
     * 寄存记录
     */
    @GetMapping("/storage/list")
    public Result<Page<BusOrder>> storageList(@RequestParam(defaultValue = "1") Integer current, 
                                              @RequestParam(defaultValue = "10") Integer size) {
        Page<BusOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusOrder::getType, 3); // 3-寄存
        return Result.success(orderService.page(page, wrapper));
    }
}
