package com.repository;

import com.entity.Absence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AbsenceRepository extends JpaRepository<Absence, Long>{

    @Query("SELECT a FROM Absence a join User u WHERE u.id = :id ORDER BY a.date_start DESC")
    public List<Absence> findByUserId(@Param("id") Long id);
    
}
