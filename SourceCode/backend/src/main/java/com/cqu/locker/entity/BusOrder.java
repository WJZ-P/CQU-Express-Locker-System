package com.cqu.locker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("bus_order")
public class BusOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    /**
     * 类型：1-投递，2-寄件，3-寄存
     */
    private Integer type; 
    private String trackingNo;
    private String pickupCode;
    private Long boxId;
    private Long courierId;
    private Long userId;
    private String receiverPhone;
    /**
     * 状态：0-待取，1-已取，2-超时
     */
    private Integer status; 
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
}
