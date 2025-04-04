package com.github.andradenathan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class CaseTecnicoMeetimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaseTecnicoMeetimeApplication.class, args);
    }

}
