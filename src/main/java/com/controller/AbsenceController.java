package com.controller;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dto.RequestAbsenceDto;
import com.dto.ResponseAbsenceDto;
import com.entity.Absence;
import com.enums.Status;
import com.service.AbsenceService;

@RestController
@RequestMapping("absences")
public class AbsenceController {

    private AbsenceService absenceService;
	
	public AbsenceController(AbsenceService absenceService) {
		this.absenceService = absenceService;
	}
	
	@GetMapping
	public ResponseEntity<?> getAbsences() {
		return ResponseEntity.ok().body(this.absenceService.getAbsences());
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<?> getAbsenceFromUser(@PathVariable Long id) {
		return ResponseEntity.ok().body(this.absenceService.getAbsencesFromUser(id).stream().map(this::convertToDto).collect(Collectors.toList()));
	}

	@GetMapping("/rtt_employer")
	public ResponseEntity<?> getEmployerRtt(){
		return ResponseEntity.ok().body(this.absenceService.getEmployerRtt());
	}
	
	@PostMapping()
	public ResponseEntity<?> addAbsence(@RequestBody RequestAbsenceDto absence) {
		return ResponseEntity.ok().body(this.absenceService.addAbsence(absence));
	}

	@PutMapping("/{id}")
	public void updateAbsence(@PathVariable Long id, @RequestBody Absence absence) {
		absenceService.updateAbsence(id, absence);
	}

	@GetMapping("/confirm/{id}")
	public ResponseEntity<?> confirmAbsence(@PathVariable Long id) {
		return ResponseEntity.ok().body(this.absenceService.confirmAbsence(id, Status.VALIDEE));
	}

	@GetMapping("/decline/{id}")
	public ResponseEntity<?> declineAbsence(@PathVariable Long id) {
		return ResponseEntity.ok().body(this.absenceService.confirmAbsence(id, Status.REJETEE));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAbsence(@PathVariable Long id) {
		this.absenceService.deleteAbsence(id);
		return ResponseEntity.ok().body(this.absenceService.getAbsence(id));
	}    
    
	private ResponseAbsenceDto convertToDto(Absence absence) {
		ResponseAbsenceDto absenceDto = new ResponseAbsenceDto(
			absence.getId(),
			absence.getDate_start(),
			absence.getDate_end(),
			absence.getType(),
			absence.getStatus(),
			absence.getUser().getId(),
			absence.getReason());

		return absenceDto;
	}
}
