package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.PublicHolidays;

import java.time.LocalDate;

public interface PublicHolidayRepository  extends JpaRepository<PublicHolidays, Integer> {
    public PublicHolidays findByDate(LocalDate date);
}
