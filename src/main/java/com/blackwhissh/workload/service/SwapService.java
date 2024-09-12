//package com.blackwhissh.workload.service;
//
//import com.blackwhissh.workload.dto.GiftDTO;
//import com.blackwhissh.workload.dto.SwapDTO;
//import com.blackwhissh.workload.dto.request.AcceptSwapHourRequest;
//import com.blackwhissh.workload.entity.Employee;
//import com.blackwhissh.workload.entity.Gift;
//import com.blackwhissh.workload.entity.Hour;
//import com.blackwhissh.workload.entity.Swap;
//import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
//import com.blackwhissh.workload.exceptions.list.EmployeeNotFoundException;
//import com.blackwhissh.workload.exceptions.list.HourSwapException;
//import com.blackwhissh.workload.repository.ScheduleRepository;
//import com.blackwhissh.workload.repository.SwapRequestRepository;
//import com.blackwhissh.workload.utils.MapToDTOUtils;
//import jakarta.transaction.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.blackwhissh.workload.utils.MapToDTOUtils.mapSwapToDTO;
//
//@Service
//public class SwapService {
//    private final static Logger LOGGER = LoggerFactory.getLogger(SwapService.class);
//    private final SwapRequestRepository swapRepository;
//    private final ScheduleRepository scheduleRepository;
//
//    public SwapService(SwapRequestRepository swapRepository, ScheduleRepository scheduleRepository) {
//        this.swapRepository = swapRepository;
//        this.scheduleRepository = scheduleRepository;
//    }
//
//    @Transactional
//    public SwapDTO publishSwap (String workId, List<Integer> hourIdList) {
//        LOGGER.info("Started to publish gift");
//        Employee publisher = employeeRepository.findByWorkId(workId)
//                .orElseThrow(EmployeeNotFoundException::new);
//
//        List<Hour> hourList = new ArrayList<>();
//        if (!isValidMinimumLimit(hourIdList.get(0), hourIdList.size())) {
//            LOGGER.error("Can not be gifted, monthly hours will be less than 40");
//        }
//        LOGGER.info("Started searching hours");
//        LocalDate giftDate = LocalDate.now();
//        for (int hourId : hourIdList) {
//            Hour hour = hourRepository.findById(hourId).orElseThrow();
//            if (hour.getGiftExists() || hour.getSwapExists()) {
//                LOGGER.error("One or more hour is already is being gifted/swapped");
//                throw new HourSwapException();
//            }
//            giftDate = hour.getSchedule().getDate();
//            hour.setGiftExists(true);
//            hourList.add(hour);
//        }
//
//        Gift saved = giftRepository.save(new Gift(
//                publisher,
//                hourList,
//                giftDate,
//                LocalDate.now(),
//                RequestStatusEnum.ACTIVE
//        ));
//        LOGGER.info("Gift saved successfully");
//        return MapToDTOUtils.mapGiftToDTO(saved);
//    }
//
//    public List<SwapDTO> listAllSwaps() {
//        LOGGER.info("Started listing all swaps");
//        List<Swap> allSwaps = swapRepository.findAll();
//        LOGGER.info("Finished listing swaps");
//        List<SwapDTO> allDTO = new ArrayList<>();
//        for (Swap swap : allSwaps) {
//            allDTO.add(mapSwapToDTO(swap));
//        }
//        return allDTO;
//    }
//
//    public List<SwapDTO> listSwapsByStatus(RequestStatusEnum requestStatusEnum) {
//        LOGGER.info("Started listing swaps by status: " + requestStatusEnum);
//        List<Swap> allByStatus = swapRepository.findAllByStatusIsLike(requestStatusEnum);
//        LOGGER.info("Finished listing swaps by status: " + requestStatusEnum);
//        List<SwapDTO> allByStatusDTO = new ArrayList<>();
//        for (Swap swap : allByStatus) {
//            allByStatusDTO.add(mapSwapToDTO(swap));
//        }
//        return allByStatusDTO;
//    }
//
//    public void acceptSwap(AcceptSwapHourRequest request) {
//
//    }
//}
