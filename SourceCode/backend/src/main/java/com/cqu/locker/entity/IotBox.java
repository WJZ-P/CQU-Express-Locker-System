package com.cqu.locker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 格口实体类
 * 对应数据库表：iot_box
 */
@Data
@TableName("iot_box")
public class IotBox {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联快递柜ID
     */
    private Long lockerId;
    
    /**
     * 格口编号（如A01）
     */
    private String boxNo;
    
    /**
     * 尺寸：1-小，2-中，3-大
     */
    private Integer size;
    
    /**
     * 状态：0-空闲，1-占用，2-故障
     */
    private Integer status;
    
    /**
     * 门锁状态：0-开，1-关
     */
    private Integer isLocked;
    
    private LocalDateTime createTime;
}
