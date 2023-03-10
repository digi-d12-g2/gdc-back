package com.dto;

public class ResponseUserDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private Boolean is_admin;
    private String department;
    private Integer vacations_avalaible;
    private Integer rtt;

    public ResponseUserDto(Long id, String firstname, String lastname, String email, Boolean is_admin,
            Integer vacations_avalaible, Integer rtt, String department) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.is_admin = is_admin;
        this.vacations_avalaible = vacations_avalaible;
        this.rtt = rtt;
        this.department = department;
    }

    public ResponseUserDto(Long id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public ResponseUserDto() {
    }

    /**
     * @return Long
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return String
     */
    public String getFirstname() {
        return this.firstname;
    }

    /**
     * @param firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return String
     */
    public String getLastname() {
        return this.lastname;
    }

    /**
     * @param lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isIs_admin() {
        return this.is_admin;
    }

    public Boolean getIs_admin() {
        return this.is_admin;
    }

    public void setIs_admin(Boolean is_admin) {
        this.is_admin = is_admin;
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

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}
