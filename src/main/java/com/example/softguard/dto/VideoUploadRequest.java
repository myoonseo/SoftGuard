package com.example.softguard.dto;

import com.example.softguard.domain.RiskLevel;
import lombok.Getter;

@Getter
public class VideoUploadRequest {

    // S3에 저장된 영상 URL
    // 예: "https://softguard-videos.s3.ap-northeast-2.amazonaws.com/영상.mp4"
    private String videoUrl;

    //S3에 저장된 썸네일 이미지 URL
    private String thumbnailUrl;

    private String action;
    private String time; // VideoUploadRequest.java에 추가

    //위치
    private String location;

    // 위험 단계 (문자열로 받아서 Enum으로 변환)
    // 예: "danger", "warning", "normal"
    private String level;

    // 위험 상황 유형
    // 예: "우회전 차량 ↔ 보행자 충돌 위험"
    private String state;

    // 감지된 차량 수
    private Integer vehicleCount;

    // 감지된 보행자 수
    private Integer pedestrianCount;

    // 감지된 PM(전동킥보드) 수
    private Integer pmCount;

    // 예상 충돌 시간
    // 예: "2.3sec"
    private String collisionTime;

    // level 문자열 → RiskLevel Enum 변환 메서드
    // "danger" → RiskLevel.danger 자동 변환
    public RiskLevel getRiskLevel() {
        try {
            return RiskLevel.valueOf(level.toLowerCase());
        } catch (Exception e) {
            return RiskLevel.normal; // 변환 실패 시 기본값
        }
    }
}
