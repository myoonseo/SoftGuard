package com.example.softguard.controller;

import com.example.softguard.dto.InsightResponse;
import com.example.softguard.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/insights")
@CrossOrigin(origins = "http://localhost:3000")
public class InsightController {
    private final InsightService insightService;

    // 프론트 명세서: GET /api/insights/latest
    @GetMapping("/latest")
    public InsightResponse getLatestInsight() {
        return insightService.getLatestInsight();
    }
}
