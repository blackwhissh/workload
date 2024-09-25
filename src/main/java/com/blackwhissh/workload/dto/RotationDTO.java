package com.blackwhissh.workload.dto;

import com.blackwhissh.workload.entity.enums.ShiftEnum;

import java.time.LocalDate;
import java.util.List;

public record RotationDTO (int rotationId, List<RotationItemDTO> rotationItems, LocalDate rotationDate, ShiftEnum shift){
}
