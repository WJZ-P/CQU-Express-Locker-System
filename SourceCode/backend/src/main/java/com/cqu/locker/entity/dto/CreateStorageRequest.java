package com.cqu.locker.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建寄存订单请求
 */
@Data
public class CreateStorageRequest {
    
    @NotBlank(message = "快递柜ID不能为空")
    private String lockerId;
    
    @NotBlank(message = "格口大小不能为空")
    private String compartmentSize;
    
    @NotNull(message = "寄存时长不能为空")
    private Integer duration;
    
    private String itemDescription;
}
