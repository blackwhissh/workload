package com.blackwhissh.workload.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PublishGiftRequest(LocalDate giftDate, LocalTime start, LocalTime end) {
}
