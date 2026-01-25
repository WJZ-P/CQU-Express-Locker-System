package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 历史记录响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponse {
    
    private Integer total;
    private Integer page;
    private Integer pageSize;
    private List<HistoryItem> list;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryItem {
        private String recordId;
        private String type;
        private String title;
        private String lockerName;
        private String compartmentNo;
        private String time;
        private String status;
        private String company;
        private String trackingNo;
    }
}