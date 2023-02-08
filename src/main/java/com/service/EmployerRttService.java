package com.service;

import java.time.LocalDateTime;

import com.repository.EmployerRttRepository;
import com.entity.EmployerRTT;
import com.entity.Absence;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class EmployerRttService {

    private EmployerRttRepository employerRttRepository;

    public EmployerRttService(EmployerRttRepository employerRttRepository) {
		this.employerRttRepository = employerRttRepository;
	}
	
	public EmployerRTT getEmployerRTT(Long id) {
		return this.employerRttRepository.getReferenceById(id);
	}

    @Transactional
	public EmployerRTT decrementEmployerRTT(Long id, EmployerRTT employerRtt){
		
        EmployerRTT employerRttToDecrement = getEmployerRTT(id);
        employerRttToDecrement.setRtt_available(employerRtt.getRtt_available() - 1);

        return this.employerRttRepository.save(employerRttToDecrement);

	}

    @Transactional
	public EmployerRTT incrementEmployerRTT(Long id, EmployerRTT employerRtt){
		
        EmployerRTT employerRttToIncrement = getEmployerRTT(id);
        employerRttToIncrement.setRtt_available(employerRtt.getRtt_available() + 1);

        return this.employerRttRepository.save(employerRttToIncrement);

	}

    protected boolean checkEmployerRttIsValid(Absence absence){             
        return (checkDateIsAftereNow(absence) && checkIsOnWeek(absence) && checkEmployerRttAvailable() && checkIsSameDay(absence));
    }
	
    private boolean checkIsOnWeek(Absence absence){
		return (absence.getDate_start().getDayOfWeek().toString() != "SATURDAY") || (absence.getDate_start().getDayOfWeek().toString() != "SUNDAY");
	}

	private boolean checkDateIsAftereNow(Absence absence){
		return (absence.getDate_start().isAfter(LocalDateTime.now()));
	}

    private boolean checkEmployerRttAvailable(){
        EmployerRTT employerRtt = this.employerRttRepository.getReferenceById(1L);
        return (employerRtt.getRtt_available() >= 1);
    }

    private boolean checkIsSameDay(Absence absence){
        int dayDateStart = absence.getDate_start().getDayOfYear();
        int yearDateStart = absence.getDate_start().getYear();
        int dayDateEnd = absence.getDate_end().getDayOfYear();
        int yearDateEnd = absence.getDate_end().getYear();

        return ((dayDateStart == dayDateEnd) && (yearDateStart == yearDateEnd));
    }

}
