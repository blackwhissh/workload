//package com.blackwhissh.workload.service;
//
//import com.blackwhissh.workload.dto.HourDTO;
//import com.blackwhissh.workload.dto.SwapDTO;
//import com.blackwhissh.workload.dto.request.AcceptSwapHourRequest;
//import com.blackwhissh.workload.dto.request.PublishSwapRequest;
//import com.blackwhissh.workload.dto.response.PublishSwapResponse;
//import com.blackwhissh.workload.entity.Hour;
//import com.blackwhissh.workload.entity.Schedule;
//import com.blackwhissh.workload.entity.Swap;
//import com.blackwhissh.workload.entity.enums.RequestStatusEnum;
//import com.blackwhissh.workload.exceptions.list.FirstOrLastHourSwapException;
//import com.blackwhissh.workload.exceptions.list.HourSwapException;
//import com.blackwhissh.workload.exceptions.list.ScheduleNotFoundException;
//import com.blackwhissh.workload.exceptions.list.SwapRequestsExistsException;
//import com.blackwhissh.workload.repository.ScheduleRepository;
//import com.blackwhissh.workload.repository.SwapRequestRepository;
//import jakarta.transaction.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Optional;
//
//import static com.blackwhissh.workload.utils.MapToDTOUtils.mapHourToDTO;
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
//    @Transactional
//    public PublishSwapResponse publishSwap(PublishSwapRequest request) {
//        LOGGER.info("Started publish swap method");
////        if (swapRepository.existsByHour_Id(request.hourId())) {
////            LOGGER.error("Swap request for this hour already exists!");
////            throw new SwapRequestsExistsException();
////        }
//        Optional<Schedule> schedule = scheduleRepository.findByDateAndEmployee_WorkId(request.hourDay(), request.publisherWorkId());
//        if (schedule.isEmpty()) {
//            LOGGER.error("Schedule not found");
//            throw new ScheduleNotFoundException();
//        }
//        List<Hour> hours = schedule.get().getHours();
//        hours.sort(new Comparator<Hour>() {
//            @Override
//            public int compare(Hour o1, Hour o2) {
//                return o1.getId() - o2.getId();
//            }
//        });
//        if (hours.get(0).getId() == request.hourId() || hours.get(hours.size() - 1).getId() == request.hourId()) {
//            LOGGER.error("First or last hour of a day can not be swapped");
//            throw new FirstOrLastHourSwapException();
//        }
//        for (int i = 1; i < hours.size() - 1; i++) {
//            if (hours.get(i).getId() == request.hourId()) {
//                LOGGER.info("Hour found");
//                HourDTO hourDTO = mapHourToDTO(hours.get(i));
//                Swap swapRequest = new Swap(
//                        schedule.get().getEmployee(),
//                        schedule.get().getDate(),
//                        hours.get(i),
//                        LocalDate.now(),
//                        RequestStatusEnum.ACTIVE,
//                        request.start(),
//                        request.end());
//                swapRepository.save(swapRequest);
//                LOGGER.info("Swap request published successfully");
//                hours.get(i).setSwapExists(true);
//                schedule.get().setHours(hours);
//                LOGGER.info("Hour swap status updated successfully");
//                scheduleRepository.save(schedule.get());
//                return new PublishSwapResponse(
//                        swapRequest.getSwapId(),
//                        swapRequest.getPublisher().getWorkId(),
//                        swapRequest.getHourDay(),
//                        hourDTO,
//                        swapRequest.getPublishDate(),
//                        swapRequest.getStart(),
//                        swapRequest.getEnd(),
//                        swapRequest.getStatus());
//            }
//        }
//        LOGGER.error("Error during publishing swap request");
//        throw new HourSwapException();
//    }
//
//    public void acceptSwap(AcceptSwapHourRequest request) {
//
//    }
//}
