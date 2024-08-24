package com.blackwhissh.workload.dto.response;

import com.blackwhissh.workload.dto.EmployeeDTO;
import com.blackwhissh.workload.dto.HourDTO;
import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.enums.StatusEnum;

import java.time.LocalDate;
import java.util.List;

public record ScheduleByYearMonthResponse (Integer scheduleId, EmployeeDTO employee,
                                           StatusEnum workStatus, LocalDate date,
                                           List<HourDTO> hours, Double totalHours){
}
