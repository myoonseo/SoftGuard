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
        /*@Bean
        public CommandLineRunner initData(EventRepository repository) {
            return args -> {
                // Test data
                Event event1 = new Event();
                event1.setTime("16:03:43");
                event1.setLevel(RiskLevel.danger);
                event1.setLocation("남문 사거리");
                event1.setObject1("우회전 차량");
                event1.setObject2("보행자");
                event1.setVehicleCount(1);
                event1.setPedestrianCount(2);
                event1.setAction("경고 송출");
                repository.save(event1);

                //
                Event event2 = new Event();
                event2.setTime("16:10:20");
                event2.setLevel(RiskLevel.warning);
                event2.setLocation("팔달문 로터리");
                event2.setObject1("오토바이");
                event2.setObject2("버스");
                event2.setVehicleCount(5);
                event2.setPedestrianCount(0);
                event2.setAction("주의 알림");
                repository.save(event2);

                System.out.println(">>> 테스트 데이터 2건 저장 완료! <<<");
            };

        }*/
}
