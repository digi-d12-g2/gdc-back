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

			Integer count = getCount(absence);
			this.userService.decrementUserVacations(absence.getUser().getId(), count);
		}
		
		if((absence.getType() != Type.RTT_EMPLOYEUR && checkAbsenceIsValid(absence)) | (absence.getType() == Type.RTT_EMPLOYEUR && checkRttIsValid(absence))){

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
		
		Absence absenceToUpdate = getAbsence(id);

		if (checkUpdateIsValid(absence, absenceToUpdate)){

			if(absence.getType() != Type.RTT_EMPLOYEUR){

				if(absenceToUpdate.getStatus() == Status.INITIALE){
					if(getCount(absence) > getCount(absenceToUpdate)){
						Integer diff = getCount(absence) - getCount(absenceToUpdate);
						System.out.println("pok  " + diff);

						this.userService.decrementUserVacations(absenceToUpdate.getUser().getId(), diff);
					} else if(getCount(absence) < getCount(absenceToUpdate)) {
						Integer diff = getCount(absenceToUpdate) - getCount(absence);
						System.out.println("ok  " + diff);
						this.userService.incrementUserVacations(absenceToUpdate.getUser().getId(), diff);
					}
				} else {
					Integer count = getCount(absence);
					System.out.println(count);
					this.userService.decrementUserVacations(absenceToUpdate.getUser().getId(), count);
				}
			}

			absenceToUpdate.setDate_start(absence.getDate_start());
			absenceToUpdate.setDate_end(absence.getDate_end());
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
		} else if ((getAbsence(id).getType() != Type.RTT_EMPLOYEUR) && (getAbsence(id).getStatus() == Status.VALIDEE || getAbsence(id).getStatus() == Status.EN_ATTENTE_VALIDATION)) {
			Integer count = getCount(getAbsence(id));
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
				Integer count = getCount(absence.get());
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
				Integer count = getCount(absence);
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
		if (!checkDateStartBeforeDateEnd(absence)) {
			throw new ResourceNotFoundException("La date de début doit être inférieur à la date de fin.");
		} else if (!checkDateAndTypeNotNull(absence)) {
			throw new ResourceNotFoundException("Les dates et le type ne peuvent pas être vides.");
		} else if (!checkStatusIsInitial(absence)) {
			throw new ResourceNotFoundException("Le status doit être initial.");
		} else if (!checkDatesAreNotPublicHoliday(absence)) {
			throw new ResourceNotFoundException("Le jour de début et de fin ne peuvent pas être sur un jour férié.");
		} else if (!checkDatesAreNotRttEmployer(absence)) {
			throw new ResourceNotFoundException("Le jour de début et de fin ne peuvent pas être sur un RTT employeur.");
		} else if (!checkDatesAreNotWeekEnd(absence)) {
			throw new ResourceNotFoundException("Le jour de début et de fin ne peuvent pas être un week-end.");
		} else if (!checkNoDateInPast(absence)) {
			throw new ResourceNotFoundException("Le jour de début ne peut pas être dans le passé.");
		}

		return true;
	}

	private Integer getCount(Absence absence){
		System.out.println("Date deb  : " + absence.getDate_start());
		System.out.println("Date fin  : " + absence.getDate_end());

		Long countL = ChronoUnit.DAYS.between(absence.getDate_start(), absence.getDate_end());
		System.out.println("COUNTL  : " + countL);
		Integer count = countL.intValue() + 1;
		System.out.println("COUNT  : " + count);

		return count;
	}
	
	private boolean checkAbsenceIsValid(Absence absence) {
		if (!checkDateStartBeforeDateEnd(absence)) {
			throw new ResourceNotFoundException("La date de début doit être inférieur à la date de fin.");
		} else if (!checkCongesSansSoldeNeedReason(absence)) {
			throw new ResourceNotFoundException("Les congés sans soldes nécessitent un motif.");
		} else if (!checkDateAndTypeNotNull(absence)) {
			throw new ResourceNotFoundException("Les dates et le type ne peuvent pas être vides.");
		} else if (!checkStatusIsInitial(absence)) {
			throw new ResourceNotFoundException("Le status doit être initial.");
		} else if (!checkDatesAreNotPublicHoliday(absence)) {
			throw new ResourceNotFoundException("Le jour de début et de fin ne peuvent pas être sur un jour férié.");
		} else if (!checkDatesAreNotRttEmployer(absence)) {
			throw new ResourceNotFoundException("Le jour de début et de fin ne peuvent pas être sur un RTT employeur.");
		} else if (!checkDatesAreNotWeekEnd(absence)) {
			throw new ResourceNotFoundException("Le jour de début et de fin ne peuvent pas être un week-end.");
		} else if (!checkNoDateInPast(absence)) {
			throw new ResourceNotFoundException("Le jour de début ne peut pas être dans le passé.");
		} else if (!checkNoAbsenceInDateRange(absence)) {
			throw new ResourceNotFoundException("Un jour de congé existe déjà sur cette période.");
		}
 
		return true;
	}

	private boolean checkUpdateIsValid(Absence absence, Absence absenceToUpdate){

		if (!checkDateStartBeforeDateEnd(absence)) {
			throw new ResourceNotFoundException("La date de début doit être inférieur à la date de fin.");
		} else if (!checkStatusIsInitialOrRejete(absenceToUpdate)) {
			throw new ResourceNotFoundException("Le status doit être initial ou rejete.");
		} else if (!checkCongesSansSoldeNeedReason(absence)) {
			throw new ResourceNotFoundException("Les congés sans soldes nécessitent un motif.");
		} 
		
		// else if (!checkNoAbsenceInDateRange(absence)) {
		// 	throw new ResourceNotFoundException("Un jour de congé existe déjà sur cette période.");
		// }

		return true;

	}

	private Boolean checkDatesAreNotPublicHoliday(Absence absence) {
		System.out.println(absence.getDate_start());
		System.out.println(absence.getDate_end());

		List<PublicHolidays> publicHolidays = this.publicHolidayRepository.getPublicHolidaysFromTwoDates(absence.getDate_start(), absence.getDate_end());
		for(PublicHolidays pH : publicHolidays){
			System.out.println(pH.getLabel());
		}
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
		return (absence.getDate_start().isBefore(absence.getDate_end()) | absence.getDate_start().isEqual(absence.getDate_end()));
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
