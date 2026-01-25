package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 待揽收列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingCollectResponse {
    
    private Integer total;
    private List<CollectItem> list;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectItem {
        private String orderId;
        private String senderName;
        private String senderPhone;
        private String senderAddress;
        private String receiverAddress;
        private String itemType;
        private String weight;
        private String createTime;
        private String remark;
    }
}