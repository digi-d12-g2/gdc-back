package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import javax.ws.rs.NotFoundException;

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
    
    public User signIn(String email, String password) {
        User user = this.repository.findByEmailAndPassword(email, password);
        if (user != null) {
            return user;
        }
        else {
            throw new NotFoundException("User not found");
        }
    }
}
