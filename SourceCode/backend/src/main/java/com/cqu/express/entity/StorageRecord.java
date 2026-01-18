package com.cqu.express.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "storage_records")
public class StorageRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String depositorName;
    private String depositorPhone;
    private String lockerId;
    private String compartmentId;
    private String pickupCode;
    
    private LocalDateTime depositTime;
    private LocalDateTime pickupTime;
    
    private String status; // 寄存中, 已取出, 已超时
    private String remark;
    
    @PrePersist
    public void prePersist() {
        if (depositTime == null) depositTime = LocalDateTime.now();
        if (status == null) status = "寄存中";
        if (pickupCode == null) pickupCode = String.valueOf((int)((Math.random()*900000)+100000));
    }
}
