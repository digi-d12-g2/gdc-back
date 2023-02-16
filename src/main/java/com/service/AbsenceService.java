package com.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

import java.time.temporal.ChronoUnit;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.repository.AbsenceRepository;
import com.repository.UserRepository;
import com.repository.PublicHolidayRepository;
import com.entity.Absence;
import com.entity.User;
import com.entity.PublicHolidays;
import com.exception.ResourceNotFoundException;

import com.enums.*;
import com.dto.RequestAbsenceDto;
import com.dto.ResponseAbsenceDto;

@Service
public class AbsenceService {

    private AbsenceRepository absenceRepository;
    private UserRepository userRepository;
    private EmployerRttService employerRttService;
    private PublicHolidayRepository publicHolidayRepository;
    private UserService userService;

    public AbsenceService(
		AbsenceRepository absenceRepository,
		UserRepository userRepository, 
		EmployerRttService employerRttService,
		PublicHolidayRepository publicHolidayRepository,
    UserService userService
	) {
		this.absenceRepository = absenceRepository;
		this.userRepository = userRepository;
		this.employerRttService = employerRttService;
		this.publicHolidayRepository = publicHolidayRepository;
    this.userService = userService;
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
	
    public List<Absence> getEmployerRttAdmin(Integer year) {
        return this.absenceRepository.findEmployerRttForAdmin(year);
    }

	public List<Absence> getEmployerRttUser(Integer year){
		return this.absenceRepository.findEmployerRttForEmployee(year);
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
	
	/**
	 * Création d'une absence
	 * @param requestAbsence
	 * @return
	 */
	@Transactional
	public ResponseAbsenceDto addAbsence(RequestAbsenceDto requestAbsence) {

		Absence absence = new Absence(
			requestAbsence.getDate_start(),
			requestAbsence.getDate_end(),
			requestAbsence.getType(),
			Status.INITIALE,
			requestAbsence.getReason()
		);

		if(absence.getType() != Type.RTT_EMPLOYEUR){
			Optional<User> optionnalUser = this.userRepository.findById(requestAbsence.getUserId());

			optionnalUser.ifPresentOrElse(
					(User u) -> {
						absence.setUser(u);
					}, () -> {
						throw new ResourceNotFoundException("Utilisateur introuvable");
					});
		}

		if ( ( absence.getType() != Type.RTT_EMPLOYEUR && checkAbsenceIsValid(absence)) | ( absence.getType() == Type.RTT_EMPLOYEUR && checkRttIsValid(absence)  ) ){

			Long countL = ChronoUnit.DAYS.between(absence.getDate_start(), absence.getDate_end());
			Integer count = countL.intValue() + 1;
			this.userService.decrementUserVacations(absence.getUser().getId(), count);

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

			absenceToUpdate.setDate_start(absence.getDate_start());
			absenceToUpdate.setDate_end(absence.getDate_end());
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

		if((getAbsence(id).getType() == Type.RTT_EMPLOYEUR) && getAbsence(id).getStatus() == Status.VALIDEE){
			this.employerRttService.incrementEmployerRTT(1L, this.employerRttService.getEmployerRTT(1L));
		} else if ((getAbsence(id).getType() != Type.RTT_EMPLOYEUR) && getAbsence(id).getStatus() == Status.VALIDEE) {
			Long countL = ChronoUnit.DAYS.between(getAbsence(id).getDate_start(), getAbsence(id).getDate_end());
			Integer count = countL.intValue() + 1;
			this.userService.incrementUserVacations(getAbsence(id).getUser().getId(), count);
		}

		this.absenceRepository.deleteById(id);

	}

	@Transactional
	public ResponseAbsenceDto confirmAbsence(Long id, Status status) {
		Optional<Absence> absence = this.absenceRepository.findById(id);

		if(absence.isPresent() && absence.get().getStatus().toString().equals("EN_ATTENTE_VALIDATION")) {
			Absence absenceToUpdate = absence.get();
			absenceToUpdate.setStatus(status);

			if(status == Status.REJETEE){
				Long countL = ChronoUnit.DAYS.between(absence.get().getDate_start(), absence.get().getDate_end());
				Integer count = countL.intValue() + 1;
				this.userService.incrementUserVacations(absence.get().getUser().getId(), count);
			}

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
				Long countL = ChronoUnit.DAYS.between(absence.getDate_start(), absence.getDate_end());
				Integer count = countL.intValue() + 1;
				this.userService.incrementUserVacations(absence.getUser().getId(), count);

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
				this.absenceRepository.save(absence);
			} else {
				this.absenceRepository.deleteById(absence.getId());
			}
			
        }
	}

	private boolean checkRttIsValid(Absence absence) {
		if(
			checkDateStartBeforeDateEnd(absence) &&
			checkDateAndTypeNotNull(absence) && 
			checkStatusIsInitial(absence) &&
			checkDatesAreNotPublicHoliday(absence) &&
			checkDatesAreNotRttEmployer(absence) &&
			checkDatesAreNotWeekEnd(absence) &&
			checkNoDateInPast(absence)
		) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkAbsenceIsValid(Absence absence){

		if(
			checkDateStartBeforeDateEnd(absence) &&
			checkCongesSansSoldeNeedReason(absence) &&
			checkDateAndTypeNotNull(absence) && 
			checkStatusIsInitial(absence) &&
			checkDatesAreNotPublicHoliday(absence) &&
			checkDatesAreNotRttEmployer(absence) &&
			checkDatesAreNotWeekEnd(absence) &&
			checkNoAbsenceInDateRange(absence)
		) {
			return true;
		} else {
			return false;
		}
		
	}

	private boolean checkUpdateIsValid(Absence absence){

		if(
			checkCongesSansSoldeNeedReason(absence) &&
			checkStatusIsInitialOrRejete(absence) &&
			checkNoAbsenceInDateRange(absence)
		) {
			return true;
		} else {
			return false;
		}
	}

	private Boolean checkDatesAreNotPublicHoliday(Absence absence) {
		List<PublicHolidays> publicHolidays = this.publicHolidayRepository.getPublicHolidaysFromTwoDates(absence.getDate_start(), absence.getDate_end());
		
		return publicHolidays.isEmpty();
	}

	private Boolean checkDatesAreNotRttEmployer(Absence absence) {
		List<Absence> absences = this.absenceRepository.getRttEmployeursFromTwoDates(absence.getDate_start(), absence.getDate_end());

		return absences.isEmpty();
	}

	private Boolean checkDatesAreNotWeekEnd(Absence absence) {
		if (
			absence.getDate_start().getDayOfWeek().toString().equals("SUNDAY") ||
			absence.getDate_start().getDayOfWeek().toString().equals("SATURDAY") ||
			absence.getDate_end().getDayOfWeek().toString().equals("SUNDAY") ||
			absence.getDate_end().getDayOfWeek().toString().equals("SATURDAY")
		) {
			return false;
		} else {
			return true;
		}
	}

	private Boolean checkNoAbsenceInDateRange(Absence absence) {
		List<Absence> absences = this.absenceRepository.getAbsencesFromTwoDatesRangeAndAbsenceIdAndUserId(absence.getDate_start(), absence.getDate_end(), absence.getId(), absence.getUser().getId());

		return absences.isEmpty();
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

	private boolean checkNoDateInPast(Absence absence) {
		return absence.getDate_start().isAfter(LocalDate.now());
	}

}
