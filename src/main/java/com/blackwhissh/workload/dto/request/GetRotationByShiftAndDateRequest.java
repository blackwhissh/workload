package com.blackwhissh.workload.dto.request;

import com.blackwhissh.workload.entity.enums.ShiftEnum;

import java.time.LocalDate;

public record GetRotationByShiftAndDateRequest(ShiftEnum shift, LocalDate date) {
}
