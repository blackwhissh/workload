package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.entity.enums.RotationAction;
import com.blackwhissh.workload.entity.enums.ShiftEnum;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @SequenceGenerator(name = "employee_id", sequenceName = "employee_id", allocationSize = 1)
    @GeneratedValue(generator = "employee_id", strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String phoneNumber;
    private String address;
    private String emergencyContact;
    private String position;
    private String pid;
    @OneToOne
    private User user;
    private String workId;
    @Enumerated(value = EnumType.STRING)
    private ShiftEnum shift;
    private int set;
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Schedule> scheduleList = new ArrayList<>();
    @OneToMany(orphanRemoval = true)
    private List<RotationItem> rotationItems = new ArrayList<>();

    public Employee() {
    }

    public Employee(String workId, ShiftEnum shift, int set) {
        this.workId = workId;
        this.shift = shift;
        this.set = set;
    }

    public Employee(String firstName, String lastName, String pid, LocalDate dob,
                    String phoneNumber, String address, String emergencyContact,
                    String position, String workId, ShiftEnum shift, int set) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.pid = pid;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.position = position;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
