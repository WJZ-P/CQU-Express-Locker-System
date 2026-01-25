package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 寄存列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageListResponse {
    
    private Integer total;
    private List<StorageItem> list;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StorageItem {
        private String storageId;
        private String lockerName;
        private String compartmentNo;
        private String compartmentSize;
        private String status;
        private String createTime;
        private String expireTime;
        private String openCode;
        private String itemDescription;
    }
}
