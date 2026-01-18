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
    private String company;
    private String employeeId;
    private LocalDateTime createTime;
}
