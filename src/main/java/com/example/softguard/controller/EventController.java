package com.example.softguard.controller;

import com.example.softguard.domain.Event;
import com.example.softguard.domain.RiskLevel;
import com.example.softguard.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.softguard.service.SseEmitterService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
@CrossOrigin(origins= "http://localhost:3000")
//@CrossOrigin(origins= "*")
public class EventController {
    private final EventService eventService;
    private final SseEmitterService sseEmitterService;

    @GetMapping("/stream")
    public List<Event> getEventStream( // 여기서 summaryr가 없으면 넘겨지지XX
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return eventService.getEvents(location, level, from, to, limit);
    }

    //@GetMapping(value = "/stream/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
   // public SseEmitter subscribe() {
        //return sseEmitterService.createEmitter();
    //}
}

