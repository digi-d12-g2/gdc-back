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

    /**
     * @return LocalDate
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * @param date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return String
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

}
