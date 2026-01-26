package com.cqu.locker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 快递员-快递柜绑定关系实体类
 */
@Data
@TableName("rel_courier_locker")
public class RelCourierLocker {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 快递员ID
     */
    private Long courierId;
    
    /**
     * 快递柜ID
     */
    private Long lockerId;
    
    private LocalDateTime createTime;
}
