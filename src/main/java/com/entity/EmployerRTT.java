package com.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employer_rtt")
public class EmployerRTT extends BaseEntity {
    
    @Column(nullable = false)
    private Integer rtt_available;


    public Integer getRtt_available() {
        return this.rtt_available;
    }

    public void setRtt_available(Integer rtt_available) {
        this.rtt_available = rtt_available;
    }

}
