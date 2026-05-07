package com.example.softguard.service;

import com.example.softguard.domain.Event;
import com.example.softguard.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventScheduler {
    private final EventRepository eventRepository;
    private final SseEmitterService sseEmitterService;

    private LocalDateTime lastChecked = LocalDateTime.now();

    @Scheduled(fixedDelay = 3000) // 3초마다 체크 -> 우리가 지정한 시간 대로!!!
    public void checkNewEvents() {
        List<Event> newEvents = eventRepository
                .findByCreatedAtAfterOrderByCreatedAtAsc(lastChecked);

        if (!newEvents.isEmpty()) {
            lastChecked = LocalDateTime.now();
            for (Event event : newEvents) {
                sseEmitterService.sendToAll(event);
            }
        }
    }
}
