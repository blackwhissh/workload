package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.Utils;
import com.blackwhissh.workload.entity.enums.RoleEnum;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(name = "user_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    @Column(name = "reg_date")
    private LocalDate registrationDate;
    @Column(name = "multiplier")
    private Double multiplier;
    @Enumerated(value = EnumType.STRING)
    private RoleEnum role;
    private String tempPass;
    public User() {
    }

    public User(String firstName, String lastName, Boolean isActive, RoleEnum role) {
        this.firstName = firstName;
        this.role = role;
        this.lastName = lastName;
        this.isActive = isActive;
        String generatedPassword = Utils.generatePassword();
        this.tempPass = generatedPassword;
        this.password = generatedPassword;
    }

    public User(RoleEnum roleEnum) {
        this.role = roleEnum;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }
    public String getTempPass() {
        return tempPass;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

    public void setTempPass(String tempPass) {
        this.tempPass = tempPass;
    }
}
