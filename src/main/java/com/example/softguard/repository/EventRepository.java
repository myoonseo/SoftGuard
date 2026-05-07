package com.example.softguard.repository;

import com.example.softguard.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreatedAtAfterOrderByCreatedAtAsc(LocalDateTime createdAtAfter);
}

