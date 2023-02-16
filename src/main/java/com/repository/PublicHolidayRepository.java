package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.entity.PublicHolidays;

import java.util.List;
import java.time.LocalDate;

public interface PublicHolidayRepository  extends JpaRepository<PublicHolidays, Long> {
    public PublicHolidays findByDate(LocalDate date);

    @Query("SELECT ph FROM PublicHolidays ph WHERE date = :date AND id != :id")
    public PublicHolidays findByDateNotSameId(LocalDate date, @Param("id") Long id);

    @Query("SELECT ph FROM PublicHolidays ph WHERE YEAR(date) = :year ORDER BY date ASC")
    public List<PublicHolidays> findSortDate(@Param("year") Integer year);

    @Query("SELECT ph FROM PublicHolidays ph WHERE ph.date = :firstDate OR ph.date = :secondDate")
    public List<PublicHolidays>getPublicHolidaysFromTwoDates(@Param("firstDate") LocalDate firstDate, @Param("secondDate") LocalDate secondDate);
}
