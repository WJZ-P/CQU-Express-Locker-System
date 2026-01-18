package com.cqu.express.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "couriers")
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String name;
    private String phone;
    private String company;
    private String status; // 正常
    
    @PrePersist
    public void prePersist() {
        if (status == null) status = "正常";
    }
}
