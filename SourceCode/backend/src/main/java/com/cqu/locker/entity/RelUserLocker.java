package com.cqu.locker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户-快递柜绑定关系实体类
 */
@Data
@TableName("rel_user_locker")
public class RelUserLocker {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 快递柜ID
     */
    private Long lockerId;
    
    /**
     * 是否默认快递柜
     */
    private Integer isDefault;
    
    private LocalDateTime createTime;
}
