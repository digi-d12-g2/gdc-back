package com.service;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.repository.AbsenceRepository;
import com.repository.UserRepository;
import com.entity.Absence;
import com.entity.User;
import com.exception.ResourceNotFoundException;

import com.enums.*;
import com.dto.RequestAbsenceDto;
import com.dto.ResponseAbsenceDto;

@Service
public class AbsenceService {

    private AbsenceRepository absenceRepository;
	private UserRepository userRepository;
	private EmployerRttService employerRttService;

    public AbsenceService(
		AbsenceRepository absenceRepository,
		UserRepository userRepository, 
		EmployerRttService employerRttService) {
		this.absenceRepository = absenceRepository;
		this.userRepository = userRepository;
		this.employerRttService = employerRttService;
	}
	
	public List<Absence> getAbsences() {
		return this.absenceRepository.findAll();
	}

	public List<Absence> getAbsencesFromManager(long id) {
		return this.absenceRepository.findByManagerId(id);
	}

	public List<Absence> getAbsencesToValidateFromManager(long id) {
		return this.absenceRepository.findAbsencesToValidateByManagerId(id);
	}

	public List<Absence> getEmployerRtt(){
		return this.absenceRepository.findEmployerRtt();
	}

	public List<Absence> getEmployerRttList(){
		return this.absenceRepository.findEmployerRttList();
	}

	public List<Absence> getAbsencesFromUser(long id) {

		Optional<User> optionnalUser = this.userRepository.findById(id);

		if(optionnalUser.isEmpty()) {
			throw new ResourceNotFoundException("Utilisateur introuvable");
		}

		return this.absenceRepository.findByUserId(id);

	}
	
	public Absence getAbsence(Long id) {
		return this.absenceRepository.getReferenceById(id);
	}
	
	@Transactional
	public ResponseAbsenceDto addAbsence(RequestAbsenceDto requestAbsence) {

		Absence absence = new Absence(
			requestAbsence.getDate_start().plusHours(1),
			requestAbsence.getDate_end().plusHours(1),
			requestAbsence.getType(),
			Status.INITIALE,
			requestAbsence.getReason()
		);

		if (checkAbsenceIsValid(absence)){

			 if(absence.getType() != Type.RTT_EMPLOYEUR){
				Optional<User> optionnalUser = this.userRepository.findById(requestAbsence.getUserId());

				optionnalUser.ifPresentOrElse(
						(User u) -> {
							absence.setUser(u);
						}, () -> {
							throw new ResourceNotFoundException("Utilisateur introuvable");
						});
			}

			this.absenceRepository.save(absence);

			ResponseAbsenceDto response = new ResponseAbsenceDto(
				absence.getId(),
				absence.getDate_start(),
				absence.getDate_end(),
				absence.getType(),
				absence.getStatus(),
				Objects.nonNull(absence.getUser())?absence.getUser().getId():null,
				absence.getReason());
	
			return response;
		} else {
			throw new ResourceNotFoundException("Impossible de créer la demande de congé");
		}
	}
	
	@Transactional
	public ResponseAbsenceDto updateAbsence(Long id, Absence absence){
		
		if (checkUpdateIsValid(absence)){
			Absence absenceToUpdate = getAbsence(id);

			absenceToUpdate.setDate_start(absence.getDate_start().plusHours(1));
			absenceToUpdate.setDate_end(absence.getDate_end().plusHours(1));
			absenceToUpdate.setReason(absence.getReason());
			absenceToUpdate.setStatus(Status.INITIALE);
			absenceToUpdate.setType(absence.getType());

			this.absenceRepository.save(absenceToUpdate);

			ResponseAbsenceDto response = new ResponseAbsenceDto(
				absenceToUpdate.getId(),
				absenceToUpdate.getDate_start(),
				absenceToUpdate.getDate_end(),
				absenceToUpdate.getType(),
				absenceToUpdate.getStatus(),
				Objects.nonNull(absenceToUpdate.getUser())?absenceToUpdate.getUser().getId():null,
				absenceToUpdate.getReason());
	
			return response;
		} else {
			throw new ResourceNotFoundException("Impossible de modifier la demande de congé");
		}

	}
	
	@Transactional
	public void deleteAbsence(Long id) {

		if(getAbsence(id).getType() == Type.RTT_EMPLOYEUR){
			this.employerRttService.incrementEmployerRTT(1L, this.employerRttService.getEmployerRTT(1L));
		}

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

	@Transactional
	public void modifyAbsencesStatus() {
		List<Absence> absences = this.absenceRepository.findInitialesAbsences();

		for(Absence absence : absences) {
			User user = this.userRepository.getReferenceById(absence.getUser().getId());

			if(user.getVacations_avalaible() > 0){
				absence.setStatus(Status.EN_ATTENTE_VALIDATION);
			} else {
				absence.setStatus(Status.REJETEE);
			}

			this.absenceRepository.save(absence);
        }
	}

	@Transactional
	public void modifyEmployerRttStatus(){

		List<Absence> absences = this.absenceRepository.findInitialesEmployerRtt();

		for(Absence absence : absences) {

			if(this.employerRttService.checkEmployerRttIsValid(absence) == true){
				absence.setStatus(Status.VALIDEE);
				this.employerRttService.decrementEmployerRTT(1L, this.employerRttService.getEmployerRTT(1L));
			} else {
				absence.setStatus(Status.REJETEE);
			}

			this.absenceRepository.save(absence);
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
		return (absence.getStatus() == Status.INITIALE);
	}

	private boolean checkStatusIsInitialOrRejete(Absence absence){
		return ((absence.getStatus() == Status.INITIALE) || (absence.getStatus() == Status.REJETEE));
	}

}
