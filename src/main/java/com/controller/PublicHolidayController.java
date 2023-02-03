package com.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.entity.PublicHolidays;
import com.service.PublicHolidayService;

import java.util.List;

@RestController
@RequestMapping("public-holidays")
public class PublicHolidayController {
    
    private PublicHolidayService publicHolidayService;

    public PublicHolidayController(
        PublicHolidayService publicHolidayService
    ) {
        super();
        this.publicHolidayService = publicHolidayService;
    }

    @GetMapping
    public List<PublicHolidays> list() {
        return this.publicHolidayService.list();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePublicHoliday(@PathVariable("id") Integer id) throws NotFoundException {
        try {
            String deleteResponse = this.publicHolidayService.delete(id);

            return ResponseEntity.status(200).body(deleteResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

}
