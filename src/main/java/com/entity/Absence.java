package com.entity;

import jakarta.persistence.*;

import com.enums.Status;
import com.enums.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "absence")
public class Absence extends BaseEntity {
    
    @Column(nullable = false)
    private LocalDateTime date_start;

    @Column(nullable = false)
    private LocalDateTime date_end;

    @Column(nullable = false, length = 25)
    private Type type;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;


    public LocalDateTime getDate_start() {
        return this.date_start;
    }

    public void setDate_start(LocalDateTime date_start) {
        this.date_start = date_start;
    }

    public LocalDateTime getDate_end() {
        return this.date_end;
    }

    public void setDate_end(LocalDateTime date_end) {
        this.date_end = date_end;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}