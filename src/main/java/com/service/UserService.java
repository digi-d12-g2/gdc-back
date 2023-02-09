package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;

import com.dto.ResponseUserDto;
import com.entity.User;

import com.repository.UserRepository;

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
    
	private ResponseUserDto convertToDto(User user) {

        ResponseUserDto userDto = new ResponseUserDto(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getIsAdmin(),
            user.getVacations_avalaible(),
            user.getRtt()
        );

		return userDto;
	}
}
