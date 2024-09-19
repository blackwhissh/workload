package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "gift")
public class Gift {
    @Id
    @SequenceGenerator(name = "gift_id", sequenceName = "gift_id", allocationSize = 1)
    @GeneratedValue(generator = "gift_id", strategy = GenerationType.SEQUENCE)
    private Integer giftId;
    @ManyToOne
    private Employee publisher;
    @ManyToOne
    private Employee receiver;
    @ManyToMany
    private List<Hour> hours;
    private LocalDate giftDate;
    private LocalDate publishDate;
    @Enumerated(EnumType.STRING)
    private RequestStatusEnum status;

    public Gift() {
    }

    public Gift(Employee publisher, List<Hour> hours, LocalDate giftDate, LocalDate publishDate, RequestStatusEnum status) {
        this.publisher = publisher;
        this.hours = hours;
        this.giftDate = giftDate;
        this.publishDate = publishDate;
        this.status = status;
    }

    public Gift(Employee publisher, Employee receiver, List<Hour> hours, LocalDate giftDate, LocalDate publishDate, RequestStatusEnum status) {
        this.publisher = publisher;
        this.receiver = receiver;
        this.hours = hours;
        this.giftDate = giftDate;
        this.publishDate = publishDate;
        this.status = status;
    }

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
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

    public LocalDate getGiftDate() {
        return giftDate;
    }

    public void setGiftDate(LocalDate giftDate) {
        this.giftDate = giftDate;
    }
}
