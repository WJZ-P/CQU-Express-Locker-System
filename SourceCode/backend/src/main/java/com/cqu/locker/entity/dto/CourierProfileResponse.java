package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 快递员信息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourierProfileResponse {
    
    private String courierId;
    private String name;
    private String phone;
    private String company;
    private List<BindLocker> bindLockers;
    private Integer todayDelivered;
    private Integer todayCollected;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BindLocker {
        private String lockerId;
        private String lockerName;
    }
}
