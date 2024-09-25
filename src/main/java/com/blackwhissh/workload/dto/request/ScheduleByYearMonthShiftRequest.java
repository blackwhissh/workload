package com.blackwhissh.workload.dto.request;

import com.blackwhissh.workload.entity.enums.ShiftEnum;

public record ScheduleByYearMonthShiftRequest (int year, int month, ShiftEnum shift) {
}
