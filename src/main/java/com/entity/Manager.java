package com.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "manager")
public class Manager extends User {
	
	@Column(nullable = false, length = 50)
	private String department;

    @OneToMany(mappedBy = "absence")
	private List<User> users;


    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
