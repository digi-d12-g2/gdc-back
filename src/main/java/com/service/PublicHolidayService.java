package com.service;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.entity.PublicHolidays;
import com.repository.PublicHolidayRepository;
import com.exception.ResourceNotFoundException;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class PublicHolidayService {

    private PublicHolidayRepository repository;

    public PublicHolidayService(PublicHolidayRepository repository) {
        super();
        this.repository = repository;
    }
    
    public void apiAddPublicHolidays() {
        final String uri = "https://calendrier.api.gouv.fr/jours-feries/metropole.json";

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
       
        try {
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
            Map<String, String> map = objectMapper.readValue(result, typeRef);

            for (Map.Entry<String,String> mapentry : map.entrySet()) {

                String dateStr = mapentry.getKey().toString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
                LocalDate date = LocalDate.parse(dateStr, formatter);

                if(checkDateNotExists(date)){
                    PublicHolidays publicHoliday = new PublicHolidays();

                    publicHoliday.setLabel(mapentry.getValue().toString());
                    publicHoliday.setDate(date);
    
                    this.repository.save(publicHoliday);
                }

            }

        } catch(Exception e) {
           e.printStackTrace();
        }

    }

    @Transactional
	public PublicHolidays addPublicHoliday(PublicHolidays publicHoliday) {
		if (checkPublicHolidayIsValid(publicHoliday)){
			return this.repository.save(publicHoliday);
		} else {
			throw new ResourceNotFoundException("Impossible de créer le jour férié");
		}
	}

    @Transactional
	public PublicHolidays updatePublicHoliday(Long id, PublicHolidays publicHoliday) {

        PublicHolidays publicHolidayToUpdate = findById(id);

        publicHolidayToUpdate.setDate(publicHoliday.getDate());
        publicHolidayToUpdate.setLabel(publicHoliday.getLabel());

        return this.repository.save(publicHolidayToUpdate);
    }

    public List<PublicHolidays> list() {
        return this.repository.findAll();
    }

    public List<PublicHolidays> listSortDate(Integer year) {
        return this.repository.findSortDate(year);
    }

    public PublicHolidays findById(Long id) {
        return this.repository.getReferenceById(id);
    }

    /**
     * @param id
     * @return String
     * @throws NotFoundException
    */
    public String delete(Long id) throws NotFoundException {

        Optional<PublicHolidays>publicHolidays = this.repository.findById(id);

        if(publicHolidays != null) {
            this.repository.deleteById(id);
            return "Public holidays deleted";
        }else {
			throw new NotFoundException();
        }
    }

    private boolean checkPublicHolidayIsValid(PublicHolidays publicHoliday){
        return checkDateNotExists(publicHoliday.getDate());
    }

    private boolean checkDateNotExists(LocalDate date){
        return (Objects.isNull(this.repository.findByDate(date)));
	}
}
