package com.blackwhissh.workload.dto.response;

import com.blackwhissh.workload.dto.HourDTO;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PublishSwapResponse(int swapId, String publisherWorkId, String receiverWorkId,
                                  List<HourDTO> hours, LocalDate swapDate, LocalDate publishDate,
                                  RequestStatusEnum status, LocalDate targetDate,
                                  LocalTime targetStart, LocalTime targetEnd) {
}
