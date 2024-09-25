package com.blackwhissh.workload.dto;

import com.blackwhissh.workload.entity.enums.RotationAction;
import com.blackwhissh.workload.entity.enums.Uniform;

import java.util.List;

public record RotationItemDTO (int rotationItemId, int studioId, String workId,
                               List<HourDTO> hourList, RotationAction action,
                               Uniform uniform) {

}
