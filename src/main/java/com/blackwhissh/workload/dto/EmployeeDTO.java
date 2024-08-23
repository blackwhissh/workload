package com.blackwhissh.workload.dto;

import com.blackwhissh.workload.entity.enums.ShiftEnum;

public record EmployeeDTO (Integer id, String workId, ShiftEnum shift, int set){
}
