package com.example.softguard.service;


import com.example.softguard.domain.Event;
import com.example.softguard.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    //public List<Event> getAllEvents(){
       // return eventRepository.findAll();
    //}
    public List<Event> getEvents(
            String location,
            String level,
            String from,
            String to,
            int limit
    ){
        //return eventRepository.findAll(); // 일단 이걸로 테스트
        return eventRepository.findBySummaryIsNotNullOrderByCreatedAtDesc(); //수정
    }

}
