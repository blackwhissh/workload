package com.blackwhissh.workload.service;

import com.blackwhissh.workload.entity.Studio;
import com.blackwhissh.workload.entity.enums.RotationAction;
import com.blackwhissh.workload.exceptions.list.StudioNotFoundException;
import com.blackwhissh.workload.repository.StudioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudioService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudioService.class);
    private final StudioRepository studioRepository;

    public StudioService(StudioRepository studioRepository) {
        this.studioRepository = studioRepository;
    }

    public List<Studio> getAllStudios() {
        LOGGER.info("Started retrieving all studios");
        return studioRepository.findAll();
    }

    public List<RotationAction> getStudioActionsByStudioId(int studioId) {
        LOGGER.info("Started retrieving current studio actions");
        Studio studio = studioRepository.findById(studioId).orElseThrow(StudioNotFoundException::new);
        return studio.getAvailableActions();
    }

    public Studio addNewStudio(List<RotationAction> actions, Boolean bonus) {
        LOGGER.info("Started adding new studio");
        Studio studio = new Studio();
        studio.setBonus(bonus);
        studio.setAvailableActions(actions);
        return studioRepository.save(studio);
    }
}
