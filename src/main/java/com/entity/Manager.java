package com.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "manager")
public class Manager extends User {

    @Column(nullable = false, length = 50)
    private String department;

    @OneToMany(mappedBy = "manager")
    private List<User> users;

    /**
     * @return String
     */
    public String getDepartment() {
        return this.department;
    }

    /**
     * @param department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * @return List<User>
     */
    public List<User> getUsers() {
        return this.users;
    }

    /**
     * @param users
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

}
