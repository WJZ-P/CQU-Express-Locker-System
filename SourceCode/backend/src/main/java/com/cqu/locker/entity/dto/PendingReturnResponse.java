package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 待退回列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingReturnResponse {
    
    private Integer total;
    private List<ReturnItem> list;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnItem {
        private String expressId;
        private String trackingNo;
        private String lockerName;
        private String compartmentNo;
        private String arrivalTime;
        private String deadline;
        private Integer overdueHours;
        private String receiverName;
        private String receiverPhone;
    }
}