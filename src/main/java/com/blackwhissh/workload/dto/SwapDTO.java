package com.blackwhissh.workload.dto;

import com.blackwhissh.workload.entity.enums.RequestStatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record SwapDTO(int swapId, String publisherWorkId, String receiverWorkId,
                      List<HourDTO> hourDTO, LocalDate swapDate,
                      LocalDate publishDate, RequestStatusEnum status,
                      LocalDate targetDate, LocalTime targetStart, LocalTime targetEnd) {
}
