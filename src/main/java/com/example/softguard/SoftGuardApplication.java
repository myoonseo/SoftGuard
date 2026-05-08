package com.example.softguard;

import com.example.softguard.domain.Event;
import com.example.softguard.domain.RiskLevel;
import com.example.softguard.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class SoftGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoftGuardApplication.class, args);
    }

}
