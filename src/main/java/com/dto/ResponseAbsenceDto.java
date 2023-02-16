package com.dto;

import java.time.LocalDate;

import com.enums.Status;
import com.enums.Type;

public class ResponseAbsenceDto {
    private Long id;
    private LocalDate date_start;
    private LocalDate date_end;
    private Type type;
    private Status status;
    private String reason;
    private Long userId;
    private ResponseUserDto user;

    public ResponseAbsenceDto(
        Long id, 
        LocalDate date_start,
        LocalDate date_end, 
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

    public ResponseAbsenceDto(
        Long id, 
        LocalDate date_start,
        LocalDate date_end, 
        Type type,
        Status status,
        ResponseUserDto user,
        String reason
    ) {
        this.id = id;
        this.date_start = date_start;
        this.date_end = date_end;
        this.type = type;
        this.status = status;
        this.reason = reason;
        this.user = user;
    }

    public ResponseAbsenceDto() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate_start() {
        return this.date_start;
    }

    public void setDate_start(LocalDate date_start) {
        this.date_start = date_start;
    }

    public LocalDate getDate_end() {
        return this.date_end;
    }

    public void setDate_end(LocalDate date_end) {
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

    public ResponseUserDto getUser() {
        return this.user;
    }

    public void setUser(ResponseUserDto user) {
        this.user = user;
    }

}
