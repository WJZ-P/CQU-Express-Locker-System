package com.cqu.locker.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cqu.locker.entity.BusOrder;
import com.cqu.locker.entity.IotBox;
import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.entity.SysCourier;
import com.cqu.locker.entity.SysUser;
import com.cqu.locker.entity.dto.admin.ChartDataItem;
import com.cqu.locker.entity.dto.admin.DashboardStatsResponse;
import com.cqu.locker.mapper.*;
import com.cqu.locker.service.admin.AdminStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理端统计服务实现类
 */
@Slf4j
@Service
public class AdminStatisticsServiceImpl implements AdminStatisticsService {
    
    @Autowired
    private BusOrderMapper orderMapper;
    
    @Autowired
    private IotLockerMapper lockerMapper;
    
    @Autowired
    private IotBoxMapper boxMapper;
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private SysCourierMapper courierMapper;
    
    @Override
    public DashboardStatsResponse getDashboardStats() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime yesterdayStart = todayStart.minusDays(1);
        
        // 今日新增订单数
        LambdaQueryWrapper<BusOrder> todayOrderWrapper = new LambdaQueryWrapper<>();
        todayOrderWrapper.ge(BusOrder::getCreateTime, todayStart);
        long todayOrders = orderMapper.selectCount(todayOrderWrapper);
        
        // 昨日订单数（用于计算增长率）
        LambdaQueryWrapper<BusOrder> yesterdayOrderWrapper = new LambdaQueryWrapper<>();
        yesterdayOrderWrapper.ge(BusOrder::getCreateTime, yesterdayStart);
        yesterdayOrderWrapper.lt(BusOrder::getCreateTime, todayStart);
        long yesterdayOrders = orderMapper.selectCount(yesterdayOrderWrapper);
        
        // 待取件数量
        LambdaQueryWrapper<BusOrder> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(BusOrder::getStatus, 0);
        long pendingPickup = orderMapper.selectCount(pendingWrapper);
        
        // 超时件数量
        LambdaQueryWrapper<BusOrder> overtimeWrapper = new LambdaQueryWrapper<>();
        overtimeWrapper.eq(BusOrder::getStatus, 2);
        long overtimeOrders = orderMapper.selectCount(overtimeWrapper);
        
        // 快递柜统计
        long totalLockers = lockerMapper.selectCount(null);
        
        LambdaQueryWrapper<IotLocker> onlineWrapper = new LambdaQueryWrapper<>();
        onlineWrapper.eq(IotLocker::getStatus, 1);
        onlineWrapper.eq(IotLocker::getEnabled, 1);
        long onlineLockers = lockerMapper.selectCount(onlineWrapper);
        
        // 格口统计
        long totalCompartments = boxMapper.selectCount(null);
        
        LambdaQueryWrapper<IotBox> availableWrapper = new LambdaQueryWrapper<>();
        availableWrapper.eq(IotBox::getStatus, 0);
        availableWrapper.eq(IotBox::getEnabled, 1);
        long availableCompartments = boxMapper.selectCount(availableWrapper);
        
        // 用户统计
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getRole, 1);
        long totalUsers = userMapper.selectCount(userWrapper);
        
        // 快递员统计
        LambdaQueryWrapper<SysCourier> courierWrapper = new LambdaQueryWrapper<>();
        courierWrapper.eq(SysCourier::getStatus, 1);
        long totalCouriers = courierMapper.selectCount(courierWrapper);
        
        // 计算增长率和使用率
        BigDecimal orderGrowthRate = BigDecimal.ZERO;
        if (yesterdayOrders > 0) {
            orderGrowthRate = BigDecimal.valueOf((todayOrders - yesterdayOrders) * 100.0 / yesterdayOrders)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        BigDecimal lockerOnlineRate = BigDecimal.ZERO;
        if (totalLockers > 0) {
            lockerOnlineRate = BigDecimal.valueOf(onlineLockers * 100.0 / totalLockers)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        BigDecimal compartmentUsageRate = BigDecimal.ZERO;
        if (totalCompartments > 0) {
            long usedCompartments = totalCompartments - availableCompartments;
            compartmentUsageRate = BigDecimal.valueOf(usedCompartments * 100.0 / totalCompartments)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        return DashboardStatsResponse.builder()
                .todayOrders(todayOrders)
                .pendingPickup(pendingPickup)
                .overtimeOrders(overtimeOrders)
                .onlineLockers(onlineLockers)
                .totalLockers(totalLockers)
                .availableCompartments(availableCompartments)
                .totalCompartments(totalCompartments)
                .totalUsers(totalUsers)
                .totalCouriers(totalCouriers)
                .orderGrowthRate(orderGrowthRate)
                .lockerOnlineRate(lockerOnlineRate)
                .compartmentUsageRate(compartmentUsageRate)
                .build();
    }
    
    @Override
    public List<ChartDataItem> getOrderTrend(Integer days) {
        if (days == null || days <= 0) {
            days = 7;
        }
        
        List<ChartDataItem> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
            
            LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(BusOrder::getCreateTime, dayStart);
            wrapper.le(BusOrder::getCreateTime, dayEnd);
            long count = orderMapper.selectCount(wrapper);
            
            result.add(ChartDataItem.builder()
                    .name(date.format(formatter))
                    .value(count)
                    .build());
        }
        
        return result;
    }
    
    @Override
    public List<ChartDataItem> getCompanyDistribution() {
        List<ChartDataItem> result = new ArrayList<>();
        
        // 获取所有快递公司
        List<SysCourier> couriers = courierMapper.selectList(null);
        
        // 按公司分组统计订单数
        couriers.stream()
                .map(SysCourier::getCompany)
                .distinct()
                .forEach(company -> {
                    LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(BusOrder::getCompany, company);
                    long count = orderMapper.selectCount(wrapper);
                    if (count > 0) {
                        result.add(ChartDataItem.builder()
                                .name(company)
                                .value(count)
                                .build());
                    }
                });
        
        // 按数量排序
        result.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        
        return result;
    }
    
    @Override
    public List<ChartDataItem> getLockerUsageRanking(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        List<ChartDataItem> result = new ArrayList<>();
        
        // 获取所有快递柜
        List<IotLocker> lockers = lockerMapper.selectList(null);
        
        for (IotLocker locker : lockers) {
            LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BusOrder::getLockerId, locker.getId());
            long count = orderMapper.selectCount(wrapper);
            
            String name = locker.getName() != null ? locker.getName() : locker.getLockerNo();
            result.add(ChartDataItem.builder()
                    .name(name)
                    .value(count)
                    .build());
        }
        
        // 按使用量排序并限制数量
        result.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        if (result.size() > limit) {
            result = result.subList(0, limit);
        }
        
        return result;
    }
    
    @Override
    public List<ChartDataItem> getDailyOrderStats(String startDate, String endDate) {
        List<ChartDataItem> result = new ArrayList<>();
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd");
        
        LocalDate start = LocalDate.parse(startDate, inputFormatter);
        LocalDate end = LocalDate.parse(endDate, inputFormatter);
        
        while (!start.isAfter(end)) {
            LocalDateTime dayStart = LocalDateTime.of(start, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(start, LocalTime.MAX);
            
            LambdaQueryWrapper<BusOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(BusOrder::getCreateTime, dayStart);
            wrapper.le(BusOrder::getCreateTime, dayEnd);
            long count = orderMapper.selectCount(wrapper);
            
            result.add(ChartDataItem.builder()
                    .name(start.format(outputFormatter))
                    .value(count)
                    .build());
            
            start = start.plusDays(1);
        }
        
        return result;
    }
    
    @Override
    public List<ChartDataItem> getHourlyOrderDistribution() {
        List<ChartDataItem> result = new ArrayList<>();
        
        // 统计过去30天的每小时订单分布
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        for (int hour = 0; hour < 24; hour++) {
            // 这里简化处理，实际应该用SQL统计
            String hourStr = String.format("%02d:00", hour);
            result.add(ChartDataItem.builder()
                    .name(hourStr)
                    .value(0L) // 需要实际的小时统计SQL
                    .build());
        }
        
        return result;
    }
}
