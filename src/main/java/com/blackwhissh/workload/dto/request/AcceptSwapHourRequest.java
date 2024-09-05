package com.blackwhissh.workload.dto.request;

import com.blackwhissh.workload.dto.HourDTO;

import java.time.LocalDate;

public record AcceptSwapHourRequest(int requestId, String receiverWorkId, LocalDate hourDay, HourDTO hourDTO) {
}
