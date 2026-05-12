package com.example.softguard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InsightResponse {
    private String timeRange;
    private String summary;
    private String suggestion;            // 운영 제안
    private Double conversionProbability; // 사고 전환 확률
    private Integer percentile;           // 상위 %
}