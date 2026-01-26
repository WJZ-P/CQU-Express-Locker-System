package com.cqu.locker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
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
     * 快递单号（非数据库字段，兼容expressNo，实际使用trackingNo）
     */
    @TableField(exist = false)
    private String expressNo;
    /**
     * 快递柜ID（非数据库字段，通过boxId关联查询）
     */
    @TableField(exist = false)
    private Long lockerId;
    /**
     * 类型：1-投递，2-寄件，3-寄存
     */
    private Integer type; 
    private String trackingNo;
    /**
     * 快递公司
     */
    private String company;
    private String pickupCode;
    private Long boxId;
    private Long courierId;
    private Long userId;
    /**
     * 寄件人姓名
     */
    private String senderName;
    /**
     * 寄件人手机
     */
    private String senderPhone;
    /**
     * 收件人姓名
     */
    private String receiverName;
    private String receiverPhone;
    /**
     * 物品描述（寄存用）
     */
    private String itemDescription;
    /**
     * 寄存时长(小时)
     */
    private Integer duration;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    /**
     * 费用
     */
    private BigDecimal fee;
    /**
     * 状态：0-待取，1-已取，2-超时，3-已退回
     */
    private Integer status; 
    /**
     * 是否已发送通知
     */
    private Integer notificationSent;
    /**
     * 通知发送时间
     */
    private LocalDateTime notificationTime;
    private LocalDateTime createTime;
    /**
     * 取件/完成时间
     */
    private LocalDateTime finishTime;
    private LocalDateTime updateTime;
}
