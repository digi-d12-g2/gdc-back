package com.repository;

import com.entity.Absence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AbsenceRepository extends JpaRepository<Absence, Long>{

    @Query("SELECT a FROM Absence a INNER JOIN User u ON u.id = a.user WHERE u.id = :id AND a.type != 'RTT_EMPLOYEUR' ORDER BY a.date_start DESC")
    public List<Absence> findByUserId(@Param("id") Long id);

    @Query("SELECT a FROM Absence a INNER JOIN User u ON u.id = a.user WHERE u.manager = :id AND a.type != 'RTT_EMPLOYEUR' ORDER BY a.date_start DESC")
    public List<Absence> findByManagerId(@Param("id") Long id);
    
    @Query("SELECT a FROM Absence a WHERE a.type = 'RTT_EMPLOYEUR'")
    public List<Absence> findEmployerRtt();
}
