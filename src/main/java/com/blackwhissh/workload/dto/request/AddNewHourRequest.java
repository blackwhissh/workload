package com.blackwhissh.workload.dto.request;

import java.time.LocalTime;

public record AddNewHourRequest(Integer scheduleId, LocalTime start, LocalTime end){
}
