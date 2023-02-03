package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.PublicHolidays;

public interface PublicHolidayRepository  extends JpaRepository<PublicHolidays, Integer> {
    
}
