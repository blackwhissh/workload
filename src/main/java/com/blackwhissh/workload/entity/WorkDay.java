package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.entity.enums.WorkDayType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "work_day")
public class WorkDay {
    @Id
    @SequenceGenerator(name = "workday_id_seq", sequenceName = "workday_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "workday_id_seq", strategy = GenerationType.SEQUENCE)
    private Integer workDayId;
    private LocalDate workDay;
    @OneToMany(fetch = FetchType.EAGER)
    private List<WorkHour> workHours;
    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private WorkDayType type;

    public WorkDay() {
    }

    public WorkDay(LocalDate workDay, List<WorkHour> workHours, WorkDayType type) {
        this.workDay = workDay;
        this.workHours = workHours;
        this.type = type;
    }

    public Integer getWorkDayId() {
        return workDayId;
    }

    public void setWorkDayId(Integer workDayId) {
        this.workDayId = workDayId;
    }

    public LocalDate getWorkDay() {
        return workDay;
    }

    public void setWorkDay(LocalDate workDay) {
        this.workDay = workDay;
    }

    public List<WorkHour> getWorkHours() {
        return workHours;
    }

    public void setWorkHours(List<WorkHour> workHours) {
        this.workHours = workHours;
    }

    public WorkDayType getType() {
        return type;
    }

    public void setType(WorkDayType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "WorkDay{" +
                "workDayId=" + workDayId +
                ", workDay=" + workDay +
                ", workHours=" + workHours +
                ", type=" + type +
                '}';
    }
}
