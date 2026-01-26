package com.cqu.locker.service.admin;

import com.cqu.locker.entity.dto.admin.ChartDataItem;
import com.cqu.locker.entity.dto.admin.DashboardStatsResponse;

import java.util.List;

/**
 * 管理端统计服务接口
 */
public interface AdminStatisticsService {
    
    /**
     * 获取仪表盘统计数据
     */
    DashboardStatsResponse getDashboardStats();
    
    /**
     * 获取订单趋势数据（近N天）
     */
    List<ChartDataItem> getOrderTrend(Integer days);
    
    /**
     * 获取快递公司分布
     */
    List<ChartDataItem> getCompanyDistribution();
    
    /**
     * 获取快递柜使用排行
     */
    List<ChartDataItem> getLockerUsageRanking(Integer limit);
    
    /**
     * 获取每日订单统计
     */
    List<ChartDataItem> getDailyOrderStats(String startDate, String endDate);
    
    /**
     * 获取每小时订单分布
     */
    List<ChartDataItem> getHourlyOrderDistribution();
}
