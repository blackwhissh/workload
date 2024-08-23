package com.blackwhissh.workload.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "hour")
public class Hour {
    @Id
    @SequenceGenerator(name = "hour_id", sequenceName = "hour_id", allocationSize = 1)
    @GeneratedValue(generator = "hour_id", strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(name = "start_time")
    private LocalTime start;
    @Column(name = "end_time")
    private LocalTime end;

    public Hour() {
    }

    public Hour(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Hour{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
