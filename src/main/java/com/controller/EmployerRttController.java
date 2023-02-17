package com.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.EmployerRttService;

@RestController
@RequestMapping("rtt_employer")
public class EmployerRttController {

	private EmployerRttService employerRttService;

	public EmployerRttController(EmployerRttService employerRttService) {
		this.employerRttService = employerRttService;
	}

	/**
	 * @return ResponseEntity<?>
	 */
	@GetMapping
	public ResponseEntity<?> getSoldeEmployerRtt() {
		return ResponseEntity.ok().body(this.employerRttService.getEmployerRTT(1L));
	}

}
