package com.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dto.RequestAbsenceDto;
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
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getAbsence(@PathVariable Long id) {
		return ResponseEntity.ok().body(this.absenceService.getAbsence(id));
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
		return ResponseEntity.ok().body(id);
	}
    
    
}
