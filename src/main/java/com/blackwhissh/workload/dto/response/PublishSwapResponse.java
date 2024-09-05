package com.blackwhissh.workload.dto.response;

import com.blackwhissh.workload.dto.HourDTO;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;

import java.time.LocalDate;
import java.time.LocalTime;

public record PublishSwapResponse(int requestId, String publisherWorkId, LocalDate hourDay,
                                  HourDTO hour, LocalDate publishDate, LocalTime start, LocalTime end,
                                  RequestStatusEnum status) {
}
