package com.blackwhissh.workload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class WorkloadApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(WorkloadApplication.class, args);
    }

}
