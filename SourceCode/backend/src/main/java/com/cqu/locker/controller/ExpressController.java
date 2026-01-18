package com.cqu.locker.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqu.locker.entity.BusOrder;
import com.cqu.locker.service.BusOrderService;
import com.cqu.locker.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 快递业务控制器
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ExpressController {

    @Autowired
    private BusOrderService orderService;

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
