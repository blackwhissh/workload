package com.blackwhissh.workload.dto.request;

import com.blackwhissh.workload.entity.enums.RotationAction;
import com.blackwhissh.workload.entity.enums.Uniform;

import java.time.LocalTime;

public record AddRotationItemRequest(int rotationId, String employeeWorkId,
                                     LocalTime start, LocalTime end,
                                     RotationAction action, int studioId,
                                     Uniform uniform) {
}
