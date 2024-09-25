package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.entity.enums.StatusEnum;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @SequenceGenerator(name = "schedule_id", sequenceName = "schedule_id", allocationSize = 1)
    @GeneratedValue(generator = "schedule_id", strategy = GenerationType.SEQUENCE)
    private Integer scheduleId;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    @Enumerated(value = EnumType.STRING)
    private StatusEnum workStatus;
    private LocalDate date;
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Hour> hours;
    private Double totalHours;

    public Schedule() {
    }

    public Schedule(Employee employee, StatusEnum workStatus, LocalDate date, List<Hour> hours) {
        this.employee = employee;
        this.workStatus = workStatus;
        this.date = date;
        this.hours = hours;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public StatusEnum getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(StatusEnum workStatus) {
        this.workStatus = workStatus;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Hour> getHours() {
        return hours;
    }

    public void setHours(List<Hour> hours) {
        this.hours = hours;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId +
                ", employee=" + employee +
                ", workStatus='" + workStatus + '\'' +
                ", date=" + date +
                ", hours=" + hours +
                '}';
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }
}
