package com.blackwhissh.workload.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record PublishSwapRequest(String publisherWorkId, LocalDate hourDay,
                                 LocalTime start, LocalTime end,
                                 LocalTime targetStart, LocalTime targetEnd) {
}
