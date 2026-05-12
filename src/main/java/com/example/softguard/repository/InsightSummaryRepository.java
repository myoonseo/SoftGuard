package com.example.softguard.repository;

import com.example.softguard.domain.InsightSummary;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface InsightSummaryRepository extends JpaRepository<InsightSummary, Long> {
    Optional<InsightSummary> findTopByOrderByCreatedAtDesc();
}
