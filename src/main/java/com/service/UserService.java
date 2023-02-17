package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;

import com.dto.ResponseUserDto;
import com.entity.User;

import com.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    // public UserService(UserRepository repository) {
    //     super();
    //     this.repository = repository;
    // }
    
    public ResponseUserDto signIn(String email, String password) {
        User user = this.repository.findByEmailAndPassword(email, password);
        if (user != null) {
            return this.convertToDto(user);
        }
        else {
            throw new NotFoundException("User not found");
        }
    }

    public Integer getVacationsAvalaibleById(Long id){
        return this.repository.findVacations_avalaibleById(id);
    }

    public User getUser(Long id) {
		return this.repository.getReferenceById(id);
	}

	private ResponseUserDto convertToDto(User user) {

        ResponseUserDto userDto = new ResponseUserDto(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getIsAdmin(),
            user.getVacations_avalaible(),
            user.getRtt(),
            user.getDepartment()
        );

		return userDto;
	}

    @Transactional
	public User decrementUserVacations(Long id, Integer count){
		
        User userToDecrement = getUser(id);
        userToDecrement.setVacations_avalaible(userToDecrement.getVacations_avalaible() - count);

        return this.repository.save(userToDecrement);

	}

    @Transactional
	public User incrementUserVacations(Long id, Integer count){
		
        User userToIncrement = getUser(id);
        userToIncrement.setVacations_avalaible(userToIncrement.getVacations_avalaible() + count);

        return this.repository.save(userToIncrement);

	}

}
