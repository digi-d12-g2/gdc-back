package com.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "public_holidays")
public class PublicHolidays extends BaseEntity {
    
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 50)
    private String label;


    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
