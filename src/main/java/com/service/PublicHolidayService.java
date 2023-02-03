package com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.entity.PublicHolidays;
import com.repository.PublicHolidayRepository;

@Service
public class PublicHolidayService {

    private PublicHolidayRepository repository;

    public PublicHolidayService(PublicHolidayRepository repository) {
        super();
        this.repository = repository;
    }
    
    public List<PublicHolidays> list() {
        return this.repository.findAll();
    }

    public Optional<PublicHolidays> findById(Integer id) {
        return this.repository.findById(id);
    }

    /**
     * @param id
     * @return String
     * @throws NotFoundException
    */
    public String delete(Integer id) throws NotFoundException {

        Optional<PublicHolidays>publicHolidays = this.repository.findById(id);

        if(publicHolidays != null) {
            this.repository.deleteById(id);
            return "Public holidays deleted";
        }else {
			throw new NotFoundException();
        }
    }
}
