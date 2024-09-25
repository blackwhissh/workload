package com.blackwhissh.workload.service;

import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.Rotation;
import com.blackwhissh.workload.entity.RotationItem;
import com.blackwhissh.workload.entity.enums.ShiftEnum;
import com.blackwhissh.workload.exceptions.list.RotationNotFoundException;
import com.blackwhissh.workload.repository.HourRepository;
import com.blackwhissh.workload.repository.RotationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RotationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RotationService.class);
    private final RotationRepository rotationRepository;
    private final HourRepository hourRepository;

    public RotationService(RotationRepository rotationRepository, HourRepository hourRepository) {
        this.rotationRepository = rotationRepository;
        this.hourRepository = hourRepository;
    }

    public Rotation createRotation(ShiftEnum shift, LocalDate rotationDate) {
        LOGGER.info("Started creation of rotation");
        Rotation rotation = new Rotation();

        rotation.setRotationItems(new ArrayList<>());
        rotation.setRotationDate(rotationDate);
        rotation.setRotationShift(shift);
        return rotationRepository.save(rotation);
    }

    public Rotation getRotationByShiftAndDate(ShiftEnum shiftEnum, LocalDate rotationDate) {
        LOGGER.info("Started retrieving rotation by shift and date");
        return rotationRepository.findByRotationShiftAndRotationDate(shiftEnum, rotationDate)
                .orElseThrow(RotationNotFoundException::new);
    }
}
