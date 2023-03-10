package com.repository;

import com.entity.Absence;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    @Query("SELECT a FROM Absence a INNER JOIN User u ON u.id = a.user WHERE u.id = :id AND a.type != 'RTT_EMPLOYEUR' ORDER BY a.date_start DESC")
    public List<Absence> findByUserId(@Param("id") Long id);

    @Query("SELECT a FROM Absence a LEFT JOIN User u ON u.id = a.user LEFT JOIN User m ON m.id = u.manager WHERE m.id = :id AND a.type != 'RTT_EMPLOYEUR' ORDER BY a.date_start DESC")
    public List<Absence> findByManagerId(@Param("id") Long id);

    @Query("SELECT a FROM Absence a LEFT JOIN User u ON u.id = a.user LEFT JOIN User m ON m.id = u.manager WHERE m.id = :id AND a.type != 'RTT_EMPLOYEUR' AND a.status = 'EN_ATTENTE_VALIDATION' ORDER BY a.date_start DESC")
    public List<Absence> findAbsencesToValidateByManagerId(@Param("id") Long id);

    @Query("SELECT a FROM Absence a WHERE a.type = 'RTT_EMPLOYEUR' AND YEAR(date_start) = :year")
    public List<Absence> findEmployerRttForAdmin(@Param("year") Integer year);

    @Query("SELECT a FROM Absence a WHERE a.type = 'RTT_EMPLOYEUR' AND a.status = 'VALIDEE' AND YEAR(date_start) = :year")
    public List<Absence> findEmployerRttForEmployee(@Param("year") Integer year);

    @Query("SELECT a FROM Absence a WHERE a.status = 'INITIALE' AND a.type != 'RTT_EMPLOYEUR'")
    public List<Absence> findInitialesAbsences();

    @Query("SELECT a FROM Absence a WHERE a.type = 'RTT_EMPLOYEUR' AND a.status = 'INITIALE'")
    public List<Absence> findInitialesEmployerRtt();

    @Query("SELECT a FROM Absence a WHERE a.type = 'RTT_EMPLOYEUR' AND a.date_start = :date_start AND a.status = 'VALIDEE'")
    public Absence findByDate(@Param("date_start") LocalDate date_start);

    @Query("SELECT a FROM Absence a WHERE (a.date_start = :firstDate OR a.date_end = :firstDate OR a.date_start = :secondDate OR a.date_end = :secondDate) AND a.type = 'RTT_EMPLOYEUR'")
    public List<Absence> getRttEmployeursFromTwoDates(@Param("firstDate") LocalDate firstDate,
            @Param("secondDate") LocalDate secondDate);

    @Query("SELECT a FROM Absence a LEFT JOIN User u ON u.id = a.user WHERE (( a.date_start <= :firstDate AND a.date_end >= :secondDate ) OR ( a.date_start <= :firstDate AND a.date_end >= :secondDate AND a.date_start >= :secondDate ) OR ( a.date_start >= :firstDate AND a.date_start <= :secondDate AND a.date_end >= :secondDate ) OR ( a.date_start >= :firstDate AND a.date_end <= :secondDate )) AND u.id = :userId AND a.status = 'VALIDEE'")
    public List<Absence> getAbsencesFromTwoDatesRangeAndAbsenceIdAndUserId(@Param("firstDate") LocalDate firstDate,
            @Param("secondDate") LocalDate secondDate, @Param("userId") Long userId);

}
