package com.blackwhissh.workload.dto.request;

import java.time.LocalTime;

public record AddHourRequest (Integer scheduleId, LocalTime start, LocalTime end){
}
