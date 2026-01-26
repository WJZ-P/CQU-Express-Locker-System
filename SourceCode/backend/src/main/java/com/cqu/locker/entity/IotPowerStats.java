package com.cqu.locker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 电量统计实体类
 */
@Data
@TableName("iot_power_stats")
public class IotPowerStats {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 快递柜ID
     */
    private Long lockerId;
    
    /**
     * 用电量(kWh)
     */
    private BigDecimal powerUsage;
    
    /**
     * 记录日期
     */
    private LocalDate recordDate;
    
    private LocalDateTime createTime;
}
