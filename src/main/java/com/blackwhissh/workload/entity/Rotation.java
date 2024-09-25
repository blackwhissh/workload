package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.entity.enums.ShiftEnum;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rotation")
public class Rotation {
    @Id
    @SequenceGenerator(name = "rotation_id", sequenceName = "rotation_id", allocationSize = 1)
    @GeneratedValue(generator = "rotation_id", strategy = GenerationType.SEQUENCE)
    private Integer rotationId;
    @OneToMany(mappedBy = "rotation", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<RotationItem> rotationItems;
    private LocalDate rotationDate;
    private ShiftEnum rotationShift;

    public Integer getRotationId() {
        return rotationId;
    }

    public void setRotationId(Integer rotationId) {
        this.rotationId = rotationId;
    }

    public List<RotationItem> getRotationItems() {
        return rotationItems;
    }

    public void setRotationItems(List<RotationItem> rotationItems) {
        this.rotationItems = rotationItems;
    }

    public LocalDate getRotationDate() {
        return rotationDate;
    }

    public void setRotationDate(LocalDate rotationDate) {
        this.rotationDate = rotationDate;
    }

    public ShiftEnum getRotationShift() {
        return rotationShift;
    }

    public void setRotationShift(ShiftEnum rotationShift) {
        this.rotationShift = rotationShift;
    }
}
