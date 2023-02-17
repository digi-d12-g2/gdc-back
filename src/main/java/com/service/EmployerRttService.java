package com.service;

import java.time.LocalDate;
import java.util.Objects;

import com.repository.EmployerRttRepository;
import com.repository.AbsenceRepository;
import com.entity.EmployerRTT;
import com.entity.Absence;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class EmployerRttService {

    private EmployerRttRepository employerRttRepository;
    private AbsenceRepository absenceRepository;

    public EmployerRttService(EmployerRttRepository employerRttRepository, AbsenceRepository absenceRepository) {
        this.employerRttRepository = employerRttRepository;
        this.absenceRepository = absenceRepository;
    }

    /**
     * @param id
     * @return EmployerRTT
     */
    public EmployerRTT getEmployerRTT(Long id) {
        return this.employerRttRepository.findById(id).get();
    }

    /**
     * @param id
     * @param employerRtt
     * @return EmployerRTT
     */
    @Transactional
    public EmployerRTT decrementEmployerRTT(Long id, EmployerRTT employerRtt) {

        EmployerRTT employerRttToDecrement = getEmployerRTT(id);
        employerRttToDecrement.setRtt_available(employerRtt.getRtt_available() - 1);

        return this.employerRttRepository.save(employerRttToDecrement);

    }

    /**
     * @param id
     * @param employerRtt
     * @return EmployerRTT
     */
    @Transactional
    public EmployerRTT incrementEmployerRTT(Long id, EmployerRTT employerRtt) {

        EmployerRTT employerRttToIncrement = getEmployerRTT(id);
        employerRttToIncrement.setRtt_available(employerRtt.getRtt_available() + 1);

        return this.employerRttRepository.save(employerRttToIncrement);

    }

    /**
     * @param absence
     * @return boolean
     */
    protected boolean checkEmployerRttIsValid(Absence absence) {
        return (checkDateIsAftereNow(absence) && checkIsOnWeek(absence) && checkEmployerRttAvailable()
                && checkIsSameDay(absence) && checkEmployerRttExists(absence));
    }

    /**
     * @param absence
     * @return boolean
     */
    private boolean checkIsOnWeek(Absence absence) {
        return (absence.getDate_start().getDayOfWeek().toString() != "SATURDAY")
                && (absence.getDate_start().getDayOfWeek().toString() != "SUNDAY");
    }

    /**
     * @param absence
     * @return boolean
     */
    private boolean checkDateIsAftereNow(Absence absence) {
        return (absence.getDate_start().isAfter(LocalDate.now()));
    }

    private boolean checkEmployerRttAvailable() {
        EmployerRTT employerRtt = this.employerRttRepository.getReferenceById(1L);
        return (employerRtt.getRtt_available() >= 1);
    }

    private boolean checkEmployerRttExists(Absence absence) {
        return (Objects.isNull(this.absenceRepository.findByDate(absence.getDate_start())));
    }

    private boolean checkIsSameDay(Absence absence) {
        int dayDateStart = absence.getDate_start().getDayOfYear();
        int yearDateStart = absence.getDate_start().getYear();
        int dayDateEnd = absence.getDate_end().getDayOfYear();
        int yearDateEnd = absence.getDate_end().getYear();

        return ((dayDateStart == dayDateEnd) && (yearDateStart == yearDateEnd));
    }

}
