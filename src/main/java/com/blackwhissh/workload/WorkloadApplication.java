package com.blackwhissh.workload;

import com.blackwhissh.workload.dto.response.ScheduleByYearMonthResponse;
import com.blackwhissh.workload.entity.Rotation;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.entity.enums.RotationAction;
import com.blackwhissh.workload.entity.enums.ShiftEnum;
import com.blackwhissh.workload.entity.enums.Uniform;
import com.blackwhissh.workload.repository.ScheduleRepository;
import com.blackwhissh.workload.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.blackwhissh.workload.utils.MapToDTOUtils.mapRotationToDTO;

@SpringBootApplication
public class WorkloadApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.
                run(WorkloadApplication.class, args);
        RotationService rotationService = run.getBean(RotationService.class);
        Rotation rotation = rotationService.createRotation(ShiftEnum.MORNING, LocalDate.now().plusDays(4));

        RotationItemService rotationItemService = run.getBean(RotationItemService.class);
        rotationItemService.addRotationItem(rotation, "001", LocalTime.of(8,0),
                LocalTime.of(10,0), RotationAction.HALF_BREAK,1, Uniform.BLACK);


        SwapService swapService = run.getBean(SwapService.class);
        GiftService giftService = run.getBean(GiftService.class);
        swapService.publishSwap("003",LocalDate.now().plusDays(8), LocalTime.of(8,0),LocalTime.of(10,0), LocalDate.now().plusDays(3),
                LocalTime.of(5,0), LocalTime.of(7,0), Optional.empty());

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                swapService.filterNonReceivedSwaps();
                giftService.filterNonReceivedGifts();
            }
        }, 0, 120000);

    }

}
