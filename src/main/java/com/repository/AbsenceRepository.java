package com.repository;

import com.entity.Absence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<Absence, Long>{
    
}
