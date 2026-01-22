package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 快递详情响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressDetailResponse {
    
    private String expressId;
    private String trackingNo;
    private String company;
    private String lockerName;
    private String lockerAddress;
    private String compartmentNo;
    private String compartmentSize;
    private String status;
    private String arrivalTime;
    private String pickupCode;
    private String deadline;
    private String senderName;
    private String senderPhone;
    private String receiverName;
    private String receiverPhone;
}
