package com.blackwhissh.workload.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "work_hour")
public class WorkHour {
    @Id
    @SequenceGenerator(name = "workhour_id_seq", sequenceName = "workhour_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "workhour_id_seq", strategy = GenerationType.SEQUENCE)
    private Integer workHourId;
    private Integer hour;
    private String studio;
    private Double coefficient;

    public WorkHour() {
    }

    public WorkHour(Integer hour, String studio, Double coefficient) {
        this.hour = hour;
        this.studio = studio;
        this.coefficient = coefficient;
    }

    public Integer getWorkHourId() {
        return workHourId;
    }

    public void setWorkHourId(Integer workHourId) {
        this.workHourId = workHourId;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public String toString() {
        return "WorkHour{" +
                "workHourId=" + workHourId +
                ", hour=" + hour +
                ", studio='" + studio + '\'' +
                ", coefficient=" + coefficient +
                '}';
    }
}
