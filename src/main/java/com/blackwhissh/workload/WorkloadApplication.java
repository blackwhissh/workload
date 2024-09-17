package com.blackwhissh.workload;

import com.blackwhissh.workload.dto.response.ScheduleByYearMonthResponse;
import com.blackwhissh.workload.entity.Schedule;
import com.blackwhissh.workload.repository.ScheduleRepository;
import com.blackwhissh.workload.service.HourService;
import com.blackwhissh.workload.service.ScheduleService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class WorkloadApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(WorkloadApplication.class, args);
    }

}
