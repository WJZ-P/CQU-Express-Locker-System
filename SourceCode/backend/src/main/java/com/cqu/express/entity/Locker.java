package com.cqu.express.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "lockers")
public class Locker {
    @Id
    private String id; // L001

    private String location;
    private Integer compartmentCount;
    private String remark;
    private String status; // 正常, 故障, 禁用
    
    @PrePersist
    public void prePersist() {
        if (status == null) status = "正常";
    }
}
