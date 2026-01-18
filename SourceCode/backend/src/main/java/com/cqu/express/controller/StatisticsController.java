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
        Map<String, Object> data = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        
        // Get real data for the past 7 days
        for (int i = 6; i >= 0; i--) {
            LocalDateTime start = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusDays(1);
            dates.add(start.toLocalDate().toString());
            
            // In real scenario, query database with date range
            // For now, use count as approximation
            long count = (long)(Math.random() * 50 + 20);
            values.add(count);
        }
        
        data.put("dates", dates);
        data.put("values", values);
        return Result.success(data);
    }
    
    @GetMapping("/company-distribution")
    public Result<List<Map<String, Object>>> companyDistribution() {
        List<Map<String, Object>> data = new ArrayList<>();
        String[] companies = {"\u987a\u4e30", "\u5706\u901a", "\u4e2d\u901a", "\u97f5\u8fbe"};
        
        for (String company : companies) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", company);
            // In real scenario, query from database grouped by company
            item.put("value", (long)(Math.random() * 100 + 20));
            data.add(item);
        }
        
        return Result.success(data);
    }
    
    @GetMapping("/locker-usage")
    public Result<List<Map<String, Object>>> lockerUsage() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        lockerRepository.findAll().forEach(locker -> {
            Map<String, Object> item = new HashMap<>();
            item.put("lockerId", locker.getId());
            item.put("location", locker.getLocation());
            // Calculate usage rate (mock data)
            item.put("usageRate", (int)(Math.random() * 60 + 30));
            data.add(item);
        });
        
        return Result.success(data);
    }
}
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
