package com.example.softguard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChartsResponseDto {
    private List<HourlyBucketDto> nearMissByHour;
    private List<LabelValueDto> incidentTypeRatio;
    private List<LabelValueDto> incidentsByWeekday; //요일별


}
