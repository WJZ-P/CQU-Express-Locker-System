package com.cqu.express.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "system_config")
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String systemName;
    private Integer pickupTimeout; // 小时
    private Integer storageTimeout; // 小时
    private Boolean smsNotify;
    private Boolean timeoutReminder;
    private Boolean faceRecognition;
    
    @PrePersist
    public void prePersist() {
        if (systemName == null) systemName = "快递柜综合应用系统";
        if (pickupTimeout == null) pickupTimeout = 24;
        if (storageTimeout == null) storageTimeout = 12;
        if (smsNotify == null) smsNotify = true;
        if (timeoutReminder == null) timeoutReminder = true;
        if (faceRecognition == null) faceRecognition = false;
    }
}
