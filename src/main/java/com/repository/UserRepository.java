package com.repository;

import org.springframework.data.jpa.repository.Query;

import com.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmailAndPassword(String email, String password);

    @Query("SELECT u.vacations_avalaible FROM User u WHERE id = :id")
    public Integer findVacations_avalaibleById(Long id);
}