package com.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "public_holidays")
public class PublicHolidays extends BaseEntity {
    
    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false, length = 50)
    private String label;


    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
