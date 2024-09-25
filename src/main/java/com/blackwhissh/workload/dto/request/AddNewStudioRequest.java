package com.blackwhissh.workload.dto.request;

import com.blackwhissh.workload.entity.enums.RotationAction;

import java.util.List;

public record AddNewStudioRequest(List<RotationAction> actions, Boolean bonus) {
}
