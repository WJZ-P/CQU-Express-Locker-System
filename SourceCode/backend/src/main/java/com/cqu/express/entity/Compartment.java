package com.cqu.express.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "compartments")
public class Compartment {
    @Id
    private String id; // L001-C01

    private String lockerId;
    private String status; // 空闲, 占用, 故障, 禁用
    
    private String size; // 小, 中, 大
    private Double pressure; // 压力传感器数值
    private Double temperature; // 温度
    
    @PrePersist
    public void prePersist() {
        if (status == null) status = "空闲";
        if (size == null) size = "中";
        if (pressure == null) pressure = 0.0;
        if (temperature == null) temperature = 25.0;
    }
}
