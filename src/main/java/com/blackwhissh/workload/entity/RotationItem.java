package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.entity.enums.RotationAction;
import com.blackwhissh.workload.entity.enums.ShiftEnum;
import com.blackwhissh.workload.entity.enums.Uniform;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "rotation_item")
public class RotationItem {
    @Id
    @SequenceGenerator(name = "rotation_item_id", sequenceName = "rotation_item_id", allocationSize = 1)
    @GeneratedValue(generator = "rotation_item_id", strategy = GenerationType.SEQUENCE)
    private Integer rotationItemId;
    @ManyToOne
    private Studio studio;
    @ManyToOne
    private Rotation rotation;
    @ManyToOne
    private Employee employee;
    @OneToMany(mappedBy = "rotationItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Hour> employeeRotationHours;
    @Enumerated(EnumType.STRING)
    private RotationAction rotationAction;
    @Enumerated(EnumType.STRING)
    private Uniform uniform;


    public Integer getRotationItemId() {
        return rotationItemId;
    }

    public void setRotationItemId(Integer rotationItemId) {
        this.rotationItemId = rotationItemId;
    }

    public Studio getStudio() {
        return studio;
    }

    public void setStudio(Studio studio) {
        this.studio = studio;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<Hour> getEmployeeRotationHours() {
        return employeeRotationHours;
    }

    public void setEmployeeRotationHours(List<Hour> employeeRotationHours) {
        this.employeeRotationHours = employeeRotationHours;
    }

    public RotationAction getRotationAction() {
        return rotationAction;
    }

    public void setRotationAction(RotationAction rotationAction) {
        this.rotationAction = rotationAction;
    }

    public Uniform getUniform() {
        return uniform;
    }

    public void setUniform(Uniform uniform) {
        this.uniform = uniform;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        return "RotationItem{" +
                "rotationItemId=" + rotationItemId +
                ", studio=" + studio +
                ", rotation=" + rotation +
                ", employee=" + employee +
                ", employeeRotationHours=" + employeeRotationHours +
                ", rotationAction=" + rotationAction +
                ", uniform=" + uniform +
                '}';
    }
}
