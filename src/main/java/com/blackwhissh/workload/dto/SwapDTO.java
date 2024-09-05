package com.blackwhissh.workload.dto;

import com.blackwhissh.workload.entity.enums.RequestStatusEnum;

import java.time.LocalDate;
import java.time.LocalTime;

public record SwapDTO(int swapId, String publisherWorkId, String receiverWorkId,
                      LocalDate hourDay, HourDTO hourDTO, LocalDate publishDate,
                      RequestStatusEnum status, LocalTime start, LocalTime end) {
}
