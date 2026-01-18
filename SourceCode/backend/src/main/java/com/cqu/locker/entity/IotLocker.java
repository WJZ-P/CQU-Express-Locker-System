package com.cqu.locker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
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
     * 部署地址
     */
    private String location;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 状态：0-离线，1-在线
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
