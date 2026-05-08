package com.example.softguard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LabelValueDto {
    private String label;
    private Double value;
}
