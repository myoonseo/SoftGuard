package com.example.softguard.service;

import com.example.softguard.domain.Event;
import com.example.softguard.domain.InsightSummary;
import com.example.softguard.dto.InsightResponse;
import com.example.softguard.repository.EventRepository;
import com.example.softguard.repository.InsightSummaryRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightService {

    private final EventRepository eventRepository;
    private final InsightSummaryRepository insightSummaryRepository;
    private final HyperClovaXService hyperClovaXService;

    // ① 매 정시마다 자동 실행 (스케줄러) //우리 데이터 없어서 지금 코드 변경함.
//    @Scheduled(fixedDelay = 20000)
//    //@Scheduled(cron = "0 0 * * * *")
//    public void generateHourlySummary() {
//        log.info("[Scheduler] 1시간 요약 생성 시작");
//
//        LocalDateTime end = LocalDateTime.now();
//        LocalDateTime start = end.minusHours(1);
//
//        // DB에서 지난 1시간 이벤트 조회
//        List<Event> events = eventRepository.findEventsBetween(start, end);
//
//        if (events.isEmpty()) {
//            log.info("[Scheduler] 이벤트 없음 - 요약 생략");
//            return;
//        }
//
//        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
//        String timeRange = start.format(fmt) + "~" + end.format(fmt);
//
//        // 전처리 + 프롬프트 생성 + HCX-007 호출
//        String prompt = buildPrompt(events, start, end);
//        Map<String, String> result = hyperClovaXService.summarize(prompt);
//
//        // insight_summary 테이블에 저장
//        InsightSummary insightSummary = new InsightSummary();
//        insightSummary.setTimeRange(timeRange);
//        insightSummary.setStartTime(start);
//        insightSummary.setEndTime(end);
//        insightSummary.setSummary(result.get("summary"));
//        insightSummary.setSuggestion(result.get("suggestion"));
//        insightSummary.setEventCount(events.size());
//        insightSummaryRepository.save(insightSummary);
//
//        log.info("[Scheduler] {} 요약 저장 완료 (이벤트 {}건)", timeRange, events.size());
//    }

    // 시연용 - 현재 시간대 추적
//    private int currentHour = 7;  // 시작 시간
//
//    @Scheduled(fixedDelay = 20000)  // 2분마다 실행
//    public void generateHourlySummary() {
//        if (currentHour > 12) {
//            log.info("[Scheduler] 모든 시간대 요약 완료");
//            return;
//        }
//
//        log.info("[Scheduler] {}시 요약 생성 시작", currentHour);
//
//        String startStr = String.format("%02d:00:00", currentHour);
//        String endStr   = String.format("%02d:59:59", currentHour);
//
//        // ✅ time 컬럼 기준으로 조회
//        List<Event> events = eventRepository.findEventsBetween(startStr, endStr);
//
//        if (events.isEmpty()) {
//            log.info("[Scheduler] {}시 이벤트 없음 - 요약 생략", currentHour);
//            currentHour++;
//            return;
//        }
//
//        // ✅ buildPrompt용 LocalDateTime (날짜는 오늘 기준)
//        LocalDateTime start = LocalDateTime.now()
//                .withHour(currentHour).withMinute(0).withSecond(0);
//        LocalDateTime end = LocalDateTime.now()
//                .withHour(currentHour).withMinute(59).withSecond(59);
//
//        String timeRange = String.format("%02d:00~%02d:59", currentHour, currentHour);
//
//        String prompt = buildPrompt(events, start, end);
//        Map<String, String> result = hyperClovaXService.summarize(prompt);
//
//        InsightSummary insightSummary = new InsightSummary();
//        insightSummary.setTimeRange(timeRange);
//        insightSummary.setStartTime(start);
//        insightSummary.setEndTime(end);
//        insightSummary.setSummary(result.get("summary"));
//        insightSummary.setSuggestion(result.get("suggestion"));
//        insightSummary.setEventCount(events.size());
//        insightSummaryRepository.save(insightSummary);
//
//        log.info("[Scheduler] {} 요약 저장 완료 (이벤트 {}건)", timeRange, events.size());
//
//        currentHour++;  // 다음 시간대로 이동
//    }
    private int currentHour = 7;

    @Scheduled(fixedDelay = 30000)
    public void generateHourlySummary() {
        // ✅ 12시 넘으면 7시로 리셋 (무한 반복)
        if (currentHour > 12) {
            currentHour = 7;
            insightSummaryRepository.deleteAll();  // 기존 요약 초기화
            log.info("[Scheduler] 모든 시간대 완료 - 7시부터 다시 시작");
        }

        log.info("[Scheduler] {}시 요약 생성 시작", currentHour);

        String startStr = String.format("%02d:00:00", currentHour);
        String endStr   = String.format("%02d:59:59", currentHour);

        List<Event> events = eventRepository.findEventsBetween(startStr, endStr);

        if (events.isEmpty()) {
            log.info("[Scheduler] {}시 이벤트 없음 - 건너뜀", currentHour);
            currentHour++;
            return;
        }

//        LocalDateTime start = LocalDateTime.now()
//                .withHour(currentHour).withMinute(0).withSecond(0);
//        LocalDateTime end = LocalDateTime.now()
//                .withHour(currentHour).withMinute(59).withSecond(59);

        // ✅ buildPrompt용 LocalDateTime (날짜는 오늘 기준)
        LocalDateTime start = LocalDate.now().atTime(currentHour, 0, 0);
        LocalDateTime end = LocalDate.now().atTime(currentHour, 59, 59);

        String timeRange = String.format("%02d:00~%02d:59", currentHour, currentHour);

        String prompt = buildPrompt(events, start, end);
        Map<String, String> result = hyperClovaXService.summarize(prompt);

        InsightSummary insightSummary = new InsightSummary();
        insightSummary.setTimeRange(timeRange);
        insightSummary.setStartTime(start);
        insightSummary.setEndTime(end);
        insightSummary.setSummary(result.get("summary"));
        insightSummary.setSuggestion(result.get("suggestion"));
        insightSummary.setEventCount(events.size());
        insightSummaryRepository.save(insightSummary);

        log.info("[Scheduler] {} 요약 저장 완료 (이벤트 {}건)", timeRange, events.size());

        currentHour++;
    }
    // ② 프론트 요청 시 - 가장 최근 요약 반환
    public InsightResponse getLatestInsight() {
        InsightSummary latest = insightSummaryRepository
                .findTopByOrderByCreatedAtDesc()
                .orElse(null);

        if (latest == null) {
            return InsightResponse.builder()
                    .timeRange("-")
                    .summary("AI 분석 결과를 준비 중입니다.")
                    .build();
        }

        return InsightResponse.builder()
                .timeRange(latest.getTimeRange())
                .summary(latest.getSummary())
                .suggestion(latest.getSuggestion())
                .build();
    }

    // ③ 전처리 + 프롬프트 생성
    private String buildPrompt(List<Event> events, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        // Near-miss(warning) 건수
        long nearMissCount = events.stream()
                .filter(e -> e.getLevel().name().equalsIgnoreCase("warning"))
                .count();

        // 위험도별 건수
        Map<String, Long> levelCount = events.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getLevel().name().toLowerCase(),
                        Collectors.counting()
                ));

        // 반복 위치 Top 2
        Map<String, Long> locationCount = events.stream()
                .collect(Collectors.groupingBy(Event::getLocation, Collectors.counting()));
        String topLocations = locationCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(2)
                .map(e -> e.getKey() + " " + e.getValue() + "회")
                .collect(Collectors.joining(", "));

        // 반복 객체 조합 Top 2
        Map<String, Long> objectCount = events.stream()
                .filter(e -> e.getObject1() != null && e.getObject2() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getObject1() + "+" + e.getObject2(),
                        Collectors.counting()
                ));
        String topObjects = objectCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(2)
                .map(e -> e.getKey() + " " + e.getValue() + "건")
                .collect(Collectors.joining(", "));

        // 평균 차량 수 / 보행자 수
        double avgVehicle = events.stream()
                .filter(e -> e.getVehicleCount() != null)
                .mapToInt(Event::getVehicleCount)
                .average().orElse(0);
        double avgPedestrian = events.stream()
                .filter(e -> e.getPedestrianCount() != null)
                .mapToInt(Event::getPedestrianCount)
                .average().orElse(0);

        // 시나리오 목록 (각 Event의 summary)
        StringBuilder scenarios = new StringBuilder();
        for (int i = 0; i < events.size(); i++) {
            scenarios.append(String.format("%d. %s\n", i + 1, events.get(i).getSummary()));
        }

        // 최종 프롬프트
        return String.format("""
                다음은 최근 1시간(%s~%s) 동안 수집된 차량 위험 상황 시나리오 %d건이다.
                
                === 시나리오 ===
                %s
                === 공통 패턴 ===
                - Near-miss 발생 건수: %d건
                - 위험도: %s
                - 반복 위치: %s
                - 반복 객체 조합: %s
                - 평균 차량 수: %.1f대 / 평균 보행자 수: %.1f명
                
                아래 JSON 형식으로만 응답하라. JSON 외 다른 텍스트는 절대 포함하지 말 것:
                {
                  "summary": "...",
                  "suggestion": "..."
                }
                
                summary 작성 규칙:
                - 발생한 사실만 서술할 것
                - Near-miss 발생 건수 반드시 언급
                - 가장 많이 반복된 위험 위치 언급
                - 주요 충돌 객체 조합 언급 (예: 차량+보행자)
                - 핵심 위험 행동 언급
                - 절대로 권고, 제안, 조치, 강화, 필요 등의 표현을 사용하지 말 것
                - 3문장 이내
                
                suggestion 작성 규칙:
                - 분석된 위험 패턴을 바탕으로 구체적인 시설/운영 개선 제안
                - 신호등, 반사경, 조명, 안전시설 등 실질적 조치 중심
                - "~을 권고합니다", "~을 제안합니다" 형식으로 작성
                - 2문장 이내
                """,
                start.format(fmt), end.format(fmt),
                events.size(),
                scenarios,
                nearMissCount,
                levelCount.entrySet().stream()
                        .map(e -> e.getKey() + " " + e.getValue() + "건")
                        .collect(Collectors.joining(" / ")),
                topLocations,
                topObjects,
                avgVehicle,
                avgPedestrian
        );
    }
}
