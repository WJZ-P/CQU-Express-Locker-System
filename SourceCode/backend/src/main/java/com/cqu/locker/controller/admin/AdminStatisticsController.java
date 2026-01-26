package com.cqu.locker.controller.admin;

import com.cqu.locker.entity.dto.admin.ChartDataItem;
import com.cqu.locker.entity.dto.admin.DashboardStatsResponse;
import com.cqu.locker.entity.dto.admin.TrendDataResponse;
import com.cqu.locker.service.admin.AdminStatisticsService;
import com.cqu.locker.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端-统计分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/statistics")
@CrossOrigin
public class AdminStatisticsController {
    
    @Autowired
    private AdminStatisticsService adminStatisticsService;
    
    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public Result<DashboardStatsResponse> getDashboardStats() {
        try {
            DashboardStatsResponse response = adminStatisticsService.getDashboardStats();
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取仪表盘统计失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取订单趋势数据
     */
    @GetMapping("/order-trend")
    public Result<TrendDataResponse> getOrderTrend(@RequestParam(defaultValue = "7") Integer days) {
        try {
            TrendDataResponse data = adminStatisticsService.getOrderTrendData(days);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取订单趋势失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取快递公司分布
     */
    @GetMapping("/company-distribution")
    public Result<List<ChartDataItem>> getCompanyDistribution() {
        try {
            List<ChartDataItem> data = adminStatisticsService.getCompanyDistribution();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取快递公司分布失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取快递柜使用排行
     */
    @GetMapping("/locker-usage-ranking")
    public Result<List<ChartDataItem>> getLockerUsageRanking(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ChartDataItem> data = adminStatisticsService.getLockerUsageRanking(limit);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取快递柜使用排行失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取每日订单统计
     */
    @GetMapping("/daily-orders")
    public Result<List<ChartDataItem>> getDailyOrderStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            List<ChartDataItem> data = adminStatisticsService.getDailyOrderStats(startDate, endDate);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取每日订单统计失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取每小时订单分布
     */
    @GetMapping("/hourly-distribution")
    public Result<List<ChartDataItem>> getHourlyOrderDistribution() {
        try {
            List<ChartDataItem> data = adminStatisticsService.getHourlyOrderDistribution();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取每小时订单分布失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取月度订单统计
     */
    @GetMapping("/monthly-orders")
    public Result<Map<String, Object>> getMonthlyOrders() {
        try {
            Map<String, Object> data = adminStatisticsService.getMonthlyOrders();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取月度订单统计失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取格口使用率统计
     */
    @GetMapping("/box-usage")
    public Result<Map<String, Object>> getBoxUsageStats() {
        try {
            Map<String, Object> data = adminStatisticsService.getBoxUsageStats();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取格口使用率统计失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用电量统计
     */
    @GetMapping("/power-stats")
    public Result<Map<String, Object>> getPowerStats() {
        try {
            Map<String, Object> data = adminStatisticsService.getPowerStats();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取用电量统计失败", e);
            return Result.error(e.getMessage());
        }
    }
}
