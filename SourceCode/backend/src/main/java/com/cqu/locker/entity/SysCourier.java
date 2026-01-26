package com.cqu.locker.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_courier")
public class SysCourier {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    /**
     * 快递员姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String phone;
    private String company;
    private String employeeId;
    /**
     * 状态：0-离职，1-在职
     */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
