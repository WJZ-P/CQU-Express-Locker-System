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
    private String status; // 空闲, 占用, 故障
    
    @PrePersist
    public void prePersist() {
        if (status == null) status = "空闲";
    }
}
