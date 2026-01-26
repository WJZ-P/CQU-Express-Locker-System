package com.cqu.locker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqu.locker.entity.IotPowerStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 电量统计Mapper
 */
@Mapper
public interface IotPowerStatsMapper extends BaseMapper<IotPowerStats> {
    
    /**
     * 获取指定日期所有快递柜的总用电量
     */
    @Select("SELECT COALESCE(SUM(power_usage), 0) FROM iot_power_stats WHERE record_date = #{date}")
    BigDecimal getTotalPowerByDate(@Param("date") LocalDate date);
    
    /**
     * 获取指定日期各快递柜用电量
     */
    @Select("SELECT p.locker_id, l.name as locker_name, l.serial_no, p.power_usage " +
            "FROM iot_power_stats p " +
            "LEFT JOIN iot_locker l ON p.locker_id = l.id " +
            "WHERE p.record_date = #{date}")
    List<Map<String, Object>> getPowerStatsByDate(@Param("date") LocalDate date);
    
    /**
     * 获取指定快递柜在日期范围内的用电量
     */
    @Select("SELECT record_date, power_usage FROM iot_power_stats " +
            "WHERE locker_id = #{lockerId} AND record_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY record_date")
    List<Map<String, Object>> getLockerPowerByDateRange(
            @Param("lockerId") Long lockerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
