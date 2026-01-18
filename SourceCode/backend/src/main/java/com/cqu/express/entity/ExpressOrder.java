package com.cqu.express.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "express_orders")
public class ExpressOrder {
    @Id
    private String trackingNo;

    private Long courierId;
    private String courierName;
    private String receiverName;
    private String receiverPhone;
    
    private String lockerId;
    private String compartmentId;
    private String pickupCode;
    
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    
    private String status; // 待取件, 已取件, 已超时
    
    @PrePersist
    public void prePersist() {
        if (inTime == null) inTime = LocalDateTime.now();
        if (status == null) status = "待取件";
    }
}
