package com.blackwhissh.workload.dto;

import java.time.LocalTime;

public record HourDTO(Integer id, LocalTime start, LocalTime end, boolean swapExists, boolean giftExists){

}
