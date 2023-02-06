package com.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.entity.User;
import com.service.UserService;


@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    // public UserController(
    //     UserService userService
    // ) {
    //     super();
    //     this.userService = userService;
    // }

    @PostMapping
    public ResponseEntity<?> signIn(@PathVariable("email") String email, @PathVariable("password") String password) throws NotFoundException {
        try {
            User signInResponse = this.userService.signIn(email, password);

            return ResponseEntity.status(200).body(signInResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    
}
