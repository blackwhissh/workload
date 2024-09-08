package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.config.PasswordConfig;
import com.blackwhissh.workload.entity.enums.RoleEnum;
import com.blackwhissh.workload.utils.GeneratorUtils;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(name = "user_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "user_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    @Column(name = "reg_date")
    private LocalDate registrationDate;
    @Enumerated(value = EnumType.STRING)
    private RoleEnum role;
    private String tempPass;

    public User() {
    }

    public User(Boolean isActive, RoleEnum role) {
        this.role = role;
        this.isActive = isActive;
        String generatedPassword = GeneratorUtils.generatePassword();
        this.tempPass = generatedPassword;
        this.password = PasswordConfig.passwordEncoder().encode(generatedPassword);
        this.registrationDate = LocalDate.now();
    }

    public User(RoleEnum roleEnum) {
        this.role = roleEnum;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setTempPass(String tempPass) {
        this.tempPass = tempPass;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
}
