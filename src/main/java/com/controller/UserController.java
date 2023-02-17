package com.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.service.UserService;
import com.dto.ResponseUserDto;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @param email
     * @param password
     * @return ResponseEntity<?>
     * @throws NotFoundException
     */
    @GetMapping
    public ResponseEntity<?> signIn(@RequestParam String email, @RequestParam String password)
            throws NotFoundException {
        try {
            ResponseUserDto signInResponse = this.userService.signIn(email, password);
            return ResponseEntity.status(200).body(signInResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    /**
     * @param id
     * @return Integer
     */
    @GetMapping("vacations_avalaible/{id}")
    public Integer getVacationsAvalaible(@PathVariable Long id) {
        return this.userService.getVacationsAvalaibleById(id);
    }

    /**
     * @param id
     * @return Integer
     */
    @GetMapping("rtt_avalaible/{id}")
    public Integer getRttAvalaible(@PathVariable Long id) {
        return this.userService.getRttAvalaibleById(id);
    }
}
