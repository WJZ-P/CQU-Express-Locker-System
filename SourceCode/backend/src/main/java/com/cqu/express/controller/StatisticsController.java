package com.cqu.express.controller;

import com.cqu.express.common.Result;
import com.cqu.express.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
public class StatisticsController {

    @Autowired
    private LockerRepository lockerRepository;
    @Autowired
    private ExpressOrderRepository expressOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourierRepository courierRepository;

    @GetMapping("/dashboard")
    public Result<Map<String, Long>> dashboard() {
        Map<String, Long> data = new HashMap<>();
        data.put("lockerCount", lockerRepository.count());
        data.put("expressCount", expressOrderRepository.count());
        data.put("userCount", userRepository.count());
        data.put("courierCount", courierRepository.count());
        return Result.success(data);
    }

    @GetMapping("/express-trend")
    public Result<Map<String, Object>> trend() {
        // Mock data for trend
        Map<String, Object> data = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        
        for (int i = 6; i >= 0; i--) {
            dates.add(LocalDateTime.now().minusDays(i).toLocalDate().toString());
            values.add((int)(Math.random() * 100));
        }
        
        data.put("dates", dates);
        data.put("values", values);
        return Result.success(data);
    }

    @GetMapping("/usage-rate")
    public Result<List<Map<String, Object>>> usageRate() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> m1 = new HashMap<>();
        m1.put("name", "已用");
        m1.put("value", 65);
        
        Map<String, Object> m2 = new HashMap<>();
        m2.put("name", "空闲");
        m2.put("value", 35);
        
        data.add(m1);
        data.add(m2);
        return Result.success(data);
    }
}
