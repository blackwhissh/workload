package com.blackwhissh.workload.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "work_schedule")
public class WorkSchedule {
    @Id
    @SequenceGenerator(name = "workschedule_id_seq", sequenceName = "workschedule_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "workschedule_id_seq", strategy = GenerationType.SEQUENCE)
    private Integer workScheduleId;
    private Integer month;
    private Integer year;
    @OneToMany(fetch = FetchType.EAGER)
    private List<WorkDay> days;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public WorkSchedule() {
    }

    public WorkSchedule(Integer month, Integer year, List<WorkDay> days, User user) {
        this.month = month;
        this.year = year;
        this.days = days;
        this.user = user;
    }

    public Integer getWorkScheduleId() {
        return workScheduleId;
    }

    public void setWorkScheduleId(Integer workScheduleId) {
        this.workScheduleId = workScheduleId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<WorkDay> getDays() {
        return days;
    }

    public void setDays(List<WorkDay> days) {
        this.days = days;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
