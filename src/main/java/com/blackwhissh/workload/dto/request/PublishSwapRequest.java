package com.blackwhissh.workload.dto.request;

import com.blackwhissh.workload.entity.Hour;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PublishSwapRequest(LocalDate swapDate,LocalTime start,
                                 LocalTime end, LocalDate targetDate,
                                 LocalTime targetStart, LocalTime targetEnd) {
}
