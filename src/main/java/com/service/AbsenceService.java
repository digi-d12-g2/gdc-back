package com.service;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.repository.AbsenceRepository;

import com.entity.Absence;
import com.exception.ResourceNotFoundException;

import com.enums.*;
import com.dto.ResponseAbsenceDto;

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
	public ResponseAbsenceDto addAbsence(Absence absence) {

		if (checkAbsenceIsValid(absence)){
			this.absenceRepository.save(absence);

			ResponseAbsenceDto response = new ResponseAbsenceDto(
				absence.getId(),
				absence.getDate_start(),
				absence.getDate_end(),
				absence.getType(),
				absence.getStatus(),
				absence.getUser().getId(),
				absence.getReason());
	
			return response;
		} else {
			return null;
		}
	}

	@Transactional
	public ResponseAbsenceDto updateAbsence(Long id, Absence absence){
		
		if (checkUpdateIsValid(absence)){
			Absence absenceToUpdate = getAbsence(id);
			absenceToUpdate.setDate_start(absence.getDate_start());
			absenceToUpdate.setDate_end(absence.getDate_end());
			absenceToUpdate.setReason(absence.getReason());
			absenceToUpdate.setType(absence.getType());

			this.absenceRepository.save(absenceToUpdate);

			ResponseAbsenceDto response = new ResponseAbsenceDto(
				absenceToUpdate.getId(),
				absenceToUpdate.getDate_start(),
				absenceToUpdate.getDate_end(),
				absenceToUpdate.getType(),
				absenceToUpdate.getStatus(),
				absenceToUpdate.getUser().getId(),
				absenceToUpdate.getReason());
	
			return response;
		} else {
			return null;
		}

	}
	
	@Transactional
	public void deleteAbsence(Long id) {
		this.absenceRepository.deleteById(id);
	}

	@Transactional
	public ResponseAbsenceDto confirmAbsence(Long id, Status status) {
		Optional<Absence> absence = this.absenceRepository.findById(id);

		if(absence.isPresent() && absence.get().getStatus().toString().equals("EN_ATTENTE_VALIDATION")) {
			Absence absenceToUpdate = absence.get();
			absenceToUpdate.setStatus(status);

			this.absenceRepository.save(absenceToUpdate);
	
			ResponseAbsenceDto response = new ResponseAbsenceDto(
				absenceToUpdate.getId(),
				absenceToUpdate.getDate_start(),
				absenceToUpdate.getDate_end(),
				absenceToUpdate.getType(),
				absenceToUpdate.getStatus(),
				absenceToUpdate.getUser().getId(),
				absenceToUpdate.getReason());
	
			return response;
		} else {
			throw new ResourceNotFoundException("Absence introuvable.");
		}
		
	
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
