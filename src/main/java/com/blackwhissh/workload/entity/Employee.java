package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.entity.enums.ShiftEnum;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @SequenceGenerator(name = "employee_id", sequenceName = "employee_id", allocationSize = 1)
    @GeneratedValue(generator = "employee_id", strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String workId;
    @Enumerated(value = EnumType.STRING)
    private ShiftEnum shift;
    private int set;
    @OneToMany
    private List<Schedule> scheduleList;

    public Employee() {
    }

    public Employee(String workId, ShiftEnum shift, int set) {
        this.workId = workId;
        this.shift = shift;
        this.set = set;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public ShiftEnum getShift() {
        return shift;
    }

    public void setShift(ShiftEnum shift) {
        this.shift = shift;
    }

    public int getSet() {
        return set;
    }

    public void setSet(int set) {
        this.set = set;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", workId='" + workId + '\'' +
                ", shift=" + shift +
                ", set=" + set +
                '}';
    }

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
