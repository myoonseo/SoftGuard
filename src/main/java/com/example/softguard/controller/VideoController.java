package com.example.softguard.controller;


import com.example.softguard.domain.Event;
import com.example.softguard.repository.EventRepository;
import com.example.softguard.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/videos")
@CrossOrigin(origins = "http://localhost:3000")
public class VideoController {
    private final EventRepository eventRepository;
    private final S3Service s3Service;

    // ① 분석 대기 중인 영상 목록 조회
    // GET /api/videos/pending
    @GetMapping("/pending")
    public List<Map<String, Object>> getPendingVideos() {
        List<Event> pendingEvents = eventRepository.findPendingEvents();

        return pendingEvents.stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("eventId", e.getId());
                    map.put("videoUrl", e.getVideoUrl());
                    return map;
                })
                .collect(Collectors.toList());

    }

    // ② 영상 다운로드 (S3 Presigned URL 반환)
    // GET /api/videos/download/{fileName}
    @GetMapping("/download/{fileName}")
    public Map<String, String> downloadVideo(@PathVariable String fileName) {
        log.info("[Video] 다운로드 요청: {}", fileName);
        String presignedUrl = s3Service.generatePresignedUrl(fileName);
        return Map.of(
                "fileName", fileName,
                "downloadUrl", presignedUrl
        );
    }
}
