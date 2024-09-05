package com.blackwhissh.workload.dto;

import com.blackwhissh.workload.entity.enums.RequestStatusEnum;

import java.time.LocalDate;
import java.util.List;

public record GiftDTO(int giftId, String publisherWorkId, String receiverWorkId, LocalDate giftDate,
                      LocalDate publishDate, RequestStatusEnum status, List<HourDTO> hours) {
}
