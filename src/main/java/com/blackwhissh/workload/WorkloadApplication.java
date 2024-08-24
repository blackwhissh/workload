package com.blackwhissh.workload;

import com.blackwhissh.workload.service.WorkloadService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WorkloadApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(WorkloadApplication.class, args);
        WorkloadService workloadService = run.getBean(WorkloadService.class);
//		workloadService.createSchedule("admin", 2024, 2);
//		workloadService.addWorkDay(1,1,"day_off", new ArrayList<>());
    }

}
