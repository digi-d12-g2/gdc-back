package com.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
public class User extends BaseEntity {
    
    @Column(nullable = false)
    private Boolean isAdmin;

    @Column(nullable = false, length = 50)
	private String firstName;
	
	@Column(nullable = false, length = 50)
	private String lastName;
	
	@Column(nullable = false, length = 50)
	private String email;
	
	@Column(nullable = false, length = 50)
	private String password;

    @Column(nullable = false)
	private Integer vacations_avalaible;

    @Column(nullable = false)
	private Integer rtt;

    @OneToMany(mappedBy = "user")
	private List<Absence> absences;

    @ManyToOne
    @JoinColumn(name = "id_manager")
    private Manager manager;


    public Boolean isIsAdmin() {
        return this.isAdmin;
    }

    public Boolean getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getVacations_avalaible() {
        return this.vacations_avalaible;
    }

    public void setVacations_avalaible(Integer vacations_avalaible) {
        this.vacations_avalaible = vacations_avalaible;
    }

    public Integer getRtt() {
        return this.rtt;
    }

    public void setRtt(Integer rtt) {
        this.rtt = rtt;
    }

    public List<Absence> getAbsences() {
        return this.absences;
    }

    public void setAbsences(List<Absence> absences) {
        this.absences = absences;
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

}
