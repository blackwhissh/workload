package com.blackwhissh.workload.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rotation")
public class Rotation {
    @Id
    private Integer rotationId;
}
