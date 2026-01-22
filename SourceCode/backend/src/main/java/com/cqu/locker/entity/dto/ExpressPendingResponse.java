package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 待取快递列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressPendingResponse {
    
    private Integer total;
    
    private List<ExpressItem> list;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpressItem {
        private String expressId;
        private String trackingNo;
        private String company;
        private String lockerName;
        private String compartmentNo;
        private String status;
        private String arrivalTime;
        private String pickupCode;
        private String deadline;
    }
}
