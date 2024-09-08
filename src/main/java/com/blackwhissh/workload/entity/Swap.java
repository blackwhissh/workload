package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "swap")
public class Swap {
    @Id
    @SequenceGenerator(name = "swap_id", sequenceName = "swap_id", allocationSize = 1)
    @GeneratedValue(generator = "swap_id", strategy = GenerationType.SEQUENCE)
    private Integer swapId;
    @ManyToOne
    private Employee publisher;
    @ManyToOne
    private Employee receiver;
    private LocalDate hourDay;
    @OneToOne
    private Hour hour;
    private LocalDate publishDate;
    @Enumerated(EnumType.STRING)
    private RequestStatusEnum status;
    private LocalTime targetStartTime;
    private LocalTime targetEndTime;

    public Swap() {
    }

    public Swap(Employee publisher, LocalDate hourDay, Hour hour,
                LocalDate publishDate, RequestStatusEnum status,
                LocalTime start, LocalTime end) {
        this.publisher = publisher;
        this.hourDay = hourDay;
        this.hour = hour;
        this.publishDate = publishDate;
        this.status = status;
        this.targetStartTime = start;
        this.targetEndTime = end;
    }

    public Swap(Employee publisher, Employee receiver, LocalDate hourDay, Hour hour, LocalDate publishDate, RequestStatusEnum status) {
        this.publisher = publisher;
        this.receiver = receiver;
        this.hourDay = hourDay;
        this.hour = hour;
        this.publishDate = publishDate;
        this.status = status;
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

    public Hour getHour() {
        return hour;
    }

    public void setHour(Hour hour) {
        this.hour = hour;
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

    public LocalDate getHourDay() {
        return hourDay;
    }

    public void setHourDay(LocalDate hourDay) {
        this.hourDay = hourDay;
    }

    @Override
    public String toString() {
        return "SwapRequest{" +
                "swapRequestId=" + swapId +
                ", publisher=" + publisher +
                ", receiver=" + receiver +
                ", hourDay=" + hourDay +
                ", hour=" + hour +
                ", publishDate=" + publishDate +
                ", status=" + status +
                '}';
    }

    public LocalTime getEnd() {
        return targetEndTime;
    }

    public void setEnd(LocalTime end) {
        this.targetEndTime = end;
    }

    public LocalTime getStart() {
        return targetStartTime;
    }

    public void setStart(LocalTime start) {
        this.targetStartTime = start;
    }
}
