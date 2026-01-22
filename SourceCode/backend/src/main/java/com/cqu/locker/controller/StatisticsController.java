package com.cqu.locker.controller;

import com.cqu.locker.utils.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计分析控制器 (Mock数据)
 */
@RestController
@RequestMapping("/api/v1/statistics")
@CrossOrigin
public class StatisticsController {

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceOnlineRate", 95);
        data.put("todayOrderCount", 128);
        return Result.success(data);
    }
}
