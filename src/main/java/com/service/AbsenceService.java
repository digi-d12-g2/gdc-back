package com.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.repository.AbsenceRepository;

import com.entity.Absence;

@Service
public class AbsenceService {

    private AbsenceRepository absenceRepository;

    public AbsenceService(AbsenceRepository absenceRepository) {
		this.absenceRepository = absenceRepository;
	}
	
	public List<Absence> getAbsences() {
		return this.absenceRepository.findAll();
	}
	
	public Absence getAbsence(Long id) {
		return this.absenceRepository.getReferenceById(id);
	}
	
	@Transactional
	public Absence addAbsence(Absence absence) {

		if (checkAbsenceIsValid(absence)){
			return this.absenceRepository.save(absence);
		} else {
			return null;
		}
	}

	@Transactional
	public Absence updateAbsence(Long id, Absence absence){
		
		if (checkUpdateIsValid(absence)){
			Absence absenceToUpdate = getAbsence(id);
			absenceToUpdate.setDate_start(absence.getDate_start());
			absenceToUpdate.setDate_end(absence.getDate_end());
			absenceToUpdate.setReason(absence.getReason());
			absenceToUpdate.setType(absence.getType());

			return this.absenceRepository.save(absenceToUpdate);
		} else {
			return null;
		}

	}
	
	@Transactional
	public void deleteAbsence(Long id) {
		this.absenceRepository.deleteById(id);
	}



	private boolean checkAbsenceIsValid(Absence absence){

		checkDateStartBeforeDateEnd(absence);
		checkCongesSansSoldeNeedReason(absence);
		checkDateAndTypeNotNull(absence);
		checkStatusIsInitial(absence);

		/** il manque date début ne peut pas être un jour férié, rtt employeurs ou w-e, pareil pour date fin */
		/** il manque le chevauchement */

		return true;

	}

	private boolean checkUpdateIsValid(Absence absence){

		checkCongesSansSoldeNeedReason(absence);
		checkStatusIsInitialOrRejete(absence);

		return true;
	}


	private boolean checkDateStartBeforeDateEnd(Absence absence){
		return absence.getDate_start().isBefore(absence.getDate_end());
	}

	private boolean checkCongesSansSoldeNeedReason(Absence absence){
		return (absence.getType().toString().equals("CONGES_SANS_SOLDE") && !absence.getReason().isEmpty()) || !absence.getType().toString().equals("CONGES_SANS_SOLDE");
	}

	private boolean checkDateAndTypeNotNull(Absence absence){
		return (!absence.getDate_start().toString().isEmpty() || !absence.getDate_end().toString().isEmpty() || !absence.getType().toString().isEmpty());
	}

	private boolean checkStatusIsInitial(Absence absence){
		return absence.getStatus().toString().equals("INITIALE");
	}

	private boolean checkStatusIsInitialOrRejete(Absence absence){
		return (absence.getStatus().toString().equals("INITIAL") || absence.getStatus().toString().equals("REJETEE"));
	}

}
