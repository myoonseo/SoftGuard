package com.example.softguard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HourlyBucketDto {
    private String bucket;  // "16", "17" 형식
    private Long count;
}
