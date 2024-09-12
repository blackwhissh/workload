package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "swap")
public class Swap {
    @Id
    @SequenceGenerator(name = "gift_id", sequenceName = "gift_id", allocationSize = 1)
    @GeneratedValue(generator = "gift_id", strategy = GenerationType.SEQUENCE)
    private Integer swapId;
    @ManyToOne
    private Employee publisher;
    @ManyToOne
    private Employee receiver;
    @ManyToMany
    private List<Hour> hours;
    private LocalDate publishDate;
    @Enumerated(EnumType.STRING)
    private RequestStatusEnum status;
    private LocalDateTime targetDate;

    public Swap() {
    }

    public Integer getSwapId() {
        return swapId;
    }

    public void setSwapId(Integer swapId) {
        this.swapId = swapId;
    }

    public Employee getPublisher() {
        return publisher;
    }

    public void setPublisher(Employee publisher) {
        this.publisher = publisher;
    }

    public Employee getReceiver() {
        return receiver;
    }

    public void setReceiver(Employee receiver) {
        this.receiver = receiver;
    }

    public List<Hour> getHours() {
        return hours;
    }

    public void setHours(List<Hour> hours) {
        this.hours = hours;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public RequestStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RequestStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDateTime targetDate) {
        this.targetDate = targetDate;
    }
}
