package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建寄存订单响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStorageResponse {
    
    private String storageId;
    private String compartmentNo;
    private String openCode;
    private String expireTime;
    private Double fee;
}
