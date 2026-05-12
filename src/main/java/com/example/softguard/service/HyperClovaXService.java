package com.example.softguard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Slf4j
@Service
public class HyperClovaXService {

    @Value("${clova.api.key}")
    private String apiKey;

    @Value("${clova.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> summarize(String userPrompt) {
        try {
            // 메시지 구성
            List<Map<String, String>> messages = new ArrayList<>();

            // System 메시지 - HCX-007 역할 부여 및 제약 강화
            messages.add(Map.of(
                    "role", "system",
                    "content", "당신은 도로 안전 관제 시스템의 AI 분석가입니다. " +
                            "차량 위험 상황 데이터를 분석하여 관제 대시보드용 통합 인사이트를 생성합니다. " +
                            "반드시 한국어로 답변하세요. " +
                            "반드시 '~했습니다', '~됩니다' 등의 격식체(합쇼체)로 작성하세요. " +
                            "요청한 JSON 형식으로만 응답하고 다른 텍스트는 절대 포함하지 마세요."
            ));

            // User 메시지 - 실제 데이터
            messages.add(Map.of(
                    "role", "user",
                    "content", userPrompt
            ));

            // 요청 바디 구성
            Map<String, Object> body = new HashMap<>();
            body.put("messages", messages);
            body.put("max_tokens", 500);
            body.put("temperature", 0.3);
            body.put("top_p", 0.8);

            String requestBody = objectMapper.writeValueAsString(body);

            // HTTP 요청
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("[HCX] 응답 status: {}", response.statusCode());
            log.info("[HCX] 응답 body: {}", response.body());

            if (response.statusCode() == 200) {
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                Map<String, Object> result = (Map<String, Object>) responseMap.get("result");
                Map<String, Object> message = (Map<String, Object>) result.get("message");
                String content = (String) message.get("content");

                // JSON 파싱
                return parseJsonResponse(content);
            } else {
                log.error("[HCX] API 오류: {} - {}", response.statusCode(), response.body());
                return fallbackResponse("요약 생성 실패", "운영 제안 생성 실패");
            }

        } catch (Exception e) {
            log.error("[HCX] 호출 오류", e);
            return fallbackResponse("요약 생성 중 오류 발생", "운영 제안 생성 중 오류 발생");
        }
    }

    // JSON 응답 파싱
    private Map<String, String> parseJsonResponse(String content) {
        try {
            // ```json ... ``` 형식으로 올 경우 제거
            String cleaned = content
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            Map<String, Object> parsed = objectMapper.readValue(cleaned, Map.class);

            Map<String, String> result = new HashMap<>();
            result.put("summary", (String) parsed.getOrDefault("summary", "요약 생성 실패"));
            result.put("suggestion", (String) parsed.getOrDefault("suggestion", "운영 제안 생성 실패"));
            return result;

        } catch (Exception e) {
            log.error("[HCX] JSON 파싱 오류: {}", content, e);
            return fallbackResponse("요약 파싱 실패", "운영 제안 파싱 실패");
        }
    }

    // 오류 발생 시 기본값 반환
    private Map<String, String> fallbackResponse(String summary, String suggestion) {
        Map<String, String> result = new HashMap<>();
        result.put("summary", summary);
        result.put("suggestion", suggestion);
        return result;
    }
}
