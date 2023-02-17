package com.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employer_rtt")
public class EmployerRTT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rtt_available;

    /**
     * @return Integer
     */
    public Integer getRtt_available() {
        return this.rtt_available;
    }

    /**
     * @param rtt_available
     */
    public void setRtt_available(Integer rtt_available) {
        this.rtt_available = rtt_available;
    }

    /**
     * @return Long
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

}
