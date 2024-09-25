package com.blackwhissh.workload.dto.request;

import com.blackwhissh.workload.entity.enums.ShiftEnum;

import java.time.LocalDate;

public record GetEmployeesForCurrentShiftRotationRequest(ShiftEnum shift, LocalDate date)  {
}
