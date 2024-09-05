package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.GiftDTO;
import com.blackwhissh.workload.dto.request.PublishGiftRequest;
import com.blackwhissh.workload.entity.Employee;
import com.blackwhissh.workload.entity.Gift;
import com.blackwhissh.workload.entity.Hour;
import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
import com.blackwhissh.workload.exceptions.list.EmployeeNotFoundException;
import com.blackwhissh.workload.exceptions.list.HourSwapException;
import com.blackwhissh.workload.repository.EmployeeRepository;
import com.blackwhissh.workload.repository.GiftRepository;
import com.blackwhissh.workload.repository.HourRepository;
import com.blackwhissh.workload.utils.MapToDTOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GiftService {
    private final static Logger LOGGER = LoggerFactory.getLogger(GiftService.class);
    private final GiftRepository giftRepository;
    private final EmployeeRepository employeeRepository;
    private final HourRepository hourRepository;
    private final HourService hourService;

    public GiftService(GiftRepository giftRepository, EmployeeRepository employeeRepository, HourRepository hourRepository, HourService hourService) {
        this.giftRepository = giftRepository;
        this.employeeRepository = employeeRepository;
        this.hourRepository = hourRepository;
        this.hourService = hourService;
    }

    public GiftDTO publishGift(PublishGiftRequest request){
        LOGGER.info("Started to publish gift");
        System.out.println(request.publisherWorkId());
        Employee publisher = employeeRepository.findByWorkId(request.publisherWorkId())
                .orElseThrow(EmployeeNotFoundException::new);

        List<Hour> hourList = new ArrayList<>();
        List<Integer> hourIds = request.hourIdList();
        if (!isValidMinimumLimit(hourIds.get(0), hourIds.size())){
            LOGGER.error("Can not be gifted, monthly hours will be less than 40");
        }
        LOGGER.info("Started searching hours");
        LocalDate giftDate = LocalDate.now();
        for (int hourId : hourIds) {
            Hour hour = hourRepository.findById(hourId).orElseThrow();
            if (hour.getGiftExists() || hour.getSwapExists()) {
                LOGGER.error("One or more hour is already is being gifted/swapped");
                throw new HourSwapException();
            }
            giftDate = hour.getSchedule().getDate();
            hour.setGiftExists(true);
            hourList.add(hour);
        }

        Gift saved = giftRepository.save(new Gift(
                publisher,
                hourList,
                giftDate,
                LocalDate.now(),
                RequestStatusEnum.ACTIVE
        ));
        LOGGER.info("Gift saved successfully");
        return MapToDTOUtils.mapGiftToDTO(saved);
    }

    public List<GiftDTO> getGiftsByWorkId(String workId) {
        LOGGER.info("Started search for gifts by workId");
        Employee publisher = employeeRepository.findByWorkId(workId).orElseThrow(EmployeeNotFoundException::new);
        List<Gift> allByPublisher = giftRepository.findAllByPublisher(publisher);
        List<GiftDTO> allGiftDTO = new ArrayList<>();
        allByPublisher.forEach(gift -> allGiftDTO.add(MapToDTOUtils.mapGiftToDTO(gift)));
        return allGiftDTO;
    }

    public boolean isValidMinimumLimit(int hourId, int hourAmount) {
        LOGGER.info("Validating monthly minimum limit");
        Hour hour = hourRepository.findById(hourId).orElseThrow();
        double monthHours = hourService.getMonthHours(hour.getSchedule());
        return monthHours - hourAmount >= 40;
    }
}
