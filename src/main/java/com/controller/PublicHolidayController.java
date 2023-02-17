package com.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.entity.PublicHolidays;
import com.service.PublicHolidayService;

import java.util.List;

@RestController
@RequestMapping("public-holidays")
public class PublicHolidayController {

    private PublicHolidayService publicHolidayService;

    public PublicHolidayController(
            PublicHolidayService publicHolidayService) {
        super();
        this.publicHolidayService = publicHolidayService;
    }

    /**
     * @param year
     * @return List<PublicHolidays>
     */
    @GetMapping("/{year}")
    public List<PublicHolidays> list(@PathVariable("year") Integer year) {
        return this.publicHolidayService.listSortDate(year);
    }

    /**
     * @param publicHoliday
     * @return ResponseEntity<?>
     */
    @PostMapping()
    public ResponseEntity<?> addPublicHoliday(@RequestBody PublicHolidays publicHoliday) {
        return ResponseEntity.ok().body(this.publicHolidayService.addPublicHoliday(publicHoliday));
    }

    /**
     * @param id
     * @param publicHoliday
     */
    @PutMapping("/{id}")
    public void updatePublicHoliday(@PathVariable Long id, @RequestBody PublicHolidays publicHoliday) {
        publicHolidayService.updatePublicHoliday(id, publicHoliday);
    }

    /**
     * @param id
     * @return ResponseEntity<?>
     * @throws NotFoundException
     */
    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePublicHoliday(@PathVariable("id") Long id) throws NotFoundException {
        try {
            String deleteResponse = this.publicHolidayService.delete(id);

            return ResponseEntity.status(200).body(deleteResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

}
