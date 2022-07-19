package com.example.limit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringBootLimitRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootLimitRedisApplication.class, args);
    }

}
