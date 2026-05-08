package com.example.softguard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatsResponseDto {
    private Long nearMissToday;
    private Double dangerRatio;
    //private Double conversionProbability;  // 아직 미구현
    private Double nightRatio;
}
