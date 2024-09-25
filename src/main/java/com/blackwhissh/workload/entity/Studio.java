package com.blackwhissh.workload.entity;

import com.blackwhissh.workload.entity.enums.RotationAction;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "studio")
public class Studio {
    @Id
    @SequenceGenerator(name = "studio_id", sequenceName = "studio_id", allocationSize = 1)
    @GeneratedValue(generator = "studio_id", strategy = GenerationType.SEQUENCE)
    private Integer studioId;
    @OneToMany(mappedBy = "studio", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<RotationItem> rotationItemList = new ArrayList<>();
    private List<RotationAction> availableActions;
    private Boolean bonus;

    public Studio() {
    }

    public Integer getStudioId() {
        return studioId;
    }

    public void setStudioId(Integer studioId) {
        this.studioId = studioId;
    }

    public List<RotationItem> getRotationItemList() {
        return rotationItemList;
    }

    public void setRotationItemList(List<RotationItem> rotationItemList) {
        this.rotationItemList = rotationItemList;
    }

    public List<RotationAction> getAvailableActions() {
        return availableActions;
    }

    public void setAvailableActions(List<RotationAction> availableActions) {
        this.availableActions = availableActions;
    }

    public Boolean getBonus() {
        return bonus;
    }

    public void setBonus(Boolean bonus) {
        this.bonus = bonus;
    }
    public void addAction(RotationAction action) {
        this.availableActions.add(action);
    }
}
