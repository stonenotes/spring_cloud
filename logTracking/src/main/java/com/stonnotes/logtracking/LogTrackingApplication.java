package com.stonnotes.logtracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: javan
 * @Date: 2019/6/19
 */
@SpringBootApplication
@EnableScheduling
public class LogTrackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogTrackingApplication.class, args);
    }
}
