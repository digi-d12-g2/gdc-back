package com.dto;

import java.time.LocalDateTime;

import com.enums.Status;
import com.enums.Type;

public class ResponseAbsenceDto {
    private Long id;
    private LocalDateTime date_start;
    private LocalDateTime date_end;
    private Type type;
    private Status status;
    private String reason;
    private Long userId;

    public ResponseAbsenceDto(
        Long id, 
        LocalDateTime date_start,
        LocalDateTime date_end, 
        Type type,
        Status status,
        Long userId,
        String reason
    ) {
        this.id = id;
        this.date_start = date_start;
        this.date_end = date_end;
        this.type = type;
        this.status = status;
        this.reason = reason;
        this.userId = userId;
    }

    public ResponseAbsenceDto() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
