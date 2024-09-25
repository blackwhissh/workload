package com.blackwhissh.workload.service;

import com.blackwhissh.workload.dto.HourDTO;
import com.blackwhissh.workload.entity.*;
import com.blackwhissh.workload.entity.enums.RotationAction;
import com.blackwhissh.workload.entity.enums.ShiftEnum;
import com.blackwhissh.workload.entity.enums.StatusEnum;
import com.blackwhissh.workload.entity.enums.Uniform;
import com.blackwhissh.workload.exceptions.list.*;
import com.blackwhissh.workload.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static com.blackwhissh.workload.utils.MapToDTOUtils.mapHourToDTO;

@Service
public class RotationItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RotationItemService.class);
    private final EmployeeRepository employeeRepository;
    private final HourRepository hourRepository;
    private final StudioRepository studioRepository;
    private final RotationItemRepository rotationItemRepository;
    private final RotationRepository rotationRepository;
    private final ScheduleRepository scheduleRepository;

    public RotationItemService(EmployeeRepository employeeRepository, HourRepository hourRepository,
                               StudioRepository studioRepository, RotationItemRepository rotationItemRepository,
                               RotationRepository rotationRepository, ScheduleRepository scheduleRepository) {
        this.employeeRepository = employeeRepository;
        this.hourRepository = hourRepository;
        this.studioRepository = studioRepository;
        this.rotationItemRepository = rotationItemRepository;
        this.rotationRepository = rotationRepository;
        this.scheduleRepository = scheduleRepository;
    }

    private static void checkEmployeeHours(List<Hour> employeeHours, Employee employee) {
        for (Hour hour : employeeHours) {
            if (hour.getSwapExists() || hour.getGiftExists()) {
                LOGGER.error("Hour is either being swapped or gifted");
                throw new HourGiftException();
            }
            if (hour.getSchedule().getEmployee() != employee) {
                LOGGER.error("Hours does not belong to this employee");
                throw new HoursDoesNotBelongToEmployeeException();
            }
            if (hour.getRotationItem() != null) {
                LOGGER.error("Hour is already added in another rotation");
                throw new HourIsInRotationException();
            }
        }
    }

    @Transactional
    public RotationItem addRotationItem(Rotation rotation, String employeeWorkId,
                                        LocalTime start, LocalTime end, RotationAction rotationAction,
                                        Integer studioId, Uniform uniform) {
        LOGGER.info("Started adding item (employee) to rotation");
        Employee employee = employeeRepository.findByWorkId(employeeWorkId)
                .orElseThrow(EmployeeNotFoundException::new);


        Optional<Schedule> employeeSchedule = Optional.empty();
        for (Schedule schedule : employee.getScheduleList()) {
            if (schedule.getDate().isEqual(rotation.getRotationDate())) {
                employeeSchedule = Optional.of(schedule);
            }
        }
        if (employeeSchedule.isEmpty()) {
            LOGGER.error("Current employee does not work at that day");
            throw new ScheduleNotFoundException();
        }
        List<Hour> employeeHours = hourRepository.findBetweenStartEndAndSchedule_Id(start, end, employeeSchedule.get().getScheduleId());
        Studio studio = checkAndGetStudio(rotationAction, studioId);


        checkEmployeeHours(employeeHours, employee);
        RotationItem rotationItem = new RotationItem();
        rotationItem.setEmployee(employee);
        rotationItem.setEmployeeRotationHours(employeeHours);
        rotationItem.setRotationAction(rotationAction);
        rotationItem.setStudio(studio);
        rotationItem.setUniform(uniform);
        rotationItem.setRotation(rotation);
        RotationItem save = rotationItemRepository.save(rotationItem);
        rotation.getRotationItems().add(rotationItem);
        rotationRepository.save(rotation);
        for (Hour hour : employeeHours) {
            hour.setRotationItem(rotationItem);
        }
        hourRepository.saveAll(employeeHours);
        LOGGER.info("Rotation item saved successfully!");
        return save;
    }

    private Studio checkAndGetStudio(RotationAction rotationAction, Integer studioId) {
        Studio studio = studioRepository.findById(studioId).orElseThrow(StudioNotFoundException::new);
        if (!studio.getAvailableActions().contains(rotationAction)) {
            throw new WrongTableProvidedForStudioException();
        }
        return studio;
    }

    public RotationItem editRotationItem(int rotationItemId, Optional<LocalTime> start, Optional<LocalTime> end,
                                         Optional<RotationAction> action, Optional<Uniform> uniform) {
        LOGGER.info("Started editing rotation item (employee)");
        RotationItem rotationItem = rotationItemRepository.findById(rotationItemId)
                .orElseThrow(RotationItemNotFoundException::new);

        if (start.isPresent() && end.isPresent()) {
            LOGGER.info("Editing rotation item's hours");
            Schedule schedule = rotationItem.getEmployeeRotationHours().get(0).getSchedule();
            List<Hour> newHours = hourRepository.findBetweenStartEndAndSchedule_Id(start.get(), end.get(), schedule.getScheduleId());
            checkEmployeeHours(newHours, schedule.getEmployee());
            rotationItem.setEmployeeRotationHours(newHours);
            for (Hour hour : newHours) {
                hour.setRotationItem(rotationItem);
            }
            for (Hour hour : rotationItem.getEmployeeRotationHours()) {
                hour.setRotationItem(null);
            }
            hourRepository.saveAll(newHours);
            hourRepository.saveAll(rotationItem.getEmployeeRotationHours());
        }

        if (action.isPresent()) {
            LOGGER.info("Editing rotation item's action");
            checkAndGetStudio(action.get(), rotationItem.getStudio().getStudioId());
            rotationItem.setRotationAction(action.get());
        }

        if (uniform.isPresent()) {
            LOGGER.info("Editing rotation item's uniform");
            rotationItem.setUniform(uniform.get());
        }

        return rotationItemRepository.save(rotationItem);
    }

    public void deleteRotationItem(int rotationItemId) {
        LOGGER.info("Started to empty rotation item (employee)");
        RotationItem rotationItem = rotationItemRepository.findById(rotationItemId)
                .orElseThrow(RotationItemNotFoundException::new);
        List<Hour> employeeRotationHours = rotationItem.getEmployeeRotationHours();
        List<Hour> list = employeeRotationHours.stream().peek(hour -> hour.setRotationItem(null)).toList();
        rotationItem.setEmployee(null);
        rotationItemRepository.save(rotationItem);
        hourRepository.saveAll(list);
        LOGGER.info("Rotation item (employee) emptied successfully!");
    }

//    public void getEmployeeWithHoursForRotation(ShiftEnum rotationShift, LocalDate rotationDate) {
//        LocalTime shiftStartHour = getShiftStartHour(rotationShift);
//        LocalTime shiftEndHour = getShiftEndHour(rotationShift);
//        List<Schedule> scheduleList = scheduleRepository.findAllByDateAndWorkStatus(rotationDate, StatusEnum.WORK);
//        List<Hour> filteredHourList = new ArrayList<>();
//        for (Schedule schedule : scheduleList) {
//            schedule.getHours().forEach(hour -> {
//                if (hour.getStart().equals(shiftStartHour))
//            });
//        }
//    }

//    public EmployeeDTO getEmployeesForCurrentShift(ShiftEnum rotationShift, LocalDate rotationDate) {
//        List<Schedule> currentRotationSchedule = getCurrentRotationSchedule(rotationDate);
//        List<Hour> hours = filterHoursWithShift(currentRotationSchedule, rotationShift);
//
//    }

    private List<Schedule> getCurrentRotationSchedule(LocalDate rotationDate) {
        LOGGER.info("Retrieving schedule for current rotation");
        return scheduleRepository.findAllByDateAndWorkStatus(rotationDate, StatusEnum.WORK);
    }

    private List<Hour> filterHoursWithShift(List<Schedule> schedules, ShiftEnum shift) {
        LOGGER.info("Filtering hours for provided shift");
        LocalTime shiftStartHour = getShiftStartHour(shift);
        LocalTime shiftEndHour = getShiftEndHour(shift);
        List<Hour> hours = new ArrayList<>();
        for (Schedule schedule : schedules) {
            for (Hour hour : schedule.getHours()) {
                if (hour.getRotationItem() == null) {
                    if (hour.getEnd().equals(LocalTime.of(0,0))){
                        hour.setEnd(LocalTime.of(23,59));
                    }
                    if ((hour.getStart().equals(shiftStartHour) || hour.getStart().isAfter(shiftStartHour)) &&
                            (hour.getEnd().equals(shiftEndHour) || hour.getEnd().isBefore(shiftEndHour))) {
                        hours.add(hour);
                    }
                }
            }
        }
        return hours;
    }

    public Map<String, List<HourDTO>> getEmployeesWithHoursForCurrentShiftRotation(ShiftEnum shiftEnum, LocalDate date){
        LOGGER.info("Started retrieving employee with hours for provided shift rotation");
        List<Schedule> currentShiftSchedule = scheduleRepository.findAllByDateAndWorkStatus(date, StatusEnum.WORK);
        List<Hour> currentShiftHours = filterHoursWithShift(currentShiftSchedule, shiftEnum);
        HashMap<String, List<HourDTO>> employeeHoursHashMap = new HashMap<>();
        if (!currentShiftHours.isEmpty()) {
            LOGGER.info("Pairing hours and employees");
            for (Hour hour : currentShiftHours) {
                employeeHoursHashMap.computeIfAbsent(hour.getSchedule().getEmployee().getWorkId(), k -> new ArrayList<>()).add(mapHourToDTO(hour));
            }
            return employeeHoursHashMap;
        }
        return Map.of();
    }

    public LocalTime getShiftStartHour(ShiftEnum rotationShift) {
        if (rotationShift.equals(ShiftEnum.MORNING)) {
            return LocalTime.of(8, 0);
        } else if (rotationShift.equals(ShiftEnum.DAY)) {
            return LocalTime.of(16, 0);
        } else {
            return LocalTime.of(0, 0);
        }
    }

    public LocalTime getShiftEndHour(ShiftEnum rotationShift) {
        if (rotationShift.equals(ShiftEnum.MORNING)) {
            return LocalTime.of(16, 0);
        } else if (rotationShift.equals(ShiftEnum.DAY)) {
            return LocalTime.of(0, 0);
        } else {
            return LocalTime.of(8, 0);
        }
    }
}