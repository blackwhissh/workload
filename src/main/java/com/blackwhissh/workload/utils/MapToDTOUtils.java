package com.blackwhissh.workload.utils;

import com.blackwhissh.workload.dto.*;
import com.blackwhissh.workload.entity.*;

import java.util.ArrayList;
import java.util.List;

public class MapToDTOUtils {

    public static HourDTO mapHourToDTO(Hour hour) {
        return new HourDTO(
                hour.getId(),
                hour.getStart(),
                hour.getEnd(),
                hour.getSwapExists(),
                hour.getGiftExists()
        );
    }

    public static SwapDTO mapSwapToDTO(Swap swap) {
        String receiverWorkId = "0";
        if (swap.getReceiver() != null) {
            receiverWorkId = swap.getReceiver().getWorkId();
        }
        List<HourDTO> hourDTOList = new ArrayList<>();
        for (Hour hour : swap.getHours()) {
            hourDTOList.add(mapHourToDTO(hour));
        }
        return new SwapDTO(
                swap.getSwapId(),
                swap.getPublisher().getWorkId(),
                receiverWorkId,
                hourDTOList,
                swap.getSwapDate(),
                swap.getPublishDate(),
                swap.getStatus(),
                swap.getTargetDate(),
                swap.getTargetStart(),
                swap.getTargetEnd()
        );
    }

    public static GiftDTO mapGiftToDTO(Gift gift) {
        String receiverWorkId = "0";
        if (gift.getReceiver() != null) {
            receiverWorkId = gift.getReceiver().getWorkId();
        }
        List<HourDTO> hourDTOList = new ArrayList<>();
        for (Hour hour : gift.getHours()) {
            hourDTOList.add(mapHourToDTO(hour));
        }
        return new GiftDTO(
                gift.getGiftId(),
                gift.getPublisher().getWorkId(),
                receiverWorkId,
                gift.getGiftDate(),
                gift.getPublishDate(),
                gift.getStatus(),
                hourDTOList
        );
    }

    public static RotationItemDTO mapRotationItemToDTO(RotationItem rotationItem) {
        List<HourDTO> hourDTOList = new ArrayList<>();
        if (!rotationItem.getEmployeeRotationHours().isEmpty()) {
            for (Hour hour : rotationItem.getEmployeeRotationHours()) {
                hourDTOList.add(mapHourToDTO(hour));
            }
        }
        return new RotationItemDTO(
                rotationItem.getRotationItemId(),
                rotationItem.getStudio().getStudioId(),
                rotationItem.getEmployee().getWorkId(),
                hourDTOList,
                rotationItem.getRotationAction(),
                rotationItem.getUniform()
        );
    }

    public static RotationDTO mapRotationToDTO(Rotation rotation) {
        List<RotationItemDTO> rotationDTOS = new ArrayList<>();
        if (!rotation.getRotationItems().isEmpty()) {
            for (RotationItem rotationItem : rotation.getRotationItems()) {
                rotationDTOS.add(mapRotationItemToDTO(rotationItem));
            }
        }
        return new RotationDTO(
                rotation.getRotationId(),
                rotationDTOS,
                rotation.getRotationDate(),
                rotation.getRotationShift()
        );
    }
}
