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
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Hour> hours;
    private LocalDate swapDate;
    private LocalDate publishDate;
    @Enumerated(EnumType.STRING)
    private RequestStatusEnum status;
    private LocalDate targetDate;
    private LocalTime targetStart;
    private LocalTime targetEnd;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Hour> receiverHours;

    public Swap() {
    }

    public Swap(Employee publisher, List<Hour> hours, LocalDate swapDate,
                LocalDate publishDate, RequestStatusEnum status,
                LocalDate targetDate, LocalTime targetStart,
                LocalTime targetEnd) {
        this.publisher = publisher;
        this.hours = hours;
        this.swapDate = swapDate;
        this.publishDate = publishDate;
        this.status = status;
        this.targetDate = targetDate;
        this.targetStart = targetStart;
        this.targetEnd = targetEnd;
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

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public LocalTime getTargetStart() {
        return targetStart;
    }

    public void setTargetStart(LocalTime targetStart) {
        this.targetStart = targetStart;
    }

    public LocalTime getTargetEnd() {
        return targetEnd;
    }

    public void setTargetEnd(LocalTime targetEnd) {
        this.targetEnd = targetEnd;
    }

    public LocalDate getSwapDate() {
        return swapDate;
    }

    public void setSwapDate(LocalDate swapDate) {
        this.swapDate = swapDate;
    }

    public List<Hour> getReceiverHours() {
        return receiverHours;
    }

    public void setReceiverHours(List<Hour> receiverHours) {
        this.receiverHours = receiverHours;
    }
}
