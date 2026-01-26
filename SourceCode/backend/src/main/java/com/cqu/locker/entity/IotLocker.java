package com.cqu.locker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 快递柜实体类
 * 对应数据库表：iot_locker
 */
@Data
@TableName("iot_locker")
public class IotLocker {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备序列号
     */
    private String serialNo;

    /**
     * 快递柜名称
     */
    private String name;

    /**
     * 部署地址
     */
    private String location;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 在线状态：0-离线，1-在线
     */
    private Integer status;

    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer enabled;

    /**
     * 温度(°C)
     */
    private BigDecimal temperature;

    /**
     * 湿度(%)
     */
    private BigDecimal humidity;

    /**
     * 最后在线时间
     */
    private LocalDateTime lastOnlineTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
