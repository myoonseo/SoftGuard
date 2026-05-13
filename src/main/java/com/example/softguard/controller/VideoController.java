package com.example.softguard.controller;


import com.example.softguard.domain.Event;
import com.example.softguard.dto.VideoUploadRequest;
import com.example.softguard.repository.EventRepository;
import com.example.softguard.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    // ① 영상 업로드 API
    // 카메라/디바이스에서 S3에 영상 업로드 후 관련 데이터를 백엔드에 전송
    // POST /api/videos/upload
    @PostMapping("/upload")
    @Transactional
    public ResponseEntity<?> uploadVideo(@RequestBody VideoUploadRequest request) {
        try {
            log.info("[Video] 영상 업로드 요청 - videoUrl: {}, level: {}", request.getVideoUrl(), request.getLevel());

            // videoUrl 필수 체크
            if (request.getVideoUrl() == null || request.getVideoUrl().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "videoUrl은 필수 항목입니다."
                ));
            }

            // Event 객체 생성
            // DTO에서 받은 값들을 Event 객체에 담아서 DB에 저장
            Event event = new Event();

            // DTO에서 받은 값 설정
            event.setVideoUrl(request.getVideoUrl());
            event.setThumbnailUrl(request.getThumbnailUrl()); //썸네일 관련
            event.setLevel(request.getRiskLevel());      // "danger" → RiskLevel.danger 자동 변환
            event.setState(request.getState());
            event.setAction(request.getAction()); //행동
            event.setLocation(request.getLocation()); //위치
            event.setVehicleCount(request.getVehicleCount() != null ? request.getVehicleCount() : 0);
            event.setPedestrianCount(request.getPedestrianCount() != null ? request.getPedestrianCount() : 0);
            event.setPmCount(request.getPmCount() != null ? request.getPmCount() : 0);
            event.setCollisionTime(request.getCollisionTime());

            // 자동 설정 값
            event.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))); // 현재 시각
            event.setLocation("분석 전"); // AI 분석 후 채워짐

            // DB에 저장
            Event savedEvent = eventRepository.save(event);

            log.info("[Video] Event 저장 완료 - eventId: {}", savedEvent.getId());

            // 저장된 eventId와 videoUrl 반환
            // AI 서버가 이 eventId를 받아서 나중에 분석 결과 업로드할 때 사용
            return ResponseEntity.ok(Map.of(
                    "eventId", savedEvent.getId(),
                    "videoUrl", savedEvent.getVideoUrl()
            ));

        } catch (Exception e) {
            log.error("[Video] 업로드 오류", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // ② 분석 대기 중인 영상 목록 조회 분석 대기 중인 영상 목록 조회
    // GET /api/videos/pending
    @GetMapping("/pending")
    public List<Map<String, Object>> getPendingVideos() {
        List<Event> pendingEvents = eventRepository.findPendingEvents();

        log.info("[Video] 분석 대기 중인 영상 {}건 조회", pendingEvents.size());

        return pendingEvents.stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("eventId", e.getId());
                    map.put("videoUrl", e.getVideoUrl());
                    return map;
                })
                .collect(Collectors.toList());

    }

    // ③ 영상 다운로드 (S3 Presigned URL 반환)
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
