package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 寄件响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendExpressResponse {
    
    private String orderId;
    private String estimatedPickupTime;
    private BigDecimal estimatedFee;
}
