package com.blackwhissh.workload.dto;

import com.blackwhissh.workload.entity.enums.RequestStatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record SwapDTO(int swapId, String publisherWorkId, String receiverWorkId,
                      LocalDate publishDate, List<HourDTO> hourDTO,
                      RequestStatusEnum status,  LocalDate targetDate,
                      LocalTime targetStart, LocalTime targetEnd) {
}
