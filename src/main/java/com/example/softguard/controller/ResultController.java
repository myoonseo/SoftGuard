package com.example.softguard.controller;


import com.example.softguard.domain.Event;
import com.example.softguard.domain.RiskLevel;
import com.example.softguard.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://localhost:3000")
public class ResultController {
    private final EventRepository eventRepository;

    // ③ AI 분석 결과 업로드
    // POST /api/results/upload
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadResult(@RequestBody Map<String, Object> payload) {
        try {
            log.info("[Result] AI 분석 결과 수신: {}", payload);

            // metadata
            Map<String, Object> metadata = (Map<String, Object>) payload.get("metadata");
            String videoId = (String) metadata.get("video_id");

            //event 추가
            Object eventIdObj = metadata.get("event_id");
            if (eventIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "event_id가 없습니다."
                ));
            }
            Long eventId = Long.valueOf(eventIdObj.toString());

            // event_stream
            Map<String, Object> eventStream = (Map<String, Object>) payload.get("event_stream");
            int riskLevel = (int) eventStream.get("risk_level");
            String locationText = (String) eventStream.get("location_type");
            List<String> involvedActors = (List<String>) eventStream.get("involved_actors");

            int vehicleCount = (int) eventStream.get("vehicleCount");
            int pedestrianCount = (int) eventStream.get("pedestrianCount");
            int pmCount = (int) eventStream.get("pmCount");

            // report_data
            Map<String, Object> reportData = (Map<String, Object>) payload.get("report_data");
            List<String> sequenceList = (List<String>) reportData.get("sequence_of_events");
            String rootCause = String.join("\n", sequenceList);

            // ✅ eventId로 정확히 찾기
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event를 찾을 수 없습니다: " + eventId));

            // video_url 기준으로 Event 찾기
            //Event event = eventRepository.findAll().stream()
             //       .filter(e -> e.getVideoUrl() != null && e.getVideoUrl().contains(videoId))
              //      .findFirst()
            //orElseThrow(() -> new RuntimeException("해당 영상의 Event를 찾을 수 없습니다: " + videoId));

            // (일단은)risk_level 숫자 → RiskLevel Enum 변환
            // 1~3: normal, 4~6: warning, 7~10: danger
            RiskLevel level;
            if (riskLevel >= 7) {
                level = RiskLevel.danger;
            } else if (riskLevel >= 4) {
                level = RiskLevel.warning;
            } else {
                level = RiskLevel.normal;
            }

            // Event 업데이트
            event.setLevel(level);
            event.setLocation(locationText);
            event.setSummary(rootCause);
            event.setVehicleCount(vehicleCount);
            event.setPedestrianCount(pedestrianCount);
            event.setPmCount(pmCount);

            // involved_actors → object1, object2
            if (involvedActors != null && involvedActors.size() >= 1) {
                event.setObject1(involvedActors.get(0));
            }
            if (involvedActors != null && involvedActors.size() >= 2) {
                event.setObject2(involvedActors.get(1));
            }

            eventRepository.save(event);

            log.info("[Result] Event 업데이트 완료 - videoId: {}, level: {}", videoId, level);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "분석 결과가 성공적으로 저장되었습니다.",
                    "videoId", videoId
            ));

        } catch (Exception e) {
            log.error("[Result] 업로드 오류", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
