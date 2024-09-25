package com.blackwhissh.workload.dto.request;

import com.blackwhissh.workload.entity.enums.RotationAction;
import com.blackwhissh.workload.entity.enums.Uniform;

import java.time.LocalTime;
import java.util.Optional;

public record EditRotationItemRequest(int rotationItemId, Optional<LocalTime> start,
                                      Optional<LocalTime> end, Optional<RotationAction> action,
                                      Optional<Uniform> uniform) {
}
