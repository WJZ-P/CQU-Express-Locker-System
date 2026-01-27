package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取件响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupResponse {
    
    private String expressId;
    private String compartmentNo;
    private String lockerName;
}
