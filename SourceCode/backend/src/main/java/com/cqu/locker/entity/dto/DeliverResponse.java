package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 快递员投递响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliverResponse {
    
    private String expressId;
    private String compartmentNo;
    private String pickupCode;
}