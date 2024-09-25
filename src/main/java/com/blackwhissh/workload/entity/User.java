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
    @OneToOne(cascade = CascadeType.ALL)
    private Employee employee;
    private LocalDate dateOfHire;

    public User() {
    }

    public User(String email, RoleEnum role, String password, LocalDate dateOfHire) {
        this.dateOfHire = dateOfHire;
        this.email = email;
        this.isActive = true;
        this.registrationDate = LocalDate.now();
        this.role = role;
        this.password = PasswordConfig.passwordEncoder().encode(password);
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

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getDateOfHire() {
        return dateOfHire;
    }

    public void setDateOfHire(LocalDate dateOfHire) {
        this.dateOfHire = dateOfHire;
    }
}
